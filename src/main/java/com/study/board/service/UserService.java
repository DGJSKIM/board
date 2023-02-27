package com.study.board.service;

import com.study.board.Dto.UserDto;
import com.study.board.repository.UserRepository;
import com.study.board.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {


    private final BCryptPasswordEncoder encoder;
    public UserService(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }


    @Autowired
    private UserRepository userRepository;



    //private final BCryptPasswordEncoder encoder;
    public String userJoin(UserDto userDto) {
        


        if(!userRepository.findByUserid(userDto.getUserid()).isEmpty()){ // 비어 있다면 ->
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
        else{
            userDto.encryptPassword(encoder.encode(userDto.getPassword()));
            // userDto.getPassword() 를 encoder.encode 로 변환 시킨 값을 password 로 저장
            userRepository.save(userDto.toEntity());// Dto 유저객체를  Entity 로 변환후 저장
        }

        return userDto.getUserid();
    }

    public boolean login(UserDto userDto) { // 로그인 처리

        if(!userRepository.findByUserid(userDto.getUserid()).isEmpty() && userRepository.findByUserid(userDto.getUserid()).size()==1){
            // 1명만 조회될 경우 (즉 id 일치하는 User 가 있을 경우)
            System.out.println("아이디 일치");
            if(encoder.matches(userDto.getPassword(),userRepository.findByUserid(userDto.getUserid()).get(0).getPassword())){
                System.out.println("비밀번호 일치");
                // 비밀번호 암호화하여 비교
                return true;
            }
        }

        return false;
    }
}
