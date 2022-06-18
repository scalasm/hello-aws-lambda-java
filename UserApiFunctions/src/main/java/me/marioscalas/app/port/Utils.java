package me.marioscalas.app.port;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.util.Base64;

public class Utils {
    /**
     * Helper method that fetches and decrypts the value for the specified environment variable
     * using AWS KMS.
     * @param name the environment variable's name
     * @return the decrypted value 
     */
    public static String getenv(String name) {
        final byte[] encryptedKey = Base64.decode(System.getenv(name));

        // Since I am not using the Lambda console to encrypt environment variables' values,
        // I don't need to set a context (the lambda function's name) - I will leave this here,
        // commented out, for future reference!

    //    Map<String, String> encryptionContext = new HashMap<>();
    //    encryptionContext.put("LambdaFunctionName",
    //            System.getenv("AWS_LAMBDA_FUNCTION_NAME"));

        final AWSKMS client = AWSKMSClientBuilder.defaultClient();

        final DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey));
                //.withEncryptionContext(encryptionContext);

        final ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), Charset.forName("UTF-8")).trim();
    }
}
