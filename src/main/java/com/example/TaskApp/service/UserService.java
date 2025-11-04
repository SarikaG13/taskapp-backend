package com.example.TaskApp.service;

import com.example.TaskApp.dto.Response;
import com.example.TaskApp.dto.UserRequest;
import com.example.TaskApp.entity.User;

public interface UserService {

    Response<?> signUp(UserRequest userRequest);
    Response<?> login(UserRequest userRequest);
    User getCurrentLoggedInUser();
    User findByUsername(String username);

}
