package com.kame.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.service.MemberService;

@Controller
public class MemberSearchController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	ViewBean viewBean;
	
	/**
	 * 会員情報 検索画面を表示する
	 * 
	 * @param mav
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/member_search_form", method=RequestMethod.GET)
	public ModelAndView searchForm(
			
			ModelAndView mav
			) {
		
		
		
		mav.setViewName("member/search");  // templetesフォルダ以下の userフォルダのsearch.htmlファイル
		
		return mav;
	}
	
	// BindingResultは、@ModelAttributeのすぐ下につけないと エラーになる BindingError

}
