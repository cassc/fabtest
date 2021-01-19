package fab.perf;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static fab.perf.Tools.*;

public class SendTest {
    private static AtomicInteger numFailed = new AtomicInteger(0);
    private static AtomicInteger idStore = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final int num_threads = NUM_THREADS > 0 ? NUM_THREADS : cpuCount();
        ExecutorService exec = Executors.newFixedThreadPool(num_threads);
        final CountDownLatch latch = new CountDownLatch(MAX_REQS);
        System.out.println("Sending " + MAX_REQS + " send requests using " + num_threads + " threads");
        long start = System.currentTimeMillis();
        for (int i = 0; i < MAX_REQS; i++) {
            final String from = "a" + (i % NUM_ACCOUNTS);
            final String to = "a" + ((i+1) % NUM_ACCOUNTS);
            double amount = 88.88;
            exec.execute(() -> {
                try{
                    send(from, to, amount);
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        exec.shutdown();

        long end = System.currentTimeMillis();
        long duration = end - start;
        double speed = (end - start) / 1.0 / MAX_REQS;
        System.out.println(String.format("%d requests completed in %d ms, speed %.3f ms/req, %d requests failed",
                MAX_REQS,
                duration,
                speed,
                numFailed.get()
        ));
    }

    private static void send(String from, String to, double amount) {
        try{
            String id = String.valueOf(nextId());
            String timestamp = String.valueOf(System.currentTimeMillis());
            String s = String.format("%s%s%s%s%s%s%s%f", id, timestamp,
                    "fromId", from,
                    "toId", to,
                    "amount", amount);
            String sig = OpenSsl.sign(s, OpenSsl.loadPrivateKeyFromFile(PRV_KEY_PATH));
            URI uri = new URIBuilder("http://localhost:9997/send")
                    .addParameter("fromId", from)
                    .addParameter("toId", to)
                    .addParameter("amount", String.valueOf(amount))
                    .addParameter("id", id)
                    .addParameter("timestamp", timestamp)
                    .addParameter("signature", sig)
                    .build();
            HttpPost req = new HttpPost(uri);

            CloseableHttpClient client = HttpClients.createDefault();

            CloseableHttpResponse resp = client.execute(req);
            HttpEntity entity = resp.getEntity();
            String r = EntityUtils.toString(entity);

            JSONObject obj = new JSONObject(r);
            boolean success = Objects.equals(obj.getString("code"), "OK");
            if (!success){
                numFailed.incrementAndGet();
            }

            if (enablePrint){
                System.out.println(String.format("Send %.3f from %s to %s returns %s", amount, from, to, r));
            }

        }catch (Exception e){
            numFailed.incrementAndGet();
            e.printStackTrace();
            System.err.println("Send error: " + e.getMessage());
        }
    }


    private static int nextId() {
        return idStore.incrementAndGet();
    }
}
