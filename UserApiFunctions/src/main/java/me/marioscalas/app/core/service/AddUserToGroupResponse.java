package me.marioscalas.app.core.service;

import lombok.Builder;
import lombok.Getter;


@Builder @Getter
public class AddUserToGroupResponse {
    private boolean isSuccessful;
    private int statusCode;
}