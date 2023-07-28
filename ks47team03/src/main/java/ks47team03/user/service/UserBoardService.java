package ks47team03.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ks47team03.user.dto.Board;
import ks47team03.user.mapper.UserBoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserBoardService {

    @Autowired
    private UserBoardMapper userBoardMapper;

    // 게시글 작성
    public void communityBoardWrite(Board board, HttpServletRequest request){
        // 게시글 작성 날짜 추가
        board.setBoardDatetime(LocalDateTime.now());

        // 현재 로그인된 사용자 ID 가져오기
        HttpSession session = request.getSession();
        String currentUserId = (String) session.getAttribute("SID");

        // 게시글 작성자 ID 설정
        board.setUser_id(currentUserId);

        userBoardMapper.save(board);
    }
    // 게시글 리스트
    public List<Board> communityBoardView(){

        return userBoardMapper.findAll();
    }
    // 게시글 상세 조회
    public Board communityBoardDetail(String boardCode){

        return userBoardMapper.findById(boardCode).get();
    }

    // 게시글 삭제
    public void communityBoardDelete(String boardCode){

        //userBoardMapper.deleteById(boardCode); 일단 주석 처리
        deleteBoard(boardCode);
    }
    // 게시글 카운트
    public long communityBoardCount(){

        return userBoardMapper.count();
    }
    // 게시글 조회 수 증가 후 저장
    public void saveBoard(Board board) {

        userBoardMapper.save(board);
    }
    // isDelete 컬럼 0,1
    public List<Board> getBoards() {
        return userBoardMapper.findByIsDeletedFalse();
    }
    // isDelete 컬럼 0,1
    public void deleteBoard(String boardId) {
        Board board = userBoardMapper.findById(boardId).orElseThrow(() -> new RuntimeException("Board not found"));
        board.setDeleted(true);
        userBoardMapper.save(board);
    }
}
