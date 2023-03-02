package com.study.board.entity;

import com.study.board.Dto.UserDto;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String userid;
    private String password;
    private String nickname;
    private int role;// 1 일반 0 관리자

    public UserDto toDto(){
        return UserDto.builder()
                .id(id)
                .userid(userid)
                .password(password)
                .nickname(nickname)
                .role(role).build();


    }
/*
    @OneToMany(mappedBy="user")
    private List<Board> boardList= new ArrayList<>();
*/

}
