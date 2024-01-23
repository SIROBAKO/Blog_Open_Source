package com.Hako.web.service.Blog.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Hako.web.entity.Blog.Blog_Board;
import com.Hako.web.entity.Blog.Blog_Comment;
import com.Hako.web.service.Blog.BlogService;

@Service
public class JDBCBlogService implements BlogService {

	@Autowired
	private DataSource dataSource;

//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}

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

		List<Blog_Board> list = new ArrayList<>();

		String sql = "SELECT * FROM ( SELECT @ROWNUM := @ROWNUM + 1 AS ROWNUM, TMP.* \r\n"
				+ "FROM (SELECT * FROM BOARD_BLOG WHERE CATEGORY LIKE ? AND TITLE LIKE ? )TMP , \r\n"
				+ "(SELECT(@ROWNUM :=0)=0)TMP2 ORDER BY CREATE_DATE DESC)TMP3\r\n" + "WHERE ROWNUM BETWEEN ? AND ?";
		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, "%" + category + "%");
			st.setString(2, "%" + query + "%");
			st.setInt(3, 1 + (page - 1) * 6);
			st.setInt(4, page * 6);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int num = rs.getInt("NUM");
				String title = rs.getString("TITLE");
				String _category = rs.getString("CATEGORY");
				String contents = rs.getString("CONTENTS");
				Date date = rs.getTimestamp("CREATE_DATE");

				Blog_Board board = new Blog_Board(num, title, _category, contents, date);

				list.add(board);

			}

			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;

	}

//	게시글 수 구하기
	@Override
	public int getBoardCount() {

		return getBoardCount("title", "");
	}

	@Override
	public int getBoardCount(String category, String query) {

		int count = 0;

		String sql = "SELECT COUNT(*)AS COUNT FROM BOARD_BLOG WHERE CATEGORY LIKE ? AND TITLE LIKE?;";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, "%" + category + "%");
			st.setString(2, "%" + query + "%");
			ResultSet rs = st.executeQuery();

			if (rs.next())
				count = rs.getInt("COUNT");

			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return count;
	}

//	게시글 반환
	public Blog_Board getBoard(int id) {

		Blog_Board board = new Blog_Board();

		String sql = "SELECT * FROM BOARD_BLOG WHERE NUM = ?;";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				board.setNum(rs.getInt("NUM"));
				board.setTitle(rs.getString("TITLE"));
				board.setCategory(rs.getString("CATEGORY"));
				board.setContents(rs.getString("CONTENTS"));
				board.setDate(rs.getTimestamp("CREATE_DATE"));
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		ADDCount(board.getNum());

		return board;
	}

//	게시글 작성
	@Override
	public int InsertBoard(Blog_Board board) {

		String sql = "INSERT INTO BOARD_BLOG (TITLE,CATEGORY,CONTENTS,COMMENT,COUNT) VALUES(?,?,?,0,0)";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, board.getTitle());
			st.setString(2, board.getCategory());
			st.setString(3, board.getContents());
			st.execute();

			st.close();
			con.close();

			return 1;
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}

	}

//	게시글 수정
	@Override
	public int UpdateBoard(Blog_Board board, int id) {

		String sql = "UPDATE  BOARD_BLOG  SET TITLE= ?, CATEGORY= ?, CONTENTS= ? WHERE NUM=?";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, board.getTitle());
			st.setString(2, board.getCategory());
			st.setString(3, board.getContents());
			st.setInt(4, id);
			st.execute();

			st.close();
			con.close();

			return 1;
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}

	}

//	게시글 삭제
	@Override
	public int DelBoard(int id) {

		String sql = "DELETE FROM BOARD_BLOG WHERE NUM=?";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);

			st.execute();

			st.close();
			con.close();

			return 1;
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}

	}

