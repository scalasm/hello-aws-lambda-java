package me.marioscalas.app.port;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import me.marioscalas.app.adapter.CognitoConfig;
import me.marioscalas.app.adapter.CognitoUserService;
import me.marioscalas.app.core.service.ConfirmUserSignUpRequest;
import me.marioscalas.app.core.service.ConfirmUserSignUpResponse;
import me.marioscalas.app.core.service.UserService;
import software.amazon.awssdk.awscore.exception.AwsServiceException;



public class ConfirmUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
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

        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            final ConfirmUserSignUpRequest request = GSON.fromJson(
                input.getBody(),    
                ConfirmUserSignUpRequest.class
            );

            final ConfirmUserSignUpResponse confirmUserSignUpResponse = userService.confirmUserSignUp(request);

            response.setStatusCode(200);
            response.setBody(
                GSON.toJson(confirmUserSignUpResponse)
            );
        } catch (AwsServiceException e) {
            logger.log("AWS SDK Error: " + e.getMessage());

            response.setStatusCode(e.awsErrorDetails().sdkHttpResponse().statusCode());
            response.setBody(
                GSON.toJson(
                    new ErrorResponse(
                        e.awsErrorDetails().errorMessage(),
                        null
                    )
                )
            );
        } catch (Exception e) {
            logger.log("Whops!: " + e.getMessage());

            response.setStatusCode(500);
            response.setBody(
                GSON.toJson(
                    ErrorResponse.fromException(e)
                )
            );
        }

        return response;
    }

    private static UserService getUserService() {
        final CognitoConfig config = new CognitoConfig(
            Utils.getenv("COGNITO_APP_CLIENT_ID"), 
            Utils.getenv("COGNITO_APP_CLIENT_SECRET"), 
            System.getenv("AWS_REGION")
        );
        
        return new CognitoUserService(config);
    }
}
