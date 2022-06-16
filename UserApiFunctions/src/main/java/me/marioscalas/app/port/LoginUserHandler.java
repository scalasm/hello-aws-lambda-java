package me.marioscalas.app.port;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import me.marioscalas.app.core.service.LoginUserRequest;
import me.marioscalas.app.core.service.LoginUserResponse;


public class LoginUserHandler extends AbstractLambdaHandler {

    @Override
    protected void onAPIGatewayProxyRequestEvent(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response, Context context) {
        final LoginUserRequest request = GSON.fromJson(
            input.getBody(),    
            LoginUserRequest.class
        );

        final LoginUserResponse loginUserResponse = userService.loginUser(request);

        response.setStatusCode(200);
        response.setBody(
            GSON.toJson(loginUserResponse)
        );
    }
}
