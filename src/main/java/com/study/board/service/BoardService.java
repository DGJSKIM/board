package com.study.board.service;

import com.study.board.domain.Board;
import com.study.board.domain.Boardfile;
import com.study.board.repository.BoardFileRepository;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.transaction.Transactional;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardFileRepository boardFileRepository;


    @Transactional
    // 게시글 작성
    public boolean write(MultipartHttpServletRequest mrequest, Board board){
        boardRepository.save(board);

        List<MultipartFile> fileList = mrequest.getFiles("fileList"); // 파일들
        boolean fileUpload = true;

        // 파일 업로드 시작
        if( !fileList.isEmpty() ) { // fileList 로 넘어온 파일이 존재한다면
            for(MultipartFile file : fileList) { // 각자의 파일별로 저장
                String path = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\boardfiles";

                UUID uuid = UUID.randomUUID(); // 랜덤값을 가져와줌\
                String fileName = uuid+file.getOriginalFilename();
                File saveFile = new File(path,fileName); // 지정한 경로에 위 이름의 파일을 만듬(빈 파일)

                // 첨부파일의 내용물을 담는 것
                try {
                    file.transferTo(saveFile);// file 을 saveFile 에 저장
                } catch (Exception e) {
                    e.printStackTrace();
                    fileUpload = false;
                    return false;
                }

                Boardfile nFile = new Boardfile(); // 새 객체를 만들어
                nFile.setBoard(board);
                nFile.setFilename(fileName);
                nFile.setFilepath(path);
                nFile.setOrgfilename(file.getOriginalFilename());

                boardFileRepository.save(nFile);

            }// end of for
        }// 파일 업로드 끝

        return true;


    }

    // 게시글 리스트 처리
    public Page<Board> boardList(Pageable pageable){

        return boardRepository.findAll(pageable);
    }

    // id로 특정 게시글 불러오기
    public Model boardView(Model model, Integer id) {

        System.out.println("id"+id);
        System.out.println("boardRepository.findById(id).get()"+boardRepository.findById(id).get());
        model.addAttribute("board",boardRepository.findById(id).get());
        model.addAttribute("fileList",boardFileRepository.findByBoard_id(id));// Fk 로 findBy 하는법 찾아봄
        System.out.println("fileList"+boardFileRepository.findByBoard_id(id));
        return model; // id 값으로 게시글 찾아서 반환
    }

    // 특정 게시글 삭제
    public void deleteBoard(Integer id){
        boardRepository.deleteById(id);
    }

    @Transactional
    public void editBoard(Integer id, Board board){// dirty checking 개념 공부할것!
        Board Eboard = boardRepository.findById(id).get(); // Eboard -> 수정할 보드
        Eboard.setTitle(board.getTitle());
        Eboard.setContent(board.getContent());
    }

}
