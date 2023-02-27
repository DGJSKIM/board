package com.study.board.Dto;


import com.study.board.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class UserDto {


    private Long id;
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9]{1,16}$",message = "ID는 16글자 내의 영어, 숫자의 조합으로 입력이 가능합니다.")
    private String userid;

    @NotBlank(message="패스워드는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&])[a-z\\d@$!%*#?&]{1,16}$" , message = "비밀번호는 16글자 이내 영어,숫자,특수문자를 최소 한번씩은 조합하여 작성해주세요.")
    private String password;
    private String nickname;
    private int role;// 1 일반 0 관리자


    /* 암호화된 password */
    public void encryptPassword(String BCryptpassword) {
        this.password = BCryptpassword;
    }

    /* Dto -> Entity */
    public User toEntity(){
        return User.builder()
                .id(id)
                .userid(userid)
                .password(password)
                .nickname(nickname)
                .role(role).build();


    }

/*
    public boolean isValid() {
        String regexId = "^[A-Za-z0-9]{1,16}$"; // 16글자 내의 영어숫자만 입력가능
        String regexPw = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&])[a-z\\d@$!%*#?&]{1,16}$"; // 16글자 이내 영어 대소문자,숫자,특수문자

        return userid.matches(regexId) && password.matches(regexPw);


    }
*/
}

