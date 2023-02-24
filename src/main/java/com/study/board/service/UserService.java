package com.study.board.service;

import com.study.board.Dto.UserDto;
import com.study.board.repository.UserRepository;
import com.study.board.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private final BCryptPasswordEncoder encoder;
    public UserService(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }


    @Autowired
    private UserRepository userRepository;



    //private final BCryptPasswordEncoder encoder;
    public void userJoin(UserDto userDto) {
        
        userDto.encryptPassword(encoder.encode(userDto.getPassword()));
        // userDto.getPassword() 를 encoder.encode 로 변환 시킨 값을 password 로 저장

        userRepository.save(userDto.toEntity());// Dto 유저객체를  Entity 로 변환후 저장
    }
}
