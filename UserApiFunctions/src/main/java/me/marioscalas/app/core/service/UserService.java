package me.marioscalas.app.core.service;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);
    ConfirmUserSignUpResponse confirmUserSignUp(ConfirmUserSignUpRequest request);
}
