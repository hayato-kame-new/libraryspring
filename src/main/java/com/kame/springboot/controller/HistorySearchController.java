package com.kame.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.form.HistorySearchForm;
import com.kame.springboot.service.HistoryService;

@Controller
public class HistorySearchController {
	
	@Autowired
	HistoryService historyService;
	
	
	// 図書館の所蔵の書籍全てから 貸出記録を条件を指定して検索する画面の表示
    @RequestMapping( value = "/history_search_form", method=RequestMethod.GET)
    public ModelAndView historySearchForm(
    		@ModelAttribute("historySearchForm")HistorySearchForm historySearchForm,
    		ModelAndView mav
    		) {
    	
    	mav.setViewName("history/search");
    	
    	return mav;
    }
    
    
    // POSTでは、historiesテーブルからの検索結果と、状態をMapにしてからビューへ渡す
    @RequestMapping( value = "/history_search", method=RequestMethod.POST)
    public ModelAndView historySearch(
    		@ModelAttribute("historySearchForm")HistorySearchForm historySearchForm,
    		RedirectAttributes redirectAttributes,  // リダイレクトするのに必要
			BindingResult result,  // バリデーションに必要
			HttpServletRequest request, // requestオブジェクトから取得したい時に
    		ModelAndView mav
    		) {
    	
    	// まず、入力チェック バリデーションに引っ掛かったら
		if (result.hasErrors()) {
			// フォワード
        	mav.setViewName("history/search");       	
        	mav.addObject("msg", "入力エラーが発生しました。");
			       
        	return mav;  //returnで メソッドの即終了この後ろは実行されない
		 }
    	
    	// もし、bookId memberId countDisplay  3つとも全部 検索条件入れてない時
    	// または bookId memberId  2つとも何も入力しないで countDisplayだけ選択をした時 は、
    	// 何もしないで リダイレクトして、また、検索フォームに戻るだけ 何か入力してくださいのメッセージをつける
    	if ((historySearchForm.getBookId() == null  &&  historySearchForm.getMemberId() == null && historySearchForm.getCountDisplay() == null ) || (historySearchForm.getBookId() == null  &&  historySearchForm.getMemberId() == null )){
   		 // メッセージを
    		String flashMsg = "検索フォームに何か入力してください";
    	//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
			redirectAttributes.addFlashAttribute("flashMsg", flashMsg);
			// リダイレクトする
			return new ModelAndView("redirect:/history_search_form");  // 未入力なので、何もしないで /history_search_form へリダイレクトするだけ リダイレクトは、リダイレクト先のリクエストハンドラを実行させます。
    	} else {
    		// bookId memberId  2つのうち 1つ以上に何か入力していたら 検索を実行する
    		// フォームに何も入力してない時には nullになってるので Integer型にする int にすると null の時に落ちる    		
    		Integer bookId = historySearchForm.getBookId();
    		// フォームに何も入力してない時には nullになってるので Integer型にする  int にすると null の時に落ちる
    		Integer memberId = historySearchForm.getMemberId();
    		// セレクトボックスを何も選択しないで送信したら nullになってる 
    		// countDisplayの中身によって、SQL文が変わる select  全て   limit 10   limit 20   limit 30 
    		Integer count = 0;
    		if(historySearchForm.getCountDisplay().equals("全て")) {
    			count = null;
    		} else {
    			count = Integer.parseInt(historySearchForm.getCountDisplay());
    		}
    		// 指定した条件で AND検索 完全一致検索 引数は nullの可能性もあるので int じゃなくて Integer  戻り値 List<Object[]>になってます  Iterable にキャストもできる (List<Book>)にキャストもできる
    		// countは、全てを選択したら null になってる
    		Iterable resultList = historyService.searchHistoryAND(historySearchForm.getBookId(), historySearchForm.getMemberId(), count);
    		// 戻り値は List<Object[]> になってるけど Iterable型にもできる
    		
    		
    	}
    	
    	return mav;
    }

}
