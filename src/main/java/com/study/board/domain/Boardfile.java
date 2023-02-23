package com.study.board.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@DynamicUpdate
public class Boardfile{
        @Id // primary key
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer boardfileid;

        private String filename;
        private String filepath;
      //private Integer board_id;
        private String orgfilename;

        @ManyToOne(fetch = FetchType.LAZY) // 필요한 정보만 효율적으로 불러옴
        @JoinColumn(name = "boardid")
        private Board board;



}
