package me.marioscalas.app.adapter;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.amazonaws.util.Base64;

import me.marioscalas.app.core.service.AddUserToGroupRequest;
import me.marioscalas.app.core.service.AddUserToGroupResponse;
import me.marioscalas.app.core.service.ConfirmUserSignUpRequest;
import me.marioscalas.app.core.service.ConfirmUserSignUpResponse;
import me.marioscalas.app.core.service.CreateUserRequest;
import me.marioscalas.app.core.service.CreateUserResponse;
import me.marioscalas.app.core.service.MyGetUserRequest;
import me.marioscalas.app.core.service.MyGetUserResponse;
import me.marioscalas.app.core.service.LoginUserRequest;
import me.marioscalas.app.core.service.LoginUserResponse;
import me.marioscalas.app.core.service.UserService;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;

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

    @Override
    public ConfirmUserSignUpResponse confirmUserSignUp(ConfirmUserSignUpRequest request) {
        final String generatedSecretHash = calculateSecretHash(
            cognitoConfig.getAppClientId(), 
            cognitoConfig.getAppClientSecret(), 
            request.getEmail()
        );

        final ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
            .secretHash(generatedSecretHash)
            .username(request.getEmail())
            .confirmationCode(request.getConfirmationCode())
            .clientId(cognitoConfig.getAppClientId())
            .build();

        final ConfirmSignUpResponse confirmSignUpResponse = cognitoIdentityProviderClient.confirmSignUp(confirmSignUpRequest);

        return ConfirmUserSignUpResponse.builder()
            .isSuccessful(confirmSignUpResponse.sdkHttpResponse().isSuccessful())
            .statusCode(confirmSignUpResponse.sdkHttpResponse().statusCode())
            .build();
    }

    @Override
    public LoginUserResponse loginUser(LoginUserRequest request) {
        final String generatedSecretHash = calculateSecretHash(
            cognitoConfig.getAppClientId(), 
            cognitoConfig.getAppClientSecret(), 
            request.getEmail()
        );

        final Map<String,String> authParameters = Map.of(
            "USERNAME", request.getEmail(), 
            "PASSWORD", request.getPassword(), 
            "SECRET_HASH", generatedSecretHash
        );
        
        final InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
            .clientId(cognitoConfig.getAppClientId())
            .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
            .authParameters(authParameters)
            .build();

        final InitiateAuthResponse authResponse = cognitoIdentityProviderClient.initiateAuth(authRequest);

        return LoginUserResponse.builder()
            .idToken(authResponse.authenticationResult().idToken())
            .accessToken(authResponse.authenticationResult().accessToken())
            .refreshToken(authResponse.authenticationResult().refreshToken())
            .isSuccessful(authResponse.sdkHttpResponse().isSuccessful())
            .statusCode(authResponse.sdkHttpResponse().statusCode())
            .build();
    }

    @Override
    public AddUserToGroupResponse addUserToGroup(AddUserToGroupRequest request) {
        final AdminAddUserToGroupRequest adminAddUserToGroupRequest = AdminAddUserToGroupRequest.builder()
            .groupName(request.getGroupName())
            .username(request.getUserName())
            .userPoolId(cognitoConfig.getUserPoolId())
            .build();

        final AdminAddUserToGroupResponse adminAddUserToGroupResponse = 
            cognitoIdentityProviderClient.adminAddUserToGroup(adminAddUserToGroupRequest);

        return AddUserToGroupResponse.builder()
            .isSuccessful(adminAddUserToGroupResponse.sdkHttpResponse().isSuccessful())
            .statusCode(adminAddUserToGroupResponse.sdkHttpResponse().statusCode())
            .build();
    }

    @Override
    public MyGetUserResponse getUser(MyGetUserRequest request) {
        final GetUserRequest getUserRequest = GetUserRequest.builder()
            .accessToken(request.getAccessToken())
            .build();

        final GetUserResponse getUserResponse = cognitoIdentityProviderClient.getUser(getUserRequest);
        
        final Map<String,String> userAttributes = getUserResponse.userAttributes()
        .stream().collect(
            Collectors.toMap(AttributeType::name, AttributeType::value)
        );

        // Nothing fancy - we only use this for testing permissions.
        return MyGetUserResponse.builder()
            .isSuccessful(getUserResponse.sdkHttpResponse().isSuccessful())
            .statusCode(getUserResponse.sdkHttpResponse().statusCode())
            .userAttributes(userAttributes)
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
