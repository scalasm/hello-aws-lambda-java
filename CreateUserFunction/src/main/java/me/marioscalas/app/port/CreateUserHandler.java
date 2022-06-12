package me.marioscalas.app.port;

import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
}

@Builder @Getter
class CreateUserResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
}

public class CreateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    // Static Initialization here 
    private static Gson GSON = new Gson();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        final LambdaLogger logger = context.getLogger();

        logger.log("Updated lambda version!");
        logger.log("Using lambda function v" + context.getFunctionVersion());

        final String jsonBody = input.getBody();
        final CreateUserRequest request = GSON.fromJson(jsonBody, CreateUserRequest.class);

        final String bodyAsJson = GSON.toJson(
            CreateUserResponse.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build()
        );
        
        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody(bodyAsJson);

        return response;
    }
}
