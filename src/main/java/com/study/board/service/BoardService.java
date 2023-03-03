package com.study.board.service;

import com.study.board.Dto.CommentDto;
import com.study.board.entity.Board;
import com.study.board.entity.Boardfile;
import com.study.board.entity.Comment;
import com.study.board.repository.BoardFileRepository;
import com.study.board.repository.BoardRepository;
import com.study.board.repository.CommentRepository;
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

    @Autowired
    private CommentRepository commentRepository;

    /**
     * 글 작성하기
     */
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

    /**
     * 글 리스트 전부 가져오기
     */
    // 게시글 리스트 처리
    public Page<Board> boardList(Pageable pageable){

        return boardRepository.findAll(pageable);
    }

    /**
     * 글 정보 하나 가져오기(첨부파일, 댓글 포함)
     */
    public Model boardView(Model model, Integer id) {
        model.addAttribute("board",boardRepository.findById(id).get());
        model.addAttribute("fileList",boardFileRepository.findByBoard_id(id));// Fk 로 findBy 하는법 찾아봄
        model.addAttribute("commentList",commentRepository.findAllByBoardidOrderBySortAsc(id));//
        return model; // id 값으로 게시글 찾아서 반환
    }

    /**
     * 게시글 삭제
     */
    public void deleteBoard(Integer id){
        boardRepository.deleteById(id);
    }

    /**
     * 게시글 편집
     */
    @Transactional
    public void editBoard(Integer id, Board board){// dirty checking 개념 공부할것!
        Board Eboard = boardRepository.findById(id).get(); // Eboard -> 수정할 보드
        Eboard.setTitle(board.getTitle());
        Eboard.setContent(board.getContent());
    }

    /**
     * 댓글 달기
     */
    @Transactional
    public String addComment(CommentDto commentdto) { // 부모댓글임


        commentdto.setLevel(0);
        commentdto.setDeletecheck(0);

        Comment comment = commentRepository.save(commentdto.toEntity());

        comment.setParentid(comment.getId());// parendid 가 본인 자신의 id인 원댓글
        comment.setTargetid(0);

        List<Comment> commentList = commentRepository.findAllByBoardidAndSortIsNotNullOrderBySortDesc(commentdto.getBoardid());
        // 
        if(commentList.size() == 0){ // 첫댓글
            System.out.println("!!!!!"+commentList.size());
            comment.setSort(1);
        }
        else{
            for(Comment cmt : commentList){
                System.out.println(cmt.getId()+"번째"+cmt.getSort()+"개");
            }
            System.out.println("최대값"+commentList.get(0).getSort());
            comment.setSort(commentList.get(0).getSort()+1); // 모든 sort 중 최대값 넣어주기
        }

        return "1";

    }

    /**
     * 답글 달기
     */
    @Transactional
    public String addRcomment(CommentDto commentdto) {
        Comment Pcomment = commentRepository.findById(commentdto.getTargetid()).get(); // 답글 타겟으로 지정한 댓글

        commentdto.setBoardid(Pcomment.getBoardid());
        commentdto.setParentid(Pcomment.getParentid());
        commentdto.setLevel(Pcomment.getLevel()+1);
        commentdto.setDeletecheck(0);

        Comment comment = commentRepository.save(commentdto.toEntity());

        List<Comment> commentList = commentRepository.findAllByBoardidAndTargetidAndSortIsNotNullOrderBySortDesc(commentdto.getBoardid(),commentdto.getTargetid());
        // 같은 게시글에 대해 같은 댓글을 타겟으로 한 댓글들을 sort 기준으로 정렬

        if(commentList.size() == 0){ // 한 타겟 첫댓글
            Comment Tcomment = commentRepository.findById(comment.getTargetid()).get(); // 타겟 코멘트
            List<Comment> GTcommentList = commentRepository.findAllByBoardidAndSortGreaterThanAndSortIsNotNullOrderBySortDesc(comment.getBoardid() , Tcomment.getSort());

            for(Comment cmt : GTcommentList){
                cmt.setSort(cmt.getSort()+1);
            }

            comment.setSort(Tcomment.getSort()+1);
        }
        else{
            List<Comment> StcommentList = commentRepository.findAllByBoardidAndTargetidAndSortIsNotNullOrderBySortDesc(comment.getBoardid() , comment.getTargetid());
            //SameTargetList 같은 타겟 가진 댓글리스트
            // 얘네가 가진 값들중 제일 큰 값보다 +1 해서 마지막에 배치할 예정
            Integer newSort = StcommentList.get(0).getSort()+1; // 같은 타겟 가진 녀석중 제일 뒤+1 넣어줄 값
            System.out.println("newSort"+newSort);

            List<Comment> GTcommentList = commentRepository.findAllByBoardidAndSortGreaterThanAndSortIsNotNullOrderBySortDesc(comment.getBoardid() , newSort-1);
            for(Comment cmt : GTcommentList){
                cmt.setSort(cmt.getSort()+1);
            }
            comment.setSort(newSort);
        }
        return "1";
    }

    /**
     * 글 목록 가져오기(with search)
     */
    public Page<Board> boardListWithSearch(Pageable pageable, String searchType, String searchWord) {
        Page<Board> list = null;
        switch(searchType) {
            case "title": list = boardRepository.findByTitleContaining(pageable,searchWord);
                break;
            case "userid": list = boardRepository.findByUseridContaining(pageable,searchWord);
                break;
            case "content": list = boardRepository.findByContentContaining(pageable,searchWord);

        }
        return list;

    }

    /**
     * 댓글 삭제(update deletecheck=1)
     */
    @Transactional
    public String deleteComment(String commentid) {

        try{
            Comment DComment = commentRepository.findById(Integer.valueOf(commentid)).get();
            DComment.setDeletecheck(1);
        }catch (Exception e){
            return "0";
        }
        return "1";
    }

    /**
     * 댓글 변경
     */
    @Transactional
    public String editComment(CommentDto commentDto) {
        try{
            Comment Ecomment = commentRepository.findById(Integer.valueOf(commentDto.getId())).get();
            Ecomment.setText(commentDto.getText());
        }catch (Exception e){
            return "0";
        }

        return "1";
    }
}
