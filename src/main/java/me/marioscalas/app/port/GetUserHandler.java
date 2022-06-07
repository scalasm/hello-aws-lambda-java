package me.marioscalas.app.port;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class User {
    final String userId;
    final String firstName;
    final String lastName;
}


public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    // Static Initialization here 
    private static Gson GSON = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        final Map<String, String> pathParameters = input.getPathParameters();
        final String userId = pathParameters.get("userId");

        final User stubUser = new User(userId, "Mario", "Scalas");    

        final String bodyAsJson = GSON.toJson(stubUser);

        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody(bodyAsJson);

        return response;
    }
}
