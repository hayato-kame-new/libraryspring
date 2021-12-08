package com.kame.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.Member;
import com.kame.springboot.service.MemberService;

@Controller
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	ViewBean viewBean;
	
	/**
	 * 会員一覧表示する
	 * ページネーションと idでソートして表示する sort = { "id" } が必要
	 * @param pageable
	 * @param model
	 * @return
	 */
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
	
	
	/**
	 * 会員 新規登録画面  編集画面   削除確認画面 を表示する
	 * 削除する際も、一度内容を確認してからなので 削除の確認画面を表示する
	 * aリンクの ?以降のクエリー文字列を取得する
	 * 新規の時    ?action=add  になる  idのクエリー文字列はない 
	 * 
	 * 編集の時     ?action=edit&id=2  などになる
	 * 削除の時     ?action=delete&id=2  などになる
	 * @param mav
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/member_form", method=RequestMethod.GET)
	public ModelAndView memberForm(
			@ModelAttribute("member") Member member,
			@RequestParam(name = "action")String action, // 必須パラメータ
			// aリンクの ？以降のクエリー文字列で、編集editの時に ?action=edit&id=2 などという形で送られてくる
			@RequestParam(name = "id", required = false)Integer id, // 任意パラメータ 編集の時だけ送られてきてるので
			ModelAndView mav			
			) {
		
		mav.setViewName("member/form");
		mav.addObject("action", action);  // フォームのビューへ送ること 必要
		
		switch(action) {
		case "add":
			// そのまま 
			break;
			
		case "edit":
			// aリンクの idが クエリー文字列で送られてきてるので 
			// 主キーのidから、Mmeberオブジェクトを取得して、
			// リクエストハンドラで定義した@ModelAttribute("member") Member member の フォームのオブジェクトに上書きをする
			// そして、それを mav.addObject("book", book);することが必要
			
//			member = memberService.findMemberDataById(id);
//			mav.addObject("member", member);  // 必要 フォームに初期値として、表示するために
			break;
			
		case "delete":
			// aリンクの idが クエリー文字列で送られてきてるので 
			// 主キーのidから、Bookオブジェクトを取得して、
			// リクエストハンドラで定義した@ModelAttribute("book") Book book の フォームのオブジェクトに上書きをする
			
//			member = memberService.findMemberDataById(id);
//			mav.addObject("member", member);  // 必要 フォームに初期値として、表示するために
			break;
		
		}
		
		return mav;
		
	}
		
}
