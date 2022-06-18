package me.marioscalas.app.core.service;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

/**
 * Nothing fancy - we only use this for testing permissions.
 */
@Builder @Getter
public class MyGetUserResponse {
    private boolean isSuccessful;
    private int statusCode;

    private Map<String,String> userAttributes;
}