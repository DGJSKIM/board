package com.study.board.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
public class Comment {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    @CreatedDate
    private LocalDateTime writedate;
    private Integer boardid;

    private String userid;
    private Integer level;
    private Integer parentid;


}
