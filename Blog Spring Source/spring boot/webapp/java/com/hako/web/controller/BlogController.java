package com.hako.web.blog.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;
import com.hako.web.blog.entity.Blog_Comment;
import com.hako.web.blog.service.BlogService;

@Controller
@RequestMapping("/")
public class BlogController {

	Logger LOG = LoggerFactory.getLogger(BlogController.class);

	@Autowired
	private BlogService blogService;

	@RequestMapping("index")
	public String BlogIndex(Model model) {

		List<Blog_Board> list = blogService.getBoardList("Y");
		List<Blog_Board> commend = blogService.getCommendBoard();
		List<Blog_Category> category = blogService.getCategory();
		model.addAttribute("list", list);
		model.addAttribute("categorys", category);
		model.addAttribute("commend", commend);
		model.addAttribute("url", "index");

		return "Blog.index";
	}

	@RequestMapping(value = { "detail", "detail/{board_num}" })
	public String BlogDetail(HttpServletRequest req, HttpServletResponse resp, Model model,
			@PathVariable(required = false, value = "board_num") String num,
			@RequestHeader(value = "User-Agent", required = false) String userAgent) {

		if (num == null) {
			req.setAttribute("msg", "존재하지 않는 게시물 입니다.");
			req.setAttribute("url", "/index");
			return "alert";
		} else {
			int _num = Integer.parseInt(num);
			Blog_Board board = blogService.getBoard(_num);
			model.addAttribute("board", board);

			if (board == null || board.getHidden().equals("Y")) {
				req.setAttribute("msg", "존재하지 않는 게시물 입니다.");
				req.setAttribute("url", "/index");
				return "alert";
			} 
			List<Blog_Comment> comment = blogService.getCommentList(_num);

			List<Blog_Comment> recomment = blogService.getCommentList(_num);
			Collections.reverse(recomment);

			// 다음 글 두개 가져오기
			List<Blog_Board> nextBoard = blogService.getCommendNextBoard(board.getCategory(), _num, 2);
			List<Blog_Board> prevBoard = blogService.getCommendPrevBoard(board.getCategory(), _num, 2);
			if (nextBoard.size() < 2 && prevBoard.size() == 2) {
				prevBoard = blogService.getCommendPrevBoard(board.getCategory(), _num, (4 - nextBoard.size()));
			} else if (nextBoard.size() == 2 && prevBoard.size() < 2) {
				nextBoard = blogService.getCommendNextBoard(board.getCategory(), _num, (4 - prevBoard.size()));
			}

			boolean isCrawling = shouldCrawl(userAgent);

			if (!isCrawling) {

				Cookie[] cookies = req.getCookies();
				if (cookies != null) {
					for (int i = 0; i < cookies.length; i++) {
						if (cookies[i].getName().equals("visited" + num)) {
							break;
						}
						if ((i == cookies.length - 1) && !cookies[i].getName().equals("visited" + num)) {
							blogService.ADDCount(_num);
							Cookie cookie = new Cookie("visited" + num, "1");
							cookie.setMaxAge(60 * 10);
							cookie.setPath("/");
							resp.addCookie(cookie);

						}
					}
				}
			}

			List<Blog_Category> category = blogService.getCategory();
			model.addAttribute("comment", comment);
			model.addAttribute("recomment", recomment);
			model.addAttribute("nextBoard", nextBoard);
			model.addAttribute("prevBoard", prevBoard);
			model.addAttribute("url", "index");
			model.addAttribute("categorys", category);
			model.addAttribute("url", "detail/" + num);

			return "Blog.nonaside.detail";
		}

	}

