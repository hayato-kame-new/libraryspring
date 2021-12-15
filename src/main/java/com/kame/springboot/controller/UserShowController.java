package com.kame.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.userDetails.UserDetailsImpl;
import com.kame.springboot.userDetailsService.UserDetailsServiceImpl;

@Controller
public class UserShowController {
	
	// サービスクラスをフィールドとして宣言して @Autowierd で取り込む
	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;
	
	
//	@GetMapping
//    public String index () {
//		
//		// SecurityContextHolder からユーザー情報を取得する 
//		//  SecurityContextHolderからAuthenticationオブジェクトを取得 
//		SecurityContext context = SecurityContextHolder.getContext();
//        Authentication authentication = context.getAuthentication();
//        
//     // Authenticationオブジェクトからユーザー情報を取得
//        System.out.println(authentication.getName());  // ユーザー名を表示
//        System.out.println(authentication.getAuthorities());  // 権限情報を表示
//        System.out.println(authentication.toString());
//        
//      
//     // Authenticationオブジェクトからユーザー情報を取得
//        UserDetails principal = (UserDetails) authentication.getPrincipal();
//        System.out.println(principal.getUsername());  // ユーザー名を表示
//        System.out.println(principal.getPassword());  // パスワードを表示
//        System.out.println(principal.getAuthorities());  // 権限情報を表示
//        System.out.println(principal.toString());
              
//        return "index";
//    }

	
	@RequestMapping( value = "/user_show", method=RequestMethod.GET)
	public ModelAndView userShow(
			// org.springframework.security.core.annotation.AuthenticationPrincipal
			@AuthenticationPrincipal UserDetailsImpl userDetails,  // ＠AuthenticationPrincipal からユーザー情報を取得する
			@RequestParam("username")String username,  // 認証ユーザの名前 
			ModelAndView mav) {
		// ＠AuthenticationPrincipal からユーザー情報を取得する
		// ユーザー情報は、アノテーション ＠AuthenticationPrincipal を使用すると、より簡単に取得することができます
		// @AuthenticationPrincipal を書いてるので 下の一文は要らない
		// UserDetails userDetails = userDetailsServiceImple.loadUserByUsername(username);
		
		System.out.println(userDetails.getUsername());  // ユーザー名を表示
		System.out.println(userDetails.getPassword());  // パスワードを表示
		System.out.println(userDetails.getAuthorities().toString());  // 権限情報を表示
		
		System.out.println(userDetails.getId());  // IDを表示
		// Integer gender = userDetails.getGender();
		// userDetails.genderStr();
		//  メソッドなどを使う時には、userDetails.genderStr()  などと使います UserDetailsImpl.genderStr() では無い
		System.out.println(userDetails.genderStr());  // genderStr()インスタンスメソッドを呼び出して 性別を文字列で 表示
		System.out.println(userDetails.getAge().toString());  // 年齢を表示 参照型なら toString()を使える 基本型(プリミティブ型)には使えない
		System.out.println(userDetails.getTel());  // 電話番号を表示
		System.out.println(userDetails.getEmail());  // メールアドレスを表示
		
		// 認証ユーザの名前 はユニークなのusersテーブルから ユーザ名で検索できます 
		// 追加した geder age email tel フィールドの値の情報はusersテーブルから取得する
		// idもusersテーブルから取得する
		
		// userDetailsをビューに送ればいい
		mav.addObject("userDetails", userDetails);
		mav.setViewName("user/show");
		return mav;
	}
		
	
}
