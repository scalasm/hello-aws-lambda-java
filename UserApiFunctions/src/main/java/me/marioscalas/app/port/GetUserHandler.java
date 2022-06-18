package me.marioscalas.app.port;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import me.marioscalas.app.core.service.MyGetUserRequest;
import me.marioscalas.app.core.service.MyGetUserResponse;

public class GetUserHandler extends AbstractLambdaHandler {
    @Override
    protected void onAPIGatewayProxyRequestEvent(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response, Context context) {
        // Case is not preserved - I must use lowercase name even if I pass "AccessToken"    
        final String accessToken = input.getHeaders().get("accesstoken");

        final LambdaLogger logger = context.getLogger();
        logger.log("Access token == " + accessToken);
        logger.log("All headers == " + input.getHeaders());

        final MyGetUserRequest request = MyGetUserRequest.builder()
            .accessToken(accessToken)
            .build();

        final MyGetUserResponse createUserResponse = userService.getUser(request);

        response.setStatusCode(200);
        response.setBody(
            GSON.toJson(createUserResponse)
        );
    }
}
