package me.marioscalas.app.port;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import me.marioscalas.app.core.service.ConfirmUserSignUpRequest;
import me.marioscalas.app.core.service.ConfirmUserSignUpResponse;


public class ConfirmUserHandler extends AbstractLambdaHandler {

    @Override
    protected void onAPIGatewayProxyRequestEvent(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response, Context context) {
        final ConfirmUserSignUpRequest request = GSON.fromJson(
            input.getBody(),    
            ConfirmUserSignUpRequest.class
        );

        final ConfirmUserSignUpResponse confirmUserSignUpResponse = userService.confirmUserSignUp(request);

        response.setStatusCode(200);
        response.setBody(
            GSON.toJson(confirmUserSignUpResponse)
        );
    }
}
