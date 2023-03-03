package com.study.board.controller;

import com.study.board.Dto.CommentDto;
import com.study.board.Dto.MessageDto;
import com.study.board.Dto.UserDto;
import com.study.board.entity.Board;
import com.study.board.entity.Boardfile;
import com.study.board.entity.Comment;
import com.study.board.entity.User;
import com.study.board.repository.BoardFileRepository;
import com.study.board.repository.BoardRepository;
import com.study.board.repository.UserRepository;
import com.study.board.service.BoardService;
import com.study.board.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.*;
import java.util.Map;
import java.util.Optional;


// 제가 바보라서 게시글을 전부 board 표기했는데 post 로 표기해야 더 알맞을 것 같습니다. 우선순위가 급한 변경점은 아니기에 일단 그대로 진행하겠습니다.
@Controller
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardFileRepository boardFileRepository;

    /**
     * 메인화면(로그인)
     */
    @GetMapping("/board") // localhost:8090/board/write
    public String boarMain(Model model) {
        model.addAttribute("userDto",new UserDto());

        return "boardMain";
    }

    /**
     * 로그인 처리
     */
    @PostMapping("/board/loginpro")
    public String login(@Valid UserDto userDto , BindingResult result , HttpSession session){
        if(result.hasErrors()){
            return "redirect:/board";
        }
        User loginuser = userService.login(userDto);

        if(loginuser == null){ // 비밀번호 오류 or 아이디 오류(보안 위해 구분x)
            result.addError(new FieldError("userDto","userid","잘못된 로그인 정보입니다. 다시 입력해주세요."));
            return "redirect:/board";

        }

        // 모두 통과시 session 에 값 저장
        session.setAttribute("userid",userDto.getUserid());
        session.setAttribute("loginuser",loginuser.toDto());

        return "redirect:/board/list";
    }

    /**
     * 로그아웃
     */
    @GetMapping("/board/logout") // localhost:8090/board/write
    public String logout(HttpSession session) {
        session.removeAttribute("userid");
        session.removeAttribute("loginuser");
        return "redirect:/board";
    }


    /**
     * 회원가입
     */
    @GetMapping("/board/join") // localhost:8090/board/write
    public String boardJoin(Model model) {

        model.addAttribute("userDto",new UserDto());

        return "boardJoin";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/board/joinpro")
    public String boardJoinPro(@Valid UserDto userDto, BindingResult result, HttpSession session) {
        // 기본적인 정규식 조건은 Valid 로 구분 중복값만 service 에서 체크

        if(result.hasErrors()){
            return "boardJoin";
        }
        try{
            userService.userJoin(userDto , session); // 저장도 하고나옴 나중에는 이러지 말자
        }catch (IllegalStateException e){

            result.addError(new FieldError("userDto","userid",e.getMessage()));

            return "boardJoin";
        }



        return "redirect:/board/list";
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
    public String boardWritePro(MultipartHttpServletRequest mrequest, Board board, HttpSession session) throws IOException {


        board.setUserid((String)session.getAttribute("userid"));
        boolean a = boardService.write(mrequest, board);
        int result = 0;
        if(a){
            result=1;
        }

        // 글 제목 내용 체크
        return Integer.toString(result);
    }
    /**
     * 게시글 리스트
     */
    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0,size = 10,sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                            String searchType,
                            String searchWord) {
        Page<Board> list;
        if(searchWord != null){
            list = boardService.boardListWithSearch(pageable,searchType,searchWord);
        }
        else{
            list = boardService.boardList(pageable);// 검색x
        }
        int nowPage = list.getPageable().getPageNumber() +1; // pageable 0부터 시작함
        int startPage = Math.max(nowPage-4,1);
        int endPage = Math.min(nowPage+4, list.getTotalPages());
        int maxPage = list.getTotalPages();

        System.out.println("startPage"+startPage);
        System.out.println("endPage"+endPage);
        System.out.println("list.getTotalPages()"+list.getTotalPages());
        System.out.println("maxPage"+maxPage);
        model.addAttribute("list",list);
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        model.addAttribute("maxPage",maxPage);

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
    public String DeleteBoard(Integer id, HttpSession session, Model model){
        Board board = boardRepository.findById(id).get();


        /*if(boardRepository.findById(id).get().getUser().getUserid().equals(session.getAttribute("userid"))){ // 아이디가 같아야지만 삭제가 진행되도록
            boardService.deleteBoard(id);
        }
        else{
            MessageDto msg = new MessageDto("삭제 권한이 없는 글입니다.","/");
            model.addAttribute("message", msg);
            return "board/message";
        }
*/

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

    /**
     * 댓글달기
     */
    @PostMapping("/board/addCommentpro")
    @ResponseBody
    public String addComment(CommentDto comment, HttpSession session){


        comment.setUserid((String)session.getAttribute("userid"));

        String result = boardService.addComment(comment);


        return result;
    }

    /**
     * 답글달기
     */
    @PostMapping("/board/addRcommentpro")
    @ResponseBody
    public String addRcomment(CommentDto comment, HttpSession session){

        comment.setUserid((String)session.getAttribute("userid"));
        String result = boardService.addRcomment(comment);

        return result;
    }
    /**
     * 댓글 삭제
     */
    @PostMapping("/board/delCommentpro")
    @ResponseBody
    public String delCommentpro(Model model , String commentid) {

        String result = boardService.deleteComment(commentid);
        return result;
    }

    /**
     * 댓글 수정
     */
    @PostMapping("/board/editCommentpro")
    @ResponseBody
    public String editCommentpro(CommentDto commentDto) {

        System.out.println("commentDto"+commentDto);

        String result = boardService.editComment(commentDto);
        return result;
    }

    /**
     *  !!!! 관리자 페이지
     */
    @GetMapping("/board/admin")
    public String admin(HttpSession session, Model model){
        String loginUser = (String)session.getAttribute("userid");
        if((loginUser != null) && (!loginUser.isEmpty()) ){
            if(userService.checkAdmin(loginUser) ){

                model.addAttribute("userList",  userService.getUserList());
                return "admin";
            }
        }

        return "redirect:/board";

    }

    /**
     * 회원정보 수정창
     */
    @GetMapping("/board/admin/useredit/{userid}")
    public String admin(@PathVariable("userid") String userid , Model model){

        model.addAttribute("userDto",userRepository.findByUserid(userid).get(0).toDto());
        return "adminUserEdit";
    }

    /**
     * 회원정보 수정
     */
    @PostMapping("/board/admin/usereditPro")
    public String usereditPro(@Valid UserDto user, BindingResult result, Model model) {
        // 기본적인 정규식 조건은 Valid 로 구분 중복값만 service 에서 체크
        if(result.hasErrors()){
            return "adminUserEdit";
        }

        userService.userEdit(user);
        return "redirect:/board/admin";
    }

    /**
     * 유저 삭제
     */
    @PostMapping("/board/admin/userdeletePro")
    public String userdeletePro(Long id) {
        // 기본적인 정규식 조건은 Valid 로 구분 중복값만 service 에서 체크

        userRepository.delete(userRepository.findById(id).get());

        return "redirect:/board/admin";
    }

    /**
     * 첨부파일 다운로드
     */
    @RequestMapping(value="/board/download")
    public void download(HttpServletRequest request, HttpServletResponse response){
        Integer fileid = Integer.valueOf(request.getParameter("fileid"));
        String orgFilename = null;
        response.setContentType("text/html; charset=UTF-8");

        try {
            Boardfile file = boardFileRepository.findByBoardfileid(fileid);
            File downloadFile = new File(file.getFilepath(), file.getFilename());

            try {
                orgFilename = new String(file.getOrgfilename().getBytes("UTF-8"), "8859_1");
                // orgFilename.getBytes("UTF-8") 은 UTF-8 형태로 되어진 문자열 orgFilename 을 byte 형태로 변경한 후
                // byte 형태로 되어진 것을 표준인 ISO-Latin1(혹은 Latin1 또는 8859_1) 형태로 인코딩한 문자열로 만든다.
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }



            if(downloadFile.exists()){ // 경로에 다운로드할 해당 파일이 있다면


                response.setContentType("application/octet-stream");
                response.setHeader("Content-disposition", "attachment; filename="+orgFilename);
                byte[] readByte = new byte[4096];
                BufferedInputStream bfin = new BufferedInputStream(new FileInputStream(downloadFile));

                ServletOutputStream souts = response.getOutputStream();
                int length = 0;

                while( (length = bfin.read(readByte, 0, 4096)) != -1  ) {
                    souts.write(readByte, 0, length);
                }
                souts.flush(); // ServletOutputStream souts 에 기록(저장)해둔 내용을 클라이언트로 내본다.

                souts.close(); // ServletOutputStream souts 객체를 소멸시킨다.
                bfin.close();  // BufferedInputStream bfin 객체를 소멸시킨다.
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }








}
