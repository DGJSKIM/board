package com.study.board.repository;

import com.study.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository <Comment, Integer> {


    List<Comment> findAllByBoardidOrderByParentid(Integer Boardid);


    List<Comment> findAllByBoardidOrderBySortAsc(Integer boardid);

    List<Comment> findAllByBoardidAndSortIsNotNullOrderBySortDesc(Integer boardid);

    List<Comment> findAllByBoardidAndTargetidAndSortIsNotNullOrderBySortDesc(Integer boardid, Integer targetid);

    List<Comment> findAllByBoardidAndSortGreaterThanAndSortIsNotNullOrderBySortDesc(Integer boardid, Integer sort);
}
