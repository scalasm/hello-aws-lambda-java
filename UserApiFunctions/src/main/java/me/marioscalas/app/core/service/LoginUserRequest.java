package me.marioscalas.app.core.service;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class LoginUserRequest {
    private String email;
    private String password;
}
