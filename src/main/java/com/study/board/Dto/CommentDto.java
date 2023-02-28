package com.study.board.Dto;

import com.study.board.entity.Comment;
import com.study.board.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommentDto {


    private Integer id;
    private String text;
    private LocalDateTime writedate;
    private Integer boardid;

    private String userid;
    private Integer level;
    private Integer parentid;

    private Integer targetid;
    private Integer sort;

    public Comment toEntity(){
        return Comment.builder()
                .id(id)
                .text(text)
                .writedate(writedate)
                .boardid(boardid)
                .userid(userid)
                .level(level)
                .parentid(parentid)
                .targetid(targetid)
                .sort(sort).build();


    }


}
