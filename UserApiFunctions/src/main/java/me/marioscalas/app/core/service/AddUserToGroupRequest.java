package me.marioscalas.app.core.service;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class AddUserToGroupRequest {
    private String userName;
    private String groupName;
}
