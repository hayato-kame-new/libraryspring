package com.kame.springboot.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.Book;
import com.kame.springboot.form.BookSearchForm;
import com.kame.springboot.service.BookService;


@Controller
public class BookSearchController {
	
	@Autowired
	BookService bookService;
	
	@Autowired
	ViewBean viewBean;
	
	/**
	 * 書籍 検索画面を表示する
	 * @param mav
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/book_search_form", method=RequestMethod.GET)
	public ModelAndView searchForm(
			@ModelAttribute("bookSearchForm") BookSearchForm bookSearchForm,
			Model model,  // Flash Scopeから 取り出しするのに必要  5つとも全部検索条件入れてない時は、何もしないで リダイレクトしてくるから
			ModelAndView mav) {
		
		String flashMsg = null;
		// flashMsg を Flash Scopeから取り出す
		if(model.getAttribute("flashMsg") != null) {
			flashMsg = (String) model.getAttribute("flashMsg");
		}
		mav.addObject("flashMsg", flashMsg);
		
		mav.setViewName("book/search");  // templetesフォルダ以下の bookフォルダのsearch.htmlファイル
		// ジャンルのセレクトボックスを表示するために
		Map<Integer, String> genreMap = viewBean.getGenreMap();
		mav.addObject("genreMap", genreMap);
		
		return mav;
	}
	
	
	/**
	 * 指定した条件で AND検索 曖昧検索 をする
	 * @param bookSearchForm
	 * @param redirectAttributes
	 * @param result
	 * @param request
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "/book_search", method=RequestMethod.POST)
	public ModelAndView search(
			@ModelAttribute("bookSearchForm")@Validated BookSearchForm bookSearchForm,
			RedirectAttributes redirectAttributes,  // リダイレクトするのに必要
			BindingResult result,  // バリデーションに必要
			HttpServletRequest request, // requestオブジェクトから取得したい時に
			ModelAndView mav
			) {
		
		// セレクトボックス表示用のgenreMap
		 Map<Integer, String> genreMap = viewBean.getGenreMap();
		
		// まず、入力チェック バリデーションに引っ掛かったら
		if (result.hasErrors()) {
			// フォワード
        	mav.setViewName("book/search");       	
        	mav.addObject("msg", "入力エラーが発生しました。");
			       
    		// セレクトボックス表示用のgenreMap
    		mav.addObject("genreMap", genreMap);
    		
        	return mav;  //returnで メソッドの即終了この後ろは実行されない
		 }
		
		// もし、5つとも全部検索条件入れてない時は、何もしないで リダイレクトして、また、検索フォームに戻るだけ 何か入力してくださいのメッセージをつける
		 if ("".equals(bookSearchForm.getIsbn()) && ( bookSearchForm.getGenre() == null || "選択しない".equals(bookSearchForm.getGenre())  ) && "".equals(bookSearchForm.getAuthors()) && "".equals(bookSearchForm.getTitle()) &&  "".equals(bookSearchForm.getPublisher())  ) {
		 // if ("".equals(bookDataSearchForm.getIsbn()) &&( genre == null || "選択しない".equals(genre)  ) && "".equals(title_included) && "".equals(authors_included) ) {
			 // 何もしないでリダイレクトするだけ メッセージだけつける
			 String flashMsg = "検索フォームに何か入力してください";
			//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
				redirectAttributes.addFlashAttribute("flashMsg", flashMsg);

			 return new ModelAndView("redirect:/book_search_form");  // 未入力なので、何もしないで /book_search_form へリダイレクトするだけ リダイレクトは、リダイレクト先のリクエストハンドラを実行させます。
		 }   // フォームに何か入ってたら、検索を実行する
			 
		 Iterable resultList = bookService.searchBookAnd(bookSearchForm.getIsbn(), bookSearchForm.getGenre(), 
					 bookSearchForm.getTitle(),bookSearchForm.getAuthors(), bookSearchForm.getPublisher());
		
		 // 何を入力して検索をかけたのか わかるように、検索結果後も、フォームに以前入力したものを表示させるため
		 mav.addObject("bookSearchForm", bookSearchForm);  // フォームのオブジェクトとして送る th:object="${bookSearchForm}" として使う
		 
		 // 結果のリストを送り表示させる
		 mav.addObject("resultList",resultList); 
		// セレクトボックス表示用のgenreMap
		mav.addObject("genreMap", genreMap);
		int count = ((List<Book>) resultList).size();
		String resultMsg = "検索結果" + count + "件です";
		mav.addObject("resultMsg", resultMsg);
		mav.setViewName("book/search");
		
		// さらに、書架状態まで調べて送ります
		
		 return mav;
	}

}
