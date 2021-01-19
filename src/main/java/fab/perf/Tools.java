package fab.perf;

import java.util.Base64;

public class Tools {
    public static final String GATEWAY_HOST = "http://localhost:9997";

    // number of accounts to create
    public static final int NUM_ACCOUNTS = 10;

    // total number of requests to send
    public static final int MAX_REQS = 1000;

    // number of threads to use to perform the test, set to 0 to use cpu_count
    public static final int NUM_THREADS = 4;

    // print the results from each requests
    public static final boolean enablePrint = true;

    // path to public and privates used in the test
    public static final String PUB_KEY_PATH = "openssl/publickey.pem";
    public static final String PRV_KEY_PATH = "openssl/privatekey-pkcs8.pem";

    public static String publicKeyAsString() throws Exception {
        return Base64.getEncoder().encodeToString(OpenSsl.loadPublicKeyFromFile(PUB_KEY_PATH).getEncoded());
    }

    public static int cpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }
}
