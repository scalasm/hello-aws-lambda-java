package me.marioscalas.app.port;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import me.marioscalas.app.core.service.CreateUserRequest;
import me.marioscalas.app.core.service.CreateUserResponse;
import me.marioscalas.app.core.service.UserService;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.http.SdkHttpResponse;

@ExtendWith(MockitoExtension.class)
public class CreateUserHandlerTest {

    private static final Gson GSON = new Gson();

    @Mock
    private APIGatewayProxyRequestEvent mockInput;

    @Mock
    private Context mockContext;

    @Mock
    private LambdaLogger mockLogger;

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private CreateUserHandler handler;

    @BeforeEach
    public void initialize() {
        when(mockContext.getLogger()).thenReturn(mockLogger);
    }

    @Test
    public void testHandleResponse_whenValidDetailsProvided_returnSuccessfulResponse() {
        final JsonObject userDetails = new JsonObject();
        userDetails.addProperty("firstName", "John");
        userDetails.addProperty("lastName", "Smith");
        userDetails.addProperty("email", "john.smith@gmail.com");
        userDetails.addProperty("password", "Kapel2ineri5.");
        userDetails.addProperty("confirmPassword", "Kapel2ineri5.");
        final String userDetailsJson = GSON.toJson(userDetails);

        when(mockInput.getBody()).thenReturn(userDetailsJson);

        final CreateUserResponse fakeCreateUserResponse = CreateUserResponse.builder()
            .statusCode(200)
            .isConfirmed(false)
            .isSuccessful(true)
            .cognitoUserId("user-xyz")
            .build();

        when(mockUserService.createUser(any(CreateUserRequest.class))).thenReturn(fakeCreateUserResponse);

        final APIGatewayProxyResponseEvent response = handler.handleRequest(mockInput, mockContext);

        verify(mockLogger, times(1)).log(anyString());
        verify(mockUserService, times(1)).createUser(any(CreateUserRequest.class));

        final CreateUserResponse responseFromPayload = GSON.fromJson(response.getBody(), CreateUserResponse.class);
        assertEquals(responseFromPayload.getStatusCode(), 200);
        assertFalse(responseFromPayload.isConfirmed());
        assertTrue(responseFromPayload.isSuccessful());
        assertEquals(responseFromPayload.getCognitoUserId(), "user-xyz");
    }

    @Test
    public void testHandleResponse_whenEmptyBodyIsProvided_returnErrorMessage() {
        final JsonObject emptyUserDetails = new JsonObject();
        final String emptyUserDetailsJson = GSON.toJson(emptyUserDetails);

        when(mockInput.getBody()).thenReturn(emptyUserDetailsJson);

        when(mockUserService.createUser(any(CreateUserRequest.class))).thenThrow(
            new RuntimeException("Error for test!")
        );

        final APIGatewayProxyResponseEvent response = handler.handleRequest(mockInput, mockContext);

        verify(mockLogger, times(1 + 1)).log(anyString());
        verify(mockUserService, times(1)).createUser(any(CreateUserRequest.class));

        assertEquals(response.getStatusCode(), 500);
        
        final ErrorResponse responseFromPayload = GSON.fromJson(response.getBody(), ErrorResponse.class);
        assertNotNull(responseFromPayload.getErrorMessage(), "Missing error message!");
        assertNotNull(responseFromPayload.getStackTrace(), "Missing stacktrace");
    }

    @Test
    public void testHandleResponse_whenCognitoSdkErrorHappens_returnErrorMessage() {
        final JsonObject userDetails = new JsonObject();
        userDetails.addProperty("firstName", "John");
        userDetails.addProperty("lastName", "Smith");
        userDetails.addProperty("email", "john.smith@gmail.com");
        userDetails.addProperty("password", "Kapel2ineri5.");
        userDetails.addProperty("confirmPassword", "Kapel2ineri5.");
        final String userDetailsJson = GSON.toJson(userDetails);

        when(mockInput.getBody()).thenReturn(userDetailsJson);

        final AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder()
            .errorCode("")
            .sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build())
            .errorMessage("Test AWS service error")
            .build();

        final AwsServiceException awsServiceException = AwsServiceException.builder()
            .statusCode(500)
            .awsErrorDetails(awsErrorDetails)
            .build();

        when(mockUserService.createUser(any(CreateUserRequest.class))).thenThrow(
            awsServiceException
        );

        final APIGatewayProxyResponseEvent response = handler.handleRequest(mockInput, mockContext);

        verify(mockLogger, times(1 + 1)).log(anyString());
        verify(mockUserService, times(1)).createUser(any(CreateUserRequest.class));

        assertEquals(response.getStatusCode(), 500);
        
        final ErrorResponse responseFromPayload = GSON.fromJson(response.getBody(), ErrorResponse.class);
        assertNotNull(responseFromPayload.getErrorMessage(), "Missing error message!");
        assertNotNull(responseFromPayload.getStackTrace(), "Missing stacktrace");
    }
}
