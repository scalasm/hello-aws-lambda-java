package me.marioscalas.app.port;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import me.marioscalas.app.core.service.CreateUserRequest;
import me.marioscalas.app.core.service.CreateUserResponse;



public class CreateUserHandler extends AbstractLambdaHandler {
    @Override
    protected void onAPIGatewayProxyRequestEvent(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response, Context context) {
        final CreateUserRequest request = GSON.fromJson(
            input.getBody(),    
            CreateUserRequest.class
        );

        final CreateUserResponse createUserResponse = userService.createUser(request);

        response.setStatusCode(200);
        response.setBody(
            GSON.toJson(createUserResponse)
        );
    }
}
