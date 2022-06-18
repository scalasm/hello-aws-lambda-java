package me.marioscalas.app.port;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;

import me.marioscalas.app.core.service.AddUserToGroupRequest;
import me.marioscalas.app.core.service.AddUserToGroupResponse;



public class AddUserToGroupHandler extends AbstractLambdaHandler {
    @Override
    protected void onAPIGatewayProxyRequestEvent(APIGatewayProxyRequestEvent input, APIGatewayProxyResponseEvent response, Context context) {
        final JsonObject payload = GSON.fromJson(
            input.getBody(),
            JsonObject.class
        );
        final String groupName = input.getPathParameters().get("groupName");
        
        final AddUserToGroupRequest request = AddUserToGroupRequest.builder()
            .userName(payload.get("userName").getAsString())
            .groupName(groupName)
            .build();

        final AddUserToGroupResponse createUserResponse = userService.addUserToGroup(request);

        response.setStatusCode(200);
        response.setBody(
            GSON.toJson(createUserResponse)
        );
    }
}
