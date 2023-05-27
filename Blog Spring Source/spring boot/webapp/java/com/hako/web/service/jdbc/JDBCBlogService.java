package com.hako.web.blog.service.jdbc;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hako.web.blog.dao.BoardDao;
import com.hako.web.blog.dao.CommentDao;
import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;
import com.hako.web.blog.entity.Blog_Comment;
import com.hako.web.blog.service.BlogService;

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
	public List<Blog_Board> getBoardList(String hidden) {
		return getBoardList("", "", 1, hidden);
	}

	@Override
	public List<Blog_Board> getBoardList(int page, String hidden) {

		return getBoardList("", "", page, hidden);
	}

	@Override
	public List<Blog_Board> getBoardList(String category, String query, int page, String hidden) {

		int start = 1 + (page - 1) * 6;
		int end = page * 6;

		return boardDao.getList(category, query, start, end, hidden);

	}

//	게시글 수 구하기
	@Override
	public int getBoardCount(String hidden) {

		return getBoardCount("title", "", hidden);
	}

	@Override
	public int getBoardCount(String category, String query, String hidden) {

		return boardDao.getCount(category, query, hidden);
	}

//	게시글 반환
	public Blog_Board getBoard(int id) {

		return boardDao.get(id);
	}

	@Override
	public Blog_Board getBoard() {
		
		return boardDao.getLast();
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
	public int UpdateComment(Blog_Comment comment) {

		// 비밀번호 불일치
		if (CheckUser(comment.getNum(), comment.getPwd()) == 0) {
			return 2;
		} else {
			return commentDao.update(comment);
		}
	}

//	댓글 삭제
	@Override
	public int DelComment(Blog_Comment comment) {

		// 비밀번호 불일치
		if (CheckUser(comment.getNum(), comment.getPwd()) == 0) {
			return 2;
		} else {

			Blog_Comment _comment = commentDao.get(comment.getNum());

			// 최상위 댓글 일시
			if (_comment.getRef_comment() == 0) {

				// 대댓글이 없을 시
				if (commentDao.getRefCount(_comment.getNum()) == 0) {
					// 삭제
					return commentDao.del(_comment.getNum());

					// 대댓글이 있을 때 업데이트
				} else {
					Blog_Comment update = new Blog_Comment();
					update.setUser_name("NULL");
					update.setComment("삭제된 댓글 입니다.");
					update.setNum(_comment.getNum());

					if (commentDao.update(update) == 1) {
						return 3;
					} else {
						return 0;
					}

				}
				// 대댓글 일때
			} else {
				// 대댓글 삭제
				if (commentDao.del(_comment.getNum()) == 1) {

					// 삭제후 최상위 댓글이 삭제된 댓글이라면
					System.out.println(commentDao.getRefCount(_comment.getRef_comment()));
					if (commentDao.getRefCount(_comment.getRef_comment()) == 0) {
						Blog_Comment refComment = commentDao.get(_comment.getRef_comment());

						if (refComment.getUser_name().equals("NULL")) {
							// 이 댓글도 삭제
							return commentDao.del(refComment.getNum());
						}
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
	private int CheckUser(int num, String pwd) {

		String _pwd = commentDao.get(num).getPwd();

		if (!BCrypt.checkpw(pwd, _pwd)) {
			return 0;
		}
		return 1;

	}

	@Override
	public List<Blog_Category> getCategory() {
		// TODO Auto-generated method stub
		return boardDao.getCategory();
	}

	
}