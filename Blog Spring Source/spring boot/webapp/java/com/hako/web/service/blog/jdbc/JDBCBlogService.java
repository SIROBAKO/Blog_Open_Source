package com.hako.web.service.blog.jdbc;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hako.web.dao.blog.BoardDao;
import com.hako.web.dao.blog.CommentDao;
import com.hako.web.entity.blog.Blog_Board;
import com.hako.web.entity.blog.Blog_Comment;
import com.hako.web.service.blog.BlogService;

@Service
public class JDBCBlogService implements BlogService {

//	boot properties로 대체
//	@Autowired
//	private DataSource dataSource;

	@Autowired
	private BoardDao boardDao;

	@Autowired
	private CommentDao commentDao;

//	게시글 목록 구하기
	@Override
	public List<Blog_Board> getBoardList() {
		return getBoardList("", "", 1);
	}

	@Override
	public List<Blog_Board> getBoardList(int page) {

		return getBoardList("", "", page);
	}

	@Override
	public List<Blog_Board> getBoardList(String category, String query, int page) {

		int start = 1 + (page - 1) * 6;
		int end = page * 6;

		return boardDao.getList(category, query, start, end);

	}

//	게시글 수 구하기
	@Override
	public int getBoardCount() {

		return getBoardCount("title", "");
	}

	@Override
	public int getBoardCount(String category, String query) {

		return boardDao.getCount(category, query);
	}

//	게시글 반환
	public Blog_Board getBoard(int id) {

		ADDCount(id);

		return boardDao.get(id);
	}

//	조회수  상승
	@Override
	public void ADDCount(int id) {

		boardDao.ADDCount(id);

	}

//	댓글 수 상승
	@Override
	public void ADDComment(int id) {

		boardDao.ADDComment(id);

	}

//	댓글 수 감소
	@Override
	public void SUBComment(int id) {

		boardDao.SUBComment(id);

	}

//	추천 게시물 반환
	@Override
	public List<Blog_Board> getCommendBoard() {

		return boardDao.getCommend();
	}

//	다음게시물 반환
	public List<Blog_Board> getCommendNextBoard(String category, int num, int count) {

		return boardDao.getCommendNext(category, num, count);
	}

//	다음게시물 반환
	public List<Blog_Board> getCommendPrevBoard(String category, int num, int count) {

		return boardDao.getCommendPrev(category, num, count);
	}

//	게시글 작성
	@Override
	public int InsertBoard(Blog_Board board) {

		return boardDao.insert(board);

	}

//	게시글 수정
	@Override
	public int UpdateBoard(Blog_Board board) {

		return boardDao.update(board);

	}

//	게시글 삭제
	@Override
	public int DelBoard(int id) {

		return boardDao.del(id);

	}

//	댓글 작성
	@Override
	public int InsertComment(Blog_Comment comment) {

		return commentDao.insert(comment);

	}

//	댓글 수정	
	@Override
	public int UpdateComment(Blog_Comment comment, int id, String pwd) {

		// 비밀번호 불일치
		if (CheckUser(id, pwd) == 0) {
			return 2;
		} else {
			return commentDao.update(comment);
		}
	}

	
//	댓글 삭제
	@Override
	public int DelComment(int id, String pwd) {

		// 비밀번호 불일치
		if (CheckUser(id, pwd) == 0) {
			return 2;
		} else {
			
			Blog_Comment comment = commentDao.get(id);

			
			// 최상위 댓글 일시
			if (comment.getRef_comment() == 0) {
			
				// 대댓글이 없을 시
				if (commentDao.getRefCount(id) == 0) {
					// 삭제
					return commentDao.del(id);
					
					// 대댓글이 있을 때 업데이트
				} else {
					Blog_Comment update = new Blog_Comment();
					update.setUser_name("NULL");
					update.setComment("삭제된 댓글 입니다.");
					update.setId(id);
					
					if(commentDao.update(update) == 1) {
						return 3;
					}else {
						return 0;
					}
					
				}
				// 대댓글 일때
			} else {
				// 대댓글 삭제
				if (commentDao.del(id) == 1) {

					// 삭제후 최상위 댓글이 삭제된 댓글이라면
					Blog_Comment refComment = commentDao.get(comment.getRef_comment());
					
					if (refComment.getUser_name().equals("NULL")) {
						// 이 댓글도 삭제
					    return commentDao.del(refComment.getId());
					}
					return 1;
				}
				return 0;
			}
		}
	}

	
	// 댓글 리스트 반환
	@Override
	public List<Blog_Comment> getCommentList(int num) {
		
		return commentDao.getList(num);
	}

//	댓글 사용자 확인
	private int CheckUser(int id, String pwd) {
		
		String _pwd = commentDao.get(id).getPwd();

		if (!BCrypt.checkpw(pwd, _pwd)) {
			return 0;
		}
		return 1;

	}
}