package com.partyCheck.Backend.controller;

import com.partyCheck.Backend.model.UserDTO;
import com.partyCheck.Backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    @GetMapping("/list")
    public List<UserDTO> getAllUser() {
        return userService.getAll();
    }

}
