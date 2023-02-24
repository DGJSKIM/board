package com.study.board.controller;

import com.study.board.Dto.UserDto;
import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import com.study.board.service.BoardService;
import com.study.board.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import java.io.IOException;
import java.util.Map;


// 제가 바보라서 게시글을 전부 board 표기했는데 post 로 표기해야 더 알맞을 것 같습니다. 우선순위가 급한 변경점은 아니기에 일단 그대로 진행하겠습니다.
@Controller
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private UserService userService;

    /**
     * 메인화면(로그인)
     */
    @GetMapping("/board") // localhost:8090/board/write
    public String boarMain() {
        return "boardMain";
    }
    /**
     * 회원가입
     */
    @GetMapping("/board/join") // localhost:8090/board/write
    public String boardJoin() {
        return "boardJoin";
    }

    /**
     * 회원가입 처리
     */
    @ResponseBody
    @PostMapping("/board/joinpro")
    public String boardJoinPro(UserDto userDto) {
        // 검증 필요
         boolean valid = userDto.isValid();

        // 저장
        userService.userJoin(userDto);

        return userDto.getUserid();
    }
    /**
     * 게시글 작성페이지
     */
    @GetMapping("/board/write") // localhost:8090/board/write
    public String boardWriteForm() {
        return "boardWrite"; //boardWrite.html
    }
    /**
     * 게시글 작성 처리
     */
    @ResponseBody
    @PostMapping(value = "/board/writepro", produces ="text/plain; charset=utf-8")
    public String boardWritePro(MultipartHttpServletRequest mrequest, Board board) throws IOException {

        System.out.println("도착");
        boolean a = boardService.write(mrequest, board);
        int result = 0;
        if(a){
            result=1;
        }

        // 글 제목 내용 체크
        System.out.println("제목" + board.getTitle());
        System.out.println("내용" + board.getContent());
        System.out.println("result" + result);




        return Integer.toString(result);
    }
    /**
     * 게시글 리스트
     */
    @GetMapping("/board/list")
    public String boardList(Model model, @PageableDefault(page = 0,size = 10,sort = "id",direction = Sort.Direction.DESC) Pageable pageable) {

        model.addAttribute("list",boardService.boardList(pageable));

        return "boardlist";
    }
    /**
     * 게시글 상세 페이지
     */
    @GetMapping("/board/view") //http://localhost:8090/board/view?id=1
    public String boardview(Model model , Integer id) {
        model = boardService.boardView(model, id);
        return "boardView";
    }

    /**
     * 게시글 삭제처리
     */
    @GetMapping("/board/delete")
    public String DeleteBoard(Integer id){
        boardService.deleteBoard(id);
        return "redirect:/board/list";
    }

    /**
     * 게시글 수정 페이지
     */
    @GetMapping("/board/edit")
    public String EditBoard(@RequestParam("id") Integer id , Model model){ // PathVariable 방법도 알아둘 것 -> 이거하면 쿼리스트링 깔끔해짐
        model = boardService.boardView(model, id);
        return "boardEdit";
    }

    /**
     * 게시글 수정 처리
     */
    // PutMapping 시도하려다가 405 에러로 일단 Post 처리(원인을 못찾겠음)
    @PostMapping("/board/editpro/{id}") // PutMappting 과 차이를 두는 이유 -> 멱등성(동일한 요청을 몇 번을 보내도 같은 효과를 보임)
    public String EditBoardPro(@PathVariable("id") Integer id , Board board){

        // 글 작성자인지 확인 필요

        boardService.editBoard(id,board);
        return "redirect:/board/list";
    }



}