//	조회수  상승
	@Override
	public void ADDCount(int id) {

		String sql = "UPDATE BOARD_BLOG SET COUNT = 1+ COUNT WHERE NUM = ?";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			st.execute();

			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

//	댓글 수 상승
	@Override
	public void ADDComment(int id) {
		String sql = "UPDATE BOARD_BLOG SET COMMENT = 1+ COMMENT WHERE NUM = ?";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			st.execute();

			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

//	댓글 수 감소
	@Override
	public void SUBComment(int id) {
		String sql = "UPDATE BOARD_BLOG SET COMMENT =  COMMENT -1 WHERE NUM = ?";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			st.execute();

			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

//	추천 게시물 반환
	@Override
	public List<Blog_Board> getCommendBoard() {

		List<Blog_Board> list = new ArrayList<Blog_Board>();

		String sql = "	SELECT * FROM BOARD_BLOG ORDER BY COUNT DESC LIMIT 5";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int num = rs.getInt("NUM");
				String title = rs.getString("TITLE");

				Blog_Board board = new Blog_Board(num, title, null, null, null);

				list.add(board);

			}

			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}

//	다음게시물 반환
	public List<Blog_Board> getCommendNextBoard(String category,int num, int count) {

		List<Blog_Board> list = new ArrayList<Blog_Board>();

		String sql = "SELECT * FROM (SELECT * FROM BOARD_BLOG\r\n"
				+ "WHERE CREATE_DATE >\r\n"
				+ "(SELECT CREATE_DATE FROM BOARD_BLOG WHERE CATEGORY LIKE ? AND NUM = ?)\r\n"
				+ " LIMIT ?)TMP ORDER BY CREATE_DATE DESC;";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, "%"+category+"%");
			st.setInt(2, num);
			st.setInt(3, count);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int _num = rs.getInt("NUM");
				String title = rs.getString("TITLE");

				Blog_Board board = new Blog_Board(_num, title, null, null, null);

				list.add(board);
			}

			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}
	
//	다음게시물 반환
	public List<Blog_Board> getCommendPrevBoard(String category,int num, int count) {

		List<Blog_Board> list = new ArrayList<Blog_Board>();

		String sql = "SELECT * FROM BOARD_BLOG\r\n"
				+ "WHERE CREATE_DATE <\r\n"
				+ "(SELECT CREATE_DATE FROM BOARD_BLOG WHERE CATEGORY LIKE ? AND NUM = ? \r\n"
				+ " )ORDER BY CREATE_DATE DESC LIMIT ?;";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, "%"+category+"%");
			st.setInt(2, num);
			st.setInt(3, count);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int _num = rs.getInt("NUM");
				String title = rs.getString("TITLE");

				Blog_Board board = new Blog_Board(_num, title, null, null, null);
		
				list.add(board);
			}

			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}

//	게시글 제목 반환(게시글 num 기반)
	@Override
	public String getBoardTitle(int id) {

		String title = null;
		String sql = "SELECT * FROM BOARD_BLOG WHERE NUM = ?";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				title = rs.getString("TITLE");
			}
			rs.close();
			st.close();
			con.close();

			return title;
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			return "0";
		}

	}

//	댓글 작성
	@Override
	public int InsertComment(Blog_Comment comment, int num, int ref_comment) {

		if (comment.getUser_name().equals("NULL")) {
			return 2;
		}
		String sql = "INSERT INTO COMMENT_BLOG (REF,REF_COMMENT,COMMENT, USER_NAME,PASSWORD  )\r\n"
				+ "values(?,?,?,?,?);";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, num);
			st.setInt(2, ref_comment);
			st.setString(3, comment.getComment());
			st.setString(4, comment.getUser_name());
			st.setString(5, comment.getPwd());
			st.execute();

			st.close();
			con.close();

			ADDComment(num);

			return 1;
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}

	}

//	댓글 수정	
	@Override
	public int UpdateComment(Blog_Comment comment, int id, String pwd) {
		String sql = "UPDATE  COMMENT_BLOG  SET COMMENT=?,USER_NAME=?  WHERE ID=?; ";

		String select_sql = "SELECT * FROM COMMENT_BLOG WHERE ID=? ";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(select_sql);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();

			String _pwd = null;
			if (rs.next()) {
				_pwd = rs.getString("PASSWORD");

			}

			if (!BCrypt.checkpw(pwd, _pwd)) {
				st.close();
				rs.close();
				con.close();
				return 2;
			} else {
				con = dataSource.getConnection();
				st = con.prepareStatement(sql);
				st.setString(1, comment.getComment());
				st.setString(2, comment.getUser_name());
				st.setInt(3, id);
				st.execute();
				st.close();

				rs.close();
				st.close();
				con.close();

				return 1;
			}

		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
	}

