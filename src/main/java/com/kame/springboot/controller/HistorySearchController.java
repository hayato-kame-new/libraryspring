package com.kame.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.service.HistoryService;

@Controller
public class HistorySearchController {
	
	@Autowired
	HistoryService historyService;
	
	@Autowired
	ViewBean viewBean;

	// 検索画面表示
	@RequestMapping( value = "/history_search",  method=RequestMethod.GET)
	public ModelAndView searchForm(
			ModelAndView mav
			) {
		mav.setViewName("history/search");  // templetesフォルダ以下の userフォルダのsearch.htmlファイル
		
		return mav;
	}

}
