package com.kame.springboot.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
    
//    @ModelAttribute("historySearchForm")@Validated HistorySearchForm historySearchForm,
//	BindingResult result,  // バリデーションに必要@ModelAttributeのすぐ下につけること
    // POSTでは、historiesテーブルからの検索結果と、状態をMapにしてからビューへ渡す
    @RequestMapping( value = "/history_search", method=RequestMethod.POST)
    public ModelAndView historySearch(
    		@ModelAttribute("historySearchForm")@Validated HistorySearchForm historySearchForm,
    		BindingResult result,  // バリデーションに必要@ModelAttributeのすぐ下につけること
    		
    		RedirectAttributes redirectAttributes,  // リダイレクトするのに必要
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
    		
    		// 取得した本のID と　会員IDが、図書館システムに登録のあるIDなのかをまず調べること
    		
    		
    		
    		
    		Integer bookId = historySearchForm.getBookId();
    		// フォームに何も入力してない時には nullになってるので Integer型にする  int にすると null の時に落ちる
    		Integer memberId = historySearchForm.getMemberId();
    		// セレクトボックスを何も選択しないで送信したら バリデーションエラーに引っかかるので 選択されてきてる
    		// countDisplayの中身によって、SQL文が変わる select  全て   limit 10   limit 20   limit 30 
    		
    		Integer count = null;  // "全て" だったら nullにしておく
    		
    		if( !historySearchForm.getCountDisplay().equals("全て")) { // nullにはなってないから historySearchForm.getCountDisplay() != null はいらない
    			// "10"  "20"  "30" だったら 文字列から数値に変換する
    			count = Integer.parseInt(historySearchForm.getCountDisplay());
    		} 
    		// 指定した条件で AND検索 完全一致検索 引数は nullの可能性もあるので int じゃなくて Integer  戻り値 List<Object[]>になってます  Iterable にキャストもできる (List<Book>)にキャストもできる
    		// countは、"全て" を選択してたら  null になってる
    		
    		
    		Iterable resultList = historyService.searchAND(historySearchForm.getBookId(), historySearchForm.getMemberId(), count);
    		
    		
    		// 戻り値は List<Object[]> になってるけど Iterable型にもできる
    		int resultCount = ((List<Object[]>) resultList).size();
    		String searchResultMsg = "検索結果は " + resultCount + "件です";
    		 // 何を入力して検索をかけたのか わかるように、検索結果後も、フォームに以前入力したものを表示させるため
   		 	mav.addObject("historySearchForm", historySearchForm);  // フォームのオブジェクトとして送る th:object="${bookSearchForm}" として使う
   		 mav.addObject("searchResultMsg", searchResultMsg);
   		 	mav.addObject("resultList", resultList); 
   		
   		 	mav.setViewName("history/search");
    	}
    	
    	return mav;
    }
    
    
	/**
	 * 社員検索
	 * @param departmentId
	 * @param employeeId
	 * @param word
	 * @return List
	 */
//	@SuppressWarnings("unchecked")
//	public List<Employee> find(String departmentId, String employeeId, String word) {
//		// 注意 引数のdepartmentId は 空文字とnullの可能ある   employeeId と word は ""空文字の可能性ある
//
//		String sql = "select * from employee";
//		String where = ""; // where句
//		int depIdIndex = 0; // プレースホルダーの位置を指定する 0だと、プレースホルダーは使用しないことになる
//		int empIdIndex = 0;
//		int wordIndex = 0;
//
//		if (departmentId == null) {
//			departmentId = "";
//		}
//		if (departmentId.equals("")) {
//			// 未指定の時 何もしない depIdIndex 0 のまま変更無し
//		} else {
//			where = " where departmentid = ?"; // 代入する 注意カラム名を全て小文字にすること departmentid また、前後半角空白入れてつなぐので注意
//			depIdIndex = 1; // 変更あり
//		}
//
//		if (employeeId.equals("")) {
//			// 未指定の時 何もしない 
//		} else {
//			if (where.equals("")) { 
//				where = " where employeeid = ?"; // 代入する カラム名を全て小文字 employeeid
//				empIdIndex = 1;
//			} else {
//				where += " and employeeid = ?"; // where句はすでにあるので 二項演算子の加算代入演算子を使って連結 												
//				empIdIndex = depIdIndex + 1;
//			}
//		}
//
//		if (word.equals("")) {
//			// 未指定の時何もしない
//		} else {
//			if (where.equals("")) { 
//				where = " where name like ?"; // 代入  
//				 wordIndex = 1;
//			} else if (where.equals(" where departmentid = ?")) {
//				where += " and name like ?"; // 二項演算子の加算代入演算子を使って連結 
//				 wordIndex = depIdIndex + 1;
//			} else if (where.equals(" where employeeid = ?")) {
//				where += " and name like ?"; // 二項演算子の加算代入演算子を使って連結 
//				 wordIndex = empIdIndex + 1;
//			} else if (where.equals(" where departmentid = ? and employeeid = ?")) {
//				where += " and name like ?"; // 二項演算子の加算代入演算子を使って連結 
//				 wordIndex = depIdIndex + empIdIndex + 1;
//			}
//		}
//
//		Query query = entityManager.createNativeQuery(sql + where);
//		if (depIdIndex > 0) {
//			query.setParameter(depIdIndex, departmentId);
//		}
//		if (empIdIndex > 0) {
//			query.setParameter(empIdIndex, employeeId);
//		}
//		if (wordIndex > 0) {
//			query.setParameter(wordIndex, "%" + word + "%");
//		}
//		return query.getResultList(); // 結果リスト 型のないリストを返す 
//	}


}
