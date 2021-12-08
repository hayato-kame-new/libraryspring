package com.kame.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BookSearchController {
	
	
	/**
	 * 書籍 検索画面を表示する
	 * その書籍が現在どういう状態かまでわかる
	 * @param mav
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/book_search", method=RequestMethod.GET)
	public ModelAndView searchForm(ModelAndView mav) {
		
		
		// その書籍が現在どういう状態かまで、わかるようにする つまり、貸出中なのかどうか
		mav.setViewName("book/search");  // templetesフォルダ以下の bookフォルダのsearch.htmlファイル
		
		return mav;
	}

}
