package com.study.board.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicUpdate

public class Board {


    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String content;



    @OneToMany(mappedBy = "board")
    private List<Boardfile> boardfileList = new ArrayList<>();
}
