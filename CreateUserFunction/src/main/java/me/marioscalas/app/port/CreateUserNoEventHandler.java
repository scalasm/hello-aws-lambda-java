package me.marioscalas.app.port;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

/**
 * This is the same as CreateUserHandler but it supports "No Proxy Integration" in API Gateway - this means
 * that we have full control about what we get and what we produce.
 */
public class CreateUserNoEventHandler implements RequestHandler<Map<String,String>, Map<String,String>> {
    // Static Initialization here 
    private static Gson GSON = new Gson();

    @Override
    public Map<String,String> handleRequest(Map<String,String> input, Context context) {
        final LambdaLogger logger = context.getLogger();

        final String firstName = input.get("firstName");
        final String lastName = input.get("lastName");
        final String email = input.get("email");
        final String password = input.get("password");
        final String confirmPassword = input.get("confirmPassword");

        logger.log("\n firstName = " + firstName);
        logger.log("\n lastName = " + lastName);
        logger.log("\n email = " + email);


        final String userId = UUID.randomUUID().toString();

        final Map<String,String> response = new HashMap<>();
        response.put("id", userId);
        response.put("firstName", firstName);
        response.put("lastName", lastName);
        response.put("email", email);

        return response;
    }
}
