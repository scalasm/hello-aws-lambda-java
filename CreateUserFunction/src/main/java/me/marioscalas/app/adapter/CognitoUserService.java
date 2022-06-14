package me.marioscalas.app.adapter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.amazonaws.util.Base64;

import me.marioscalas.app.core.service.CreateUserRequest;
import me.marioscalas.app.core.service.CreateUserResponse;
import me.marioscalas.app.core.service.UserService;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

public class CognitoUserService implements UserService {

    private final CognitoConfig cognitoConfig;

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    public CognitoUserService(CognitoConfig config) {
        this.cognitoConfig = config;
        
        this.cognitoIdentityProviderClient = config.getCognitoIdentityProviderClient();
    }

    @Override
    public CreateUserResponse createUser(final CreateUserRequest request) {
        final String userId = UUID.randomUUID().toString();
        final AttributeType userIdAttribute = AttributeType.builder()
            .name("custom:userId")
            .value(userId)
            .build();

        final AttributeType emailAttribute = AttributeType.builder()
            .name("email")
            .value(request.getEmail())
            .build();

        final AttributeType nameAttribute = AttributeType.builder()
            .name("name")
            .value(request.getFirstName() + " " + request.getLastName())
            .build();

        final String generatedSecretHash = calculateSecretHash(
            cognitoConfig.getAppClientId(), 
            cognitoConfig.getAppClientSecret(), 
            request.getEmail()
        );

        final SignUpRequest cognitoSignUpRequest = SignUpRequest.builder()
            .username(request.getEmail())
            .password(request.getPassword())
            .userAttributes(
                userIdAttribute, emailAttribute, nameAttribute
            )
            .clientId(cognitoConfig.getAppClientId())
            .secretHash(generatedSecretHash)
            .build();

        final SignUpResponse signUp = cognitoIdentityProviderClient.signUp(cognitoSignUpRequest);

        return CreateUserResponse.builder()
            .isSuccessful(signUp.sdkHttpResponse().isSuccessful())
            .statusCode(signUp.sdkHttpResponse().statusCode())
            .cognitoUserId(signUp.userSub())
            .isConfirmed(signUp.userConfirmed())
            .build();
    }

    // From https://docs.aws.amazon.com/cognito/latest/developerguide/signing-up-users-in-your-app.html
    private static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
        
        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeAsString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
}
