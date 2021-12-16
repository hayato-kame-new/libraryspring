package com.kame.springboot.controller;

import java.util.LinkedHashMap;
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

import com.kame.springboot.bean.Library;
import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.Book;
import com.kame.springboot.form.BookSearchForm;
import com.kame.springboot.service.BookService;
import com.kame.springboot.service.HistoryService;


@Controller
public class BookSearchController {
	
	@Autowired
	BookService bookService;
	
	@Autowired
	ViewBean viewBean;
	
	@Autowired
	HistoryService historyService;
	
	@Autowired
	Library library;  // Beanのクラスとして設定したクラスを組み込む
	
	
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
		 // 戻り値 List<Object[]>になってます  Iterable にキャストもできる (List<Book>)にキャストもできる
		 Iterable resultList = bookService.searchBookAND(bookSearchForm.getIsbn(), bookSearchForm.getGenre(), 
					 bookSearchForm.getTitle(),bookSearchForm.getAuthors(), bookSearchForm.getPublisher());
		
		 // 何を入力して検索をかけたのか わかるように、検索結果後も、フォームに以前入力したものを表示させるため
		 mav.addObject("bookSearchForm", bookSearchForm);  // フォームのオブジェクトとして送る th:object="${bookSearchForm}" として使う
		 
		 // 結果のリストを送り表示させる このリストに書架状態までくっつけて リストを作り直しして送ります
		//  mav.addObject("resultList",resultList); 
		 		
		// History実体は 貸し出しを完了する時点で historiesテーブルに保存する 全てhistoriesテーブルに保存していく
		// bookid で絞って検索をすると
		// historiesテーブルから select * from histories where bookid = ?  order by id desk limit 1; で探すと　最後の貸し出し履歴が取れる
		// そのHistoryデータの returnDate が nullだったら、貸し出し中なので、それで状態がわかる nullじゃなかったら、書架状態は 書架にある
		// さらに、書架状態まで調べて送ります?
		 //Mapに変換するをnewして確保しておく
		 Map<Book, String> statusMap = new LinkedHashMap<Book, String>();  // LinkedHashMapは、格納した順番を記憶する
		
		
		for( Book book : (List<Book>)resultList) {
			int bookId = book.getId();  // Bookインスタンスの主キーidが Historyインスタンスの bookidとリレーションがあります Historyインスタンスの bookidは Bookの idを参照してます
			// このリストには 要素は1つ もしくは 要素はない 
			// historiesテーブルから 書籍のIDで絞り込んで そして主キーでソートをして limit 1　で 最新の貸し出し履歴を取得してる
			List<Object[]> LastHistoryDatalist = historyService.getLastHistoryData(bookId);
			
			// これで最後の貸し出し履歴のHistoryのデータ！履歴がまだない時は []
			String status = library.getStatusStr(LastHistoryDatalist);
			
//			int id = 0;
//			Date lendDate = null;
//			 Date returnDate = null;
//			 // int bookId = 0;
//			 int memberId = 0;	
//			 
//			for(Object[] obj : LastHistoryDatalist) {
//				System.out.println(obj[0]);
//				System.out.println(obj[1]);
//				System.out.println(obj[2]);
//				System.out.println(obj[3]);  // イラン
//				System.out.println(obj[4]);
//				id =  Integer.parseInt(String.valueOf(obj[0])); 
//				lendDate = (Date) obj[1];
//				returnDate = (Date) obj[2];
//				memberId = Integer.parseInt(String.valueOf(obj[4])); 
//			}
//			// HistoryDatalistに要素がない サイズが 0なら、貸し出し履歴はないので まだ一度も貸出されていませんので
//			// 貸出可能 書架の状態です
//			if(LastHistoryDatalist.size() == 0) { // 該当の本は貸し出しの履歴は今まで一度も無い
//				status = "書架";
//			} else if(LastHistoryDatalist.size() > 0){ // 該当の本は貸し出しの最新の履歴１件はあります
//				// 最新の履歴の状態は 返却済みなのかどうか、 returnDate;  // 返却した日が nullなのかどうか
//				if(returnDate == null) {
//					// 貸し出し中です
//					status = "貸し出し中";
//				} else {
//					status = "書架";
//				}				
//			}
			// Mapに詰める
			statusMap.put(book, status);
			
		}
		// Mapをビューへ送る
		mav.addObject("statusMap",statusMap); 
		
		// セレクトボックス表示用のgenreMap
		mav.addObject("genreMap", genreMap);
		int count = ((List<Book>) resultList).size();
		String searchResultMsg = "検索結果" + count + "件です";
		mav.addObject("searchResultMsg", searchResultMsg);
	
		mav.setViewName("book/search");
		 return mav;
	}
	
	

}
