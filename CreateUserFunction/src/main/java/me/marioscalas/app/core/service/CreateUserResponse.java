package me.marioscalas.app.core.service;

import lombok.Builder;
import lombok.Getter;


@Builder @Getter
public class CreateUserResponse {
    private boolean isSuccessful;
    private int statusCode;
    private String cognitoUserId;
    private boolean isConfirmed;
}