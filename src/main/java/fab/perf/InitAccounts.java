package fab.perf;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;

import static fab.perf.Tools.NUM_ACCOUNTS;
import static fab.perf.Tools.publicKeyAsString;

public class InitAccounts {

    public static void main(String[] args) throws Exception {
        System.out.println("Creating " + NUM_ACCOUNTS + " accounts ...");
        for (int i = 0; i < NUM_ACCOUNTS; i++) {
            URI uri = new URIBuilder("http://localhost:9997/account")
                    .addParameter("accountId", "a"+i)
                    .addParameter("balance", String.valueOf(9989889.898))
                    .addParameter("publicKey", publicKeyAsString())
                    .build();
            HttpPut req = new HttpPut(uri);

            CloseableHttpClient client = HttpClients.createDefault();

            CloseableHttpResponse resp = client.execute(req);
            HttpEntity entity = resp.getEntity();

            System.out.println(EntityUtils.toString(entity));

        }
    }


}