	@RequestMapping(value = { "list", "list/{category}" })
	public String BlogList(HttpServletRequest req, Model model,
			@PathVariable(required = false, value = "category") String category,
			@RequestParam(defaultValue = "1", name = "page") int page,
			@RequestParam(defaultValue = "", name = "title") String title) {

		category = (category == null) ? "" : category;

		List<Blog_Board> list = blogService.getBoardList(category, title, page, "Y");
		List<Blog_Board> commend = blogService.getCommendBoard();
		int count = blogService.getBoardCount(category, title, "Y");

		if (list.isEmpty()) {
			req.setAttribute("msg", "게시글이 없습니다..");
			req.setAttribute("url", "/index");
			return "alert";
		}

		List<Blog_Category> _category = blogService.getCategory();
		model.addAttribute("categorys", _category);
		model.addAttribute("category", category);
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("commend", commend);
		model.addAttribute("url", "list/" + category);

		return "Blog.list";
	}

	@PostMapping("Comment")
	@ResponseBody
	public int CommentHandler(@RequestBody Map<String, Object> form) {

	
		String pwd = ((String) form.get("pwd") == null) ? "" : (String) form.get("pwd");
		// 댓글작성
		if (form.get("doit").equals("insert")) {
			int ref = (int) form.get("ref");
			int ref_comment = (int) form.get("ref_comment");
			if ((form.get("user_name").equals("HAKO") || form.get("user_name").equals("NULL"))) {
				if (!pwd.equals("a8087485")) {
					return 2;
				}
			}

			pwd = BCrypt.hashpw(pwd, BCrypt.gensalt());

			Blog_Comment comment = new Blog_Comment();
			comment.setRef(ref);
			comment.setRef_comment(ref_comment);
			comment.setUser_name(cleanXSS((String) form.get("user_name")));
			comment.setPwd(pwd);
			comment.setComment(cleanXSS((String) form.get("comment")).replace("\n", "<br/>"));

			if (blogService.InsertComment(comment) == 1) {
				blogService.ADDComment(ref);
				LOG.info("insert comment WHERE board : " + ref);
				return 1;
			} else {
				return 0;
			}
			// 댓글 삭제
		} else if (form.get("doit").equals("del")) {
			
			
			int num = (int) form.get("num");
			Blog_Comment comment = new Blog_Comment();
			comment.setNum(num);
			comment.setPwd(pwd);
			
		
			int result = blogService.DelComment(comment);
			
			if (result == 1 || result == 3) {
				blogService.SUBComment(num);
				LOG.info("del comment : " + num);
			}

			return result;
			// 댓글 수정
		} else if (form.get("doit").equals("update")) {

			if ((form.get("user_name").equals("HAKO") || form.get("user_name").equals("NULL"))) {
				if (!pwd.equals("a8087485")) {
					System.out.print(form.get("user_name"));
					return 3;
				}
			}

			Blog_Comment comment = new Blog_Comment();
			int num = (int) form.get("num");
			comment.setUser_name(cleanXSS((String) form.get("user_name")));
			comment.setComment(cleanXSS((String) form.get("comment")).replace("\n", "<br/>"));
			comment.setNum(num);
			comment.setPwd(pwd);
			if (blogService.UpdateComment(comment) == 1) {
				LOG.info("update comment : " + num);
				return 1;
			} else {
				return 0;
			}
		}

		return 0;
	}

	@PostMapping("AddList")
	@ResponseBody
	public String AddList(@RequestBody Map<String, Object> form) {

		JSONObject add_list = new JSONObject();

		List<Blog_Board> list = blogService.getBoardList((int) form.get("page"), "Y");

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

	private boolean shouldCrawl(String userAgent) {
		// User-Agent를 분석하여 크롤링 여부를 결정하는 로직 작성
		// 예를 들어, 특정 User-Agent 패턴을 가진 요청만 크롤링으로 허용할 수 있습니다.
		// 필요에 따라 쿠키 정보 등 다른 요소도 고려할 수 있습니다.
		// 크롤링을 허용하는 경우 true, 제한하는 경우 false를 반환합니다.
		// 이 예시에서는 User-Agent가 "CrawlerBot"으로 시작하는 경우에만 크롤링으로 허용하도록 설정하였습니다.
		return userAgent != null && userAgent.startsWith("CrawlerBot");
	}

}