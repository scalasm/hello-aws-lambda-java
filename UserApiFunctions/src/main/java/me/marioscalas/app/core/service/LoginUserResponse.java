package me.marioscalas.app.core.service;

import lombok.Builder;
import lombok.Getter;


@Builder @Getter
public class LoginUserResponse {
    private boolean isSuccessful;
    private int statusCode;
    private String idToken;
    private String accessToken;
    private String refreshToken;
}