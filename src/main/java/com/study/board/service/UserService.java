package com.study.board.service;

import com.study.board.Dto.UserDto;
import com.study.board.repository.UserRepository;
import com.study.board.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.lang.reflect.Member;
import java.util.List;

@Service
public class UserService {


    private final BCryptPasswordEncoder encoder;
    public UserService(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }


    @Autowired
    private UserRepository userRepository;



    /**
     * 회원가입
     */
    public String userJoin(UserDto userDto, HttpSession session) {
        


        if(!userRepository.findByUserid(userDto.getUserid()).isEmpty()){ // 비어 있다면 ->
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
        else{
            userDto.encryptPassword(encoder.encode(userDto.getPassword()));
            // userDto.getPassword() 를 encoder.encode 로 변환 시킨 값을 password 로 저장
            User newUser = userRepository.save(userDto.toEntity());// Dto 유저객체를  Entity 로 변환후 저장
            session.setAttribute("userid", newUser.toDto().getUserid());
            session.setAttribute("loginuser",newUser.toDto());
        }

        return userDto.getUserid();
    }

    /**
     * 로그인
     */
    public User login(UserDto userDto) { // 로그인 처리

        if(!userRepository.findByUserid(userDto.getUserid()).isEmpty() && userRepository.findByUserid(userDto.getUserid()).size()==1){
            // 1명만 조회될 경우 (즉 id 일치하는 User 가 있을 경우)
            if(encoder.matches(userDto.getPassword(),userRepository.findByUserid(userDto.getUserid()).get(0).getPassword())){
                // 비밀번호 암호화하여 비교
                return userRepository.findByUserid(userDto.getUserid()).get(0);

            }
        }

        return null;
    }

    /**
     * admin 체크 (role == 1)
     */
    public boolean checkAdmin(String loginUser) {
        boolean result;
        if(userRepository.findByUserid(loginUser).get(0).getRole()==1){
            result = true;
        }
        else{
            result = false;
        }
        return result;
    }

    /**
     * 회원리스트 가져오기
     */
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    /**
     * 회원정보 수정
     */
    @Transactional
    public void userEdit(UserDto userDto) {
        User user = userRepository.findByUserid(userDto.getUserid()).get(0);

        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setNickname(userDto.getNickname());

    }
}
