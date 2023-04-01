package com.Hako.web.controller.Blog;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Hako.web.entity.Blog.Blog_Board;
import com.Hako.web.entity.Blog.Blog_Comment;
import com.Hako.web.service.Blog.BlogService;

@Controller
@RequestMapping("/")
public class BlogController {

	@Autowired
	private BlogService blogService;

	@RequestMapping("index")
	public String BlogIndex(Model model) {
		List<Blog_Board> list = blogService.getBoardList();
		List<Blog_Board> commend = blogService.getCommendBoard();
		model.addAttribute("list", list);
		model.addAttribute("commend", commend);
		return "Blog.index";
	}

	@RequestMapping("detail")
	public String BlogDetail(HttpServletRequest req, Model model, String num) {

		if (num == null) {
			req.setAttribute("msg", "존재하지 않는 게시물 입니다.");
			req.setAttribute("url", "/index");
			return "alert";
		} else {
			int _num = Integer.parseInt(num);
			Blog_Board board = blogService.getBoard(_num);
			List<Blog_Comment> comment = blogService.getCommentList(_num);
			model.addAttribute("comment", comment);
			List<Blog_Comment> recomment = blogService.getCommentList(_num);
			Collections.reverse(recomment);
			List<Blog_Board> commend = blogService.getCommendRandBoard();
			model.addAttribute("commend", commend);

			model.addAttribute("recomment", recomment);

			if (board.getTitle() == null) {
				req.setAttribute("msg", "존재하지 않는 게시물 입니다.");
				req.setAttribute("url", "/index");
				return "alert";
			}

			model.addAttribute("board", board);

			return "Blog.nonaside.detail";
		}

	}

	@RequestMapping("list")
	public String BlogList(HttpServletRequest req, Model model,
			@RequestParam(name = "page", defaultValue = "1") int page, String category, String title) {

		category = (category == null) ? "" : category;
		title = (title == null) ? "" : title;

		List<Blog_Board> list = blogService.getBoardList(category, title, page);
		List<Blog_Board> commend = blogService.getCommendBoard();

		int count = blogService.getBoardCount(category, title);

		if (list.isEmpty()) {
			req.setAttribute("msg", "게시글이 없습니다..");
			req.setAttribute("url", "/index");
			return "alert";
		}

		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("commend", commend);

		return "Blog.list";
	}

	@RequestMapping("Comment")
	@ResponseBody
	public int CommentHandler(@RequestBody Map<String, Object> form) {

		int id = (int) form.get("id");
		String pwd = ((String) form.get("pwd") == null) ? "" : (String) form.get("pwd");

		if (form.get("doit").equals("insert")) {
			if ((form.get("user_name").equals("HAKO") || form.get("user_name").equals("NULL"))) {
				if (!pwd.equals("a8087485")) {
					return 2;
				}
			}
			int num = (int) form.get("num");
			pwd = BCrypt.hashpw(cleanXSS(pwd), BCrypt.gensalt());

			Blog_Comment comment = new Blog_Comment();
			comment.setUser_name(cleanXSS((String) form.get("user_name")));
			comment.setPwd(pwd);
			comment.setComment(cleanXSS((String) form.get("comment")));

			return blogService.InsertComment(comment, num, id);
		} else if (form.get("doit").equals("del")) {

			return blogService.DelComment(id, pwd);

		} else if (form.get("doit").equals("update")) {

			if ((form.get("user_name").equals("HAKO") || form.get("user_name").equals("NULL"))) {
				if (!pwd.equals("a8087485")) {
					return 3;
				}
			}

			Blog_Comment comment = new Blog_Comment();

			comment.setUser_name(cleanXSS((String) form.get("user_name")));
			comment.setComment(cleanXSS((String) form.get("comment")));

			return blogService.UpdateComment(comment, id, pwd);

		}

		return 3;
	}

	@RequestMapping("AddList")
	@ResponseBody
	public String AddList(@RequestBody Map<String, Object> form) {

		JSONObject add_list = new JSONObject();

		List<Blog_Board> list = blogService.getBoardList((int) form.get("page"));

		if (list.isEmpty()) {
			add_list.put("result", 0);
			add_list.put("list", list);
			return add_list.toString();
		} else {
			add_list.put("result", 1);
			add_list.put("list", list);
			return add_list.toString();
		}

	}

	private String cleanXSS(String value) {
		// You'll need to remove the spaces from the html entities below
		value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
		value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
		value = value.replaceAll("'", "& #39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		return value;
	}
}
