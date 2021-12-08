package com.kame.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.service.MemberService;

@Controller
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	ViewBean viewBean;

}
