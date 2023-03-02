package com.study.board.repository;

import com.study.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository <Board, Integer> {
    Page<Board> findByTitleContaining(Pageable pageable, String searchWord);

    Page<Board> findByUseridContaining(Pageable pageable, String searchWord);

    Page<Board> findByContentContaining(Pageable pageable, String searchWord);

}
