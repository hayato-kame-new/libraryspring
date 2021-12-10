package com.kame.springboot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.Book;
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
			
			Model model,  // Flash Scopeから 取り出しするのに必要
			ModelAndView mav) {
		
		String flashMsg = null;
		// flashMsg を Flash Scopeから取り出す
		if(model.getAttribute("flashMsg") != null) {
			flashMsg = (String) model.getAttribute("flashMsg");
		}
		mav.addObject("flashMsg", flashMsg);
		
		// その書籍が現在どういう状態かまで、わかるようにする つまり、貸出中なのかどうか
		List<Book> books = bookService.booksList();
		mav.addObject("books", books);		
		mav.setViewName("book/search");  // templetesフォルダ以下の bookフォルダのsearch.htmlファイル
		// ジャンルのセレクトボックスを表示するために
		Map<Integer, String> genreMap = viewBean.getGenreMap();
		mav.addObject("genreMap", genreMap);
		
		return mav;
	}
	
	@RequestMapping(value = "/book_search", method=RequestMethod.POST)
	public ModelAndView search(
			@RequestParam(name = "genre", required = false)String genre,  // 任意パラメータ required = false が必要です
			@RequestParam(name = "title_included", required = false)String title_included, // 任意パラメータ required = false が必要です
			@RequestParam(name = "authors_included", required = false)String authors_included, // 任意パラメータ required = false が必要です
			RedirectAttributes redirectAttributes,  // リダイレクトするのに必要
			ModelAndView mav
			) {
		
		// もし、3つとも全部検索条件入れてない時は、何もしないで リダイレクトして、また、検索フォームに戻るだけ 何か入力してくださいのメッセージをつける
		 if (( genre == null || "選択しない".equals(genre)  ) && "".equals(title_included) && "".equals(authors_included) ) {
			 // 何もしないでリダイレクトするだけ メッセージだけつける
			 String flashMsg = "検索フォームに何か入力してください";
			//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
				redirectAttributes.addFlashAttribute("flashMsg", flashMsg);

			 return new ModelAndView("redirect:/book_search_form");  // 未入力なので、何もしないで /book_search_form へリダイレクトするだけ リダイレクトは、リダイレクト先のリクエストハンドラを実行させます。
		 } else {  // フォームに何か入ってたら、検索を実行する
			 
		
		 }
		return mav;
	}

}
