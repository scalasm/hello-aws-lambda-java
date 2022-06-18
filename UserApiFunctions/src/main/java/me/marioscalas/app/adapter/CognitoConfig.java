package me.marioscalas.app.adapter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@RequiredArgsConstructor @Getter
public class CognitoConfig {

    private final String appClientId;

    private final String appClientSecret;  
    
    private final String userPoolId;

    private final String region;    

    public CognitoIdentityProviderClient getCognitoIdentityProviderClient() {
        return CognitoIdentityProviderClient.builder()
            .region(Region.of(region))
            .build();
    }
}
