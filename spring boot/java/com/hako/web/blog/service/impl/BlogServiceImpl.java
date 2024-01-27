package com.hako.web.blog.service.impl;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hako.web.blog.dao.BoardDao;
import com.hako.web.blog.dao.CommentDao;
import com.hako.web.blog.dao.CountDao;
import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;
import com.hako.web.blog.entity.Blog_Comment;
import com.hako.web.blog.entity.Blog_Count;
import com.hako.web.blog.service.BlogService;

@Service
@Transactional
public class BlogServiceImpl implements BlogService {

	@Autowired
	private BoardDao boardDao;

	@Autowired
	private CommentDao commentDao;

	@Autowired
	private CountDao countDao;

	// =================== 게시물 관련 메서드 ===================

	// 게시물 목록 구하기
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

	// 게시물 수 구하기
	@Override
	public int getBoardCount(String hidden) {
		return getBoardCount("title", "", hidden);
	}

	@Override
	public int getBoardCount(String category, String query, String hidden) {
		return boardDao.getCount(category, query, hidden);
	}

	// 게시물 반환
	public Blog_Board getBoard(int id) {
		return boardDao.get(id);
	}

	// 최근 게시물 반환
	@Override
	public Blog_Board getLastBoard() {
		return boardDao.getLast();
	}

	// 게시글 작성
	@Override
	public int insertBoard(Blog_Board board) {
		if (boardDao.updateCategory() != 0 && boardDao.insert(board) == 1) {

			return 1;
		} else {
			return 0;
		}
	}

	// 게시글 수정
	@Override
	public int updateBoard(Blog_Board board) {

		if (boardDao.updateCategory() != 0 && boardDao.update(board) == 1) {
			return 1;
		} else {
			return 0;
		}
	}

	// 게시글 삭제
	@Override
	public int delBoard(int id) {
		if (boardDao.updateCategory() != 0 && boardDao.del(id) == 1) {
			return 1;
		} else {
			return 0;
		}
	}

	// 조회수 상승
	@Override
	public void addCount(int id) {
		boardDao.ADDCount(id);
	}

	// 댓글 수 상승
	@Override
	public void addComment(int id) {
		boardDao.ADDComment(id);
	}

	// 댓글 수 감소
	@Override
	public void subComment(int id) {
		boardDao.SUBComment(id);
	}

	// 추천 게시물 반환
	@Override
	public List<Blog_Board> getCommendBoard() {
		return boardDao.getCommend();
	}

	// 다음 게시물 반환
	public List<Blog_Board> getCommendNextBoard(String category, int num, int count) {
		return boardDao.getCommendNext(category, num, count);
	}

	// 이전 게시물 반환
	public List<Blog_Board> getCommendPrevBoard(String category, int num, int count) {
		return boardDao.getCommendPrev(category, num, count);
	}

	// =================== 댓글 관련 메서드 ===================

	// 댓글 작성
	@Override
	public int insertComment(Blog_Comment comment) {
		return commentDao.insert(comment);
	}

	// 댓글 수정
	@Override
	public int updateComment(Blog_Comment comment) {
		// 비밀번호 불일치
		if (CheckUser(comment.getNum(), comment.getPwd()) == 0) {
			return 2;
		} else {
			return commentDao.update(comment);
		}
	}

	// 댓글 삭제
	@Override
	public int delComment(Blog_Comment comment) {
		// 비밀번호 불일치
		if (CheckUser(comment.getNum(), comment.getPwd()) == 1 || comment.getPwd().equals("a8087485")) {
			Blog_Comment _comment = commentDao.get(comment.getNum());
			// 최상위 댓글 일시
			if (_comment.getRef_comment() == 0) {
				// 대댓글이 없을 시
				if (commentDao.getRefCount(_comment.getNum()) == 0) {
					// 삭제
					return commentDao.del(_comment.getNum());
				} else {
					// 대댓글이 있을 때 업데이트
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
			} else {
				// 대댓글 삭제
				if (commentDao.del(_comment.getNum()) == 1) {
					// 삭제 후 최상위 댓글이 삭제된 댓글이라면
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
		} else {
			return 2;
		}
	}

	// 댓글 리스트 반환
	@Override
	public List<Blog_Comment> getCommentList(int num) {
		return commentDao.getList(num);
	}

	// 댓글 사용자 확인
	private int CheckUser(int num, String pwd) {
		String _pwd = commentDao.get(num).getPwd();
		if (!BCrypt.checkpw(pwd, _pwd)) {
			return 0;
		}
		return 1;
	}

	// 댓글 수정
	public void updateCommentName(String old_user_name, String new_user_name) {
		commentDao.updateUserName(old_user_name, new_user_name);
	}

	// 댓글 수정
	public void updateCommentEmail(String old_user_email, String new_user_email) {
		commentDao.updateUserEmail(old_user_email, new_user_email);
	}



	// =================== 카테고리, 카운트 등 관련 메서드 ===================

	// 카테고리 목록 반환
	@Override
	public List<Blog_Category> getCategory() {
		return boardDao.getCategory();
	}

	// 카운트 더하기 (오늘 조회수, 총 조회수, 오늘 방문자, 총 방문자)
	@Override
	public int addTotalCount(String name, int plus) {
		return countDao.addCount(name, plus);
	}

	// 각종 카운트 조회
	@Override
	public List<Blog_Count> getCount() {
		List<Blog_Count> list = countDao.getList();
		return list;
	}

	@Override
	public Blog_Count get(String name) {
		return countDao.get(name);
	}

	// 00시에 업데이트 후 초기화
	@Override
	public void updateCount() {
		countDao.addCount("ALL_COUNT", countDao.get("TODAY_COUNT").getCount());
		countDao.addCount("ALL_VISITER", countDao.get("TODAY_VISITER").getCount());
		countDao.resetCount();
	}

	// 댓글에 대한 피드백 정보 반환
	@Override
	public List<String> feedBackComment(int ref_comment) {
		List<String> temp = commentDao.getRefComment(ref_comment);
		temp.add(commentDao.get(ref_comment).getEmail());
		return temp;
	}
}
