package com.study.board.repository;

import com.study.board.entity.Boardfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardFileRepository extends JpaRepository<Boardfile, String> {


    public List<Boardfile> findByBoard_id(int id);
}