//	댓글 삭제

	@SuppressWarnings("resource")
	@Override
	public int DelComment(int id, String pwd) {

		String sql = "DELETE FROM COMMENT_BLOG WHERE ID=? ";

		String select_sql1 = "SELECT * FROM COMMENT_BLOG WHERE ID=? ";

		String select_sql2 = "SELECT COUNT(*)AS COUNT FROM COMMENT_BLOG WHERE REF_COMMENT=? ";

		String update_sql = "UPDATE  COMMENT_BLOG  SET USER_NAME=?, COMMENT=? WHERE ID=? ";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(select_sql1);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();

			int ref = 0;
			int ref_comment = 0;
			int count = 0;

			String _pwd = null;
			if (rs.next()) {
				_pwd = rs.getString("PASSWORD");
				ref = Integer.parseInt(rs.getString("REF"));
				ref_comment = Integer.parseInt(rs.getString("REF_COMMENT"));

			}
			// 비밀번호 불일치
			if (!BCrypt.checkpw(pwd, _pwd)) {
				st.close();
				rs.close();
				con.close();
				return 2;
			}
			// 최상위 댓글 일시
			if (ref_comment == 0) {
				st = con.prepareStatement(select_sql2);
				st.setInt(1, id);
				rs = st.executeQuery();

				if (rs.next())
					count = rs.getInt("count");

				// 대댓이 없을시 삭제
				if (count == 0) {
					st = con.prepareStatement(sql);
					st.setInt(1, id);
					st.execute();

					rs.close();
					st.close();
					con.close();

					SUBComment(ref);

					return 1;
					// 대댓 있을 시 삭제 댓글로 업데이트
				} else {
					st = con.prepareStatement(update_sql);
					st.setString(1, "NULL");
					st.setString(2, "삭제된 댓글 입니다.");
					st.setInt(3, id);
					st.execute();

					rs.close();
					st.close();
					con.close();

					SUBComment(ref);

					return 3;
				}
				// 대댓일때
			} else {
				// 대댓 삭제
				st = con.prepareStatement(sql);
				st.setInt(1, id);
				st.execute();

				// 삭제된 댓글에서 마지막 대댓글일때 검사
				st = con.prepareStatement(select_sql2);
				st.setInt(1, ref_comment);
				rs = st.executeQuery();

				if (rs.next())
					count = rs.getInt("COUNT");

				if (count == 0) {
					// 맞으면 전부 삭제
					st = con.prepareStatement(sql);
					st.setInt(1, ref_comment);
					st.execute();
				}

				st.close();
				rs.close();
				con.close();

				SUBComment(ref);

				return 1;
			}

		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
	}

	// 댓글 리스트 반환
	@Override
	public List<Blog_Comment> getCommentList(int num) {
		List<Blog_Comment> list = new ArrayList<>();

		String sql = "SELECT *FROM COMMENT_BLOG WHERE REF =? ORDER BY CREATE_DATE DESC";

		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection con = DriverManager.getConnection(url, user, pwd);

			Connection con = dataSource.getConnection();
			PreparedStatement st = con.prepareStatement(sql);

			st.setInt(1, num);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("ID");
				int ref = rs.getInt("REF");
				int ref_comment = rs.getInt("REF_COMMENT");
				String _comment = rs.getString("COMMENT");
				Date create_date = rs.getTimestamp("CREATE_DATE");

				String user_name = rs.getString("USER_NAME");

				Blog_Comment comment = new Blog_Comment(id, ref, ref_comment, _comment, create_date, user_name, "");

				list.add(comment);

			}

			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return list;
	}

}
