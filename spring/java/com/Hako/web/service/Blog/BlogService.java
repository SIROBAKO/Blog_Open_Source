package com.Hako.web.service.Blog;

import java.util.List;

import com.Hako.web.entity.Blog.Blog_Board;
import com.Hako.web.entity.Blog.Blog_Comment;

public interface BlogService {

//	�Խù� ��� ��ȯ
	List<Blog_Board> getBoardList();

	List<Blog_Board> getBoardList(int page);

	List<Blog_Board> getBoardList(String category, String query, int page);

//	�Խù� ���� ��ȯ
	int getBoardCount();

	int getBoardCount(String category, String query);

//	�Խù� ��ȯ
	Blog_Board getBoard(int id);

//	�Խù����
	int InsertBoard(Blog_Board board);

//	�Խù�����
	int UpdateBoard(Blog_Board board, int id);

//	�Խù� ����
	int DelBoard(int id);

//	��õ�Խù�(��ȸ�����) ��ȯ
	List<Blog_Board> getCommendBoard();
	
//   ���� �Խù� ��ȯ
	 List<Blog_Board> getCommendNextBoard(String category,int num, int count);

//   ���� �Խù� ��ȯ
	 List<Blog_Board> getCommendPrevBoard(String category,int num, int count);

//	���� ��ȯ
	String getBoardTitle(int id);
	
//	�Խñ� ��ȸ�� ���
	void ADDCount(int id);
	
//	�Խñ� ��� �� ���
	void ADDComment(int id);
	
//	�Խñ� ��� �� ����
	void SUBComment(int id);

//	����ۼ�
	int InsertComment(Blog_Comment comment, int num, int ref_comment);

//	��� ����
	int UpdateComment(Blog_Comment comment, int id, String pwd);

//	��� ����
	int DelComment(int id, String pwd);
	
//	��� ��ȯ
	List<Blog_Comment> getCommentList(int num);
	
	

	
}
