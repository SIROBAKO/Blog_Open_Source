package com.hako.web.blog.board.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hako.web.blog.board.entity.BlogBoard;
import com.hako.web.blog.board.service.BoardService;

@Controller
@RequestMapping("/")
public class BoardRestController {

	@Autowired
	private BoardService boardService;

	private Logger LOG = LogManager.getLogger(BoardRestController.class);

	private static final String FAIL_GET_BOARD_LIST = "게시글 추가 반환 오류";

	@PostMapping("AddList")
	@ResponseBody
	public ResponseEntity<?> addBoardList(@RequestBody Map<String, Object> requestData) {
		try {
			int pageNum = (int) requestData.get("page");

			List<BlogBoard> boardList = boardService.getBoardList(pageNum);

			if (boardList.isEmpty()) {
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.ok(boardList);
			}
		} catch (NullPointerException e) {
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			LOG.error(FAIL_GET_BOARD_LIST, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
