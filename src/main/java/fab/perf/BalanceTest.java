package fab.perf;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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

public class BalanceTest {
    private static AtomicInteger numFailed = new AtomicInteger(0);
    private static AtomicInteger idStore = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final int num_threads = NUM_THREADS > 0 ? NUM_THREADS : cpuCount();
        ExecutorService exec = Executors.newFixedThreadPool(num_threads);
        final CountDownLatch latch = new CountDownLatch(MAX_REQS);
        System.out.println("Sending " + MAX_REQS + " get-balance requests using " + num_threads + " threads");
        long start = System.currentTimeMillis();
        for (int i = 0; i < MAX_REQS; i++) {
            String accountId = "a"+ (i % NUM_ACCOUNTS) ;
            exec.execute(() -> {
                try{
                    getBalance(accountId);
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



    private static void getBalance(String accountId) {
        try{
            String id = String.valueOf(nextId());
            String timestamp = String.valueOf(System.currentTimeMillis());
            String s = String.format("%s%s%s%s", id, timestamp, "accountId", accountId);
            String sig = OpenSsl.sign(s, OpenSsl.loadPrivateKeyFromFile(PRV_KEY_PATH));
            URI uri = new URIBuilder(GATEWAY_HOST + "/balance")
                    .addParameter("accountId", accountId)
                    .addParameter("id", id)
                    .addParameter("timestamp", timestamp)
                    .addParameter("signature", sig)
                    .build();
            HttpGet req = new HttpGet(uri);

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
                System.out.println(String.format("get balance of %s returns %s", accountId, r));
            }
        }catch (Exception e){
            numFailed.incrementAndGet();
            e.printStackTrace();
            System.err.println("Get balance error: " + e.getMessage());
        }
    }

    private static int nextId() {
        return idStore.incrementAndGet();
    }
}

