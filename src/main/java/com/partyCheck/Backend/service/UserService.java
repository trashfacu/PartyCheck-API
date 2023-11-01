package com.partyCheck.Backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyCheck.Backend.entity.Role;
import com.partyCheck.Backend.entity.User;
import com.partyCheck.Backend.model.UserDTO;
import com.partyCheck.Backend.model.VenueDTO;
import com.partyCheck.Backend.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper mapper;

    public List<UserDTO> getAll() {
        List<User> userList = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : userList){
            userDTOList.add(mapper.convertValue(user, UserDTO.class));
        }
        return userDTOList;
    }

    public void registerDefaultAdminUser() {
        if (userRepository.findByEmail("admin@admin.com").isEmpty()){
            User adminUser = User.builder()
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("Admin")
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(adminUser);
        }
    }

    public UserDTO getUserInfo(User user){
        Optional <User> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return convertToResponse(userOptional.get());
    }

    private UserDTO convertToResponse(User user){
        UserDTO userDTO = new UserDTO();

        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());

        return userDTO;
    }

}
