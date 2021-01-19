package fab.perf;

import junit.framework.TestCase;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

import static fab.perf.Tools.*;
import static fab.perf.OpenSsl.*;

public class OpenSslTest extends TestCase {
    @Test
    public void testSignature() throws Exception {
        PublicKey pub = loadPublicKeyFromFile(PUB_KEY_PATH);
        PrivateKey prv = loadPrivateKeyFromFile(PRV_KEY_PATH);

        String plainText = "hello world";
        String sig = sign(plainText, prv);


        assertTrue("Signature verification fail", verify(plainText, sig, pub));
    }

}