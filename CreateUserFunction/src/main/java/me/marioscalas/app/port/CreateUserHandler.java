package me.marioscalas.app.port;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import me.marioscalas.app.adapter.CognitoConfig;
import me.marioscalas.app.adapter.CognitoUserService;
import me.marioscalas.app.core.service.CreateUserRequest;
import me.marioscalas.app.core.service.CreateUserResponse;
import me.marioscalas.app.core.service.UserService;
import software.amazon.awssdk.awscore.exception.AwsServiceException;



public class CreateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    // Static Initialization here 

    /**
     * Default GSON serializer - null attributes won't get serialized.
     */
    private static final Gson GSON = new Gson();

    private static final UserService userService = getUserService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        final LambdaLogger logger = context.getLogger();

        logger.log("Using lambda function v" + context.getFunctionVersion());

        final CreateUserRequest request = GSON.fromJson(
            input.getBody(),    
            CreateUserRequest.class
        );

        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            final CreateUserResponse createUserResponse = userService.createUser(request);

            response.setStatusCode(200);
            response.setBody(
                GSON.toJson(createUserResponse)
            );
        } catch (AwsServiceException e) {
            logger.log("Error: " + e.getMessage());

            response.setStatusCode(500);
            response.setBody(
                GSON.toJson(
                    new ErrorResponse(
                        e.awsErrorDetails().errorMessage(),
                        null
                    )
                )
            );
        }

        return response;
    }

    private static UserService getUserService() {
        final CognitoConfig config = new CognitoConfig(
            System.getenv("COGNITO_APP_CLIENT_ID"), 
            System.getenv("COGNITO_APP_CLIENT_SECRET"), 
            System.getenv("AWS_REGION")
        );
        
        return new CognitoUserService(config);
    }
}
