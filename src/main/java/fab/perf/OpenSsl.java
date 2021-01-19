package fab.perf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

// Use openssl keys in Java
// https://adangel.org/2016/08/29/openssl-rsa-java/
public class OpenSsl {

    public static PublicKey loadPublicKeyFromFile(String path) throws Exception {
        String publicKeyPEM = Files.readString(Path.of(path));

        // strip of header, footer, newlines, whitespaces
        publicKeyPEM = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        return loadPublicKey(publicKeyPEM);
    }

    public static PublicKey loadPublicKey(String b64key) throws Exception {
        // decode to get the binary DER representation
        byte[] publicKeyDER = Base64.getDecoder().decode(b64key);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyDER));
        return publicKey;
    }

    public static PrivateKey loadPrivateKeyFromFile(String path) throws Exception {
        String privateKeyPEM = Files.readString(Path.of(path));


        // strip of header, footer, newlines, whitespaces
        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        return loadPrivateKey(privateKeyPEM);
    }

    public static PrivateKey loadPrivateKey(String b64key) throws Exception {
        // decode to get the binary DER representation
        byte[] privateKeyDER = Base64.getDecoder().decode(b64key);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDER));
        return privateKey;
    }

    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();

        return Base64.getEncoder().encodeToString(signature);
    }

    public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }




}
