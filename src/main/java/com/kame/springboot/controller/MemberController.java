package com.kame.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.Member;
import com.kame.springboot.service.MemberService;

@Controller
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	ViewBean viewBean;
	
	
	@RequestMapping(value = "/members", method=RequestMethod.GET)
	public String index(
			@PageableDefault(page = 0, size = 10, sort = { "id" }) Pageable pageable, // id でソートしてる 重要
			Model model
			) {
		
		// ページネーションと idで ソートしたコレクション を取得
		Page<Member> memberPage = memberService.getAllMembers(pageable);
		model.addAttribute("page", memberPage);
		model.addAttribute("members", memberPage.getContent());
		
		return "member/members";
	}

}
