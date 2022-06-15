package me.marioscalas.app.core.service;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class ConfirmUserSignUpRequest {
    private String email;
    private String confirmationCode;
}
