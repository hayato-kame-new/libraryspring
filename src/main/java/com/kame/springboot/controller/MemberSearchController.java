package com.kame.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.form.MemberSearchForm;
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
			@ModelAttribute("memberSearchForm")MemberSearchForm memberSearchForm,
			ModelAndView mav
			) {
						
		mav.setViewName("member/search");  
		return mav;
	}
	
	// BindingResultは、@ModelAttributeのすぐ下につけないと エラーになる BindingError
	@RequestMapping(value = "/member_search", method=RequestMethod.POST)
	public ModelAndView search(
			@ModelAttribute("memberSearchForm")@Validated MemberSearchForm memberSearchForm,
			BindingResult result, // BindingResultは、@ModelAttributeのすぐ下につけないと エラーになる BindingError
			RedirectAttributes redirectAttributes,  // リダイレクトするのに必要
			ModelAndView mav
			
			) {
		String msg = "";
    	// まず、入力チェック バリデーションに引っ掛かったら
		if (result.hasErrors()) {
			// フォワード
        	mav.setViewName("member/search"); 
        	msg = "入力エラーが発生しました。";
        	mav.addObject("msg", msg);
			       
        	return mav;  //returnで メソッドの即終了この後ろは実行されない
		 }
		// 2つとも 入力しないまま 検索ボタンを押してきた時には 何もしないで リダイレクトして、また、検索フォームに戻るだけ 何か入力してくださいのメッセージをつける
		if (memberSearchForm.getId() == null &&  "".equals(memberSearchForm.getName())) {
			// idは　Integerにしてください 何も入力しないと null   nameフィールドはStringだから 何も入力しないと""空文字が入ってくる
			
			 // 何もしないでリダイレクトするだけ メッセージだけつける
			 String flashMsg = "検索フォームに何か入力してください";
			//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
				redirectAttributes.addFlashAttribute("flashMsg", flashMsg);

			 return new ModelAndView("redirect:/member_search_form");  // 未入力なので、何もしないで /member_search_form へリダイレクトするだけ リダイレクトは、リダイレクト先のリクエストハンドラを実行させます。
			 // returnで即終了 return の後ろの引数を呼び出し元へ返す この行以降は実行されない			 
		}
		// フォームに何か入ってたら、検索を実行する
		 // まず、会員のIDがフォームに入力があったら、（nullじゃなかったら)
		 // 会員のIDが 図書館システムに存在するのかを調べる必要がある
		if(memberSearchForm.getId() != null) {
			boolean exist = false;
			// 存在するのかどうか調べる
			exist = memberService.exist(memberSearchForm.getId());
			
			if(exist == false) {  // false 存在しないIDだった
				// このIDの会員は図書館システムには存在していないので 	   		 
	   		 msg = "このIDの会員は存在しません. ";
	   		 // フォワードをするが
 			// 何を入力して検索をかけたのか わかるように、フォームに以前入力したものを表示させるため
 			mav.addObject("memberSearchForm", memberSearchForm);  // フォームのオブジェクトとして送る th:object="${memberSearchForm}" として使うので
 			mav.addObject("msg", msg);
			// フォワード
			mav.setViewName("member/search");
			return mav;  // ここでreturnして即終了 returnの後ろの引数を呼び出し元へ返す この行より下は実行されない
			}
			
		}// 会員ID未入力か 入力してあってそのIDの会員が存在するかのどちらか
		//  会員のIDが 図書館システムに存在するので処理を進める  Integerにすること
			Integer id = memberSearchForm.getId();  // Integerだから 入力しないと nullになってる int にすると null の時に落ちるし、フォームに0が初期値として表示されてしまうので
		// id この図書館システムに存在している または フォームに何も入力してないnullも ありえる
			// 指定した条件で AND検索 idは完全一致検索 nameは LIKEで曖昧検索をする  引数は nullの可能性もあるので int じゃなくて Integer 
			// 戻り値 List<Object[]>になってます  Iterable にキャストもできる (List<Member>)にキャストもできる
			Iterable resultList = memberService.searchMemberAND(memberSearchForm.getId(), memberSearchForm.getName());
			 
			// 戻り値は List<Object[]> になってるけど Iterable型にもできる
    		int resultCount = ((List<Object[]>) resultList).size();
    		String searchResultMsg = "検索結果は " + resultCount + "件です";
    		 // 何を入力して検索をかけたのか わかるように、検索結果後も、フォームに以前入力したものを表示させるため
   		 	mav.addObject("memberSearchForm", memberSearchForm);  // フォームのオブジェクトとして送る th:object="${bookSearchForm}" として使う
   		 mav.addObject("searchResultMsg", searchResultMsg);
   		 	mav.addObject("resultList", resultList); 
   		 	// フォワード
   		 	mav.setViewName("member/search");
		
		return mav;
	}
}
