package com.study.board.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter
public class CommentDto {


    private Integer id;
    private String text;
    private LocalDateTime writedate;
    private Integer boardid;

    private String userid;
    private Integer level;
    private Integer parentid;


}
