package com.study.board.service;

import com.study.board.domain.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 게시글 작성
    public void write(Board board){
        boardRepository.save(board);
    }

    // 게시글 리스트 처리
    public List<Board> boardList(){
        return boardRepository.findAll();
    }

    // id로 특정 게시글 불러오기
    public Board boardView(Integer id) {
        return boardRepository.findById(id).get(); // id 값으로 게시글 찾아서 반환
    }

    // 특정 게시글 삭제
    public void deleteBoard(Integer id){
        boardRepository.deleteById(id);
    }

    @Transactional
    public void editBoard(Integer id, Board board){// dirty checking 개념 공부할것!
        Board Eboard = boardRepository.getById(id); // Eboard -> 수정할 보드
        Eboard.setTitle(board.getTitle());
        Eboard.setContent(board.getContent());
    }

}
