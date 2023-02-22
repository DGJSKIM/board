package com.study.board.repository;

import com.study.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository <Board, Integer> { // <Entity, Id타입>

}
