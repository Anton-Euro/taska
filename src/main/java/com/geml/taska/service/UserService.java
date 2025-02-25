package com.geml.taska.service;

import com.geml.taska.dto.CreateUserDto;
import com.geml.taska.dto.DisplayUserDto;
import com.geml.taska.mapper.UserMapper;
import com.geml.taska.models.User;
import com.geml.taska.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserService(final UserRepository userRepository, final UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    public List<DisplayUserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toDisplayUserDto)
            .collect(Collectors.toList());
    }


    public DisplayUserDto getUserById(final Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return userMapper.toDisplayUserDto(user);
    }


    public DisplayUserDto createUser(final CreateUserDto userDto) {
        User user = userMapper.fromCreateUserDto(userDto);
        User saved = userRepository.save(user);
        return userMapper.toDisplayUserDto(saved);
    }


    public DisplayUserDto updateUser(final Long id, final CreateUserDto userDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        User saved = userRepository.save(user);
        return userMapper.toDisplayUserDto(saved);
    }


    public void deleteUser(final Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
    }
}