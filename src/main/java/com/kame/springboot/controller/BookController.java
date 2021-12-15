package com.kame.springboot.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.bean.Library;
import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.Book;
import com.kame.springboot.service.BookService;
import com.kame.springboot.service.HistoryService;


@Controller
public class BookController {
	
	
	@Autowired
	BookService bookService;
	
	@Autowired
	ViewBean viewBean;
	
	@Autowired
	HistoryService historyService;
	
	@Autowired
	Library library;
	
	/**
	 * 書籍一覧を表示する
	 * ページネーションと idでソートをして書籍一覧を表示する
	 * JPQLを使うやり方です 表示は books.html
	 * sort = { "id" } で id順に表示でき ページングできます @PageableDefault(page = 0, size = 10, sort = { "id" }) Pageable pageable 
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/books", method=RequestMethod.GET)
	public String index(
			// @PageableDefault(page = 0, size = 10) Pageable pageable,
			@PageableDefault(page = 0, size = 10, sort = { "id" }) Pageable pageable, // id でソートしてる 重要
			Model model) {
		
		// 書籍を 新規登録 編集 が成功したら リダイレクトしてくるので このリクエストハンドラを実行する
		// Flash Scope から 取り出すには Modelインスタンスの getAttributeメソッドを使用する
		String flashMsg = null;
		if(model.getAttribute("flashMsg") != null) {
			flashMsg = (String) model.getAttribute("flashMsg");  // Flash Scopeに保存してあるのは Object型のインスタンスなので キャストする
		}
		// ページネーションと idで ソートしたコレクション を取得
		 Page<Book> bookPage = bookService.getPageableBooks(pageable);
		 
		 model.addAttribute("page", bookPage);  
	     model.addAttribute("books", bookPage.getContent());
	     
	     model.addAttribute("flashMsg", flashMsg);
	        
        return "book/books";
    }
	
	/**
	 * 書籍 詳細画面を表示
	 * ISBNをURLのクエリーパラメータで送ってくる
	 * ISBNから指定したレコードを取得して表示をする。
	 * @param id
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "/book_show/{isbn}", method=RequestMethod.GET)
	public ModelAndView show(
			@PathVariable String isbn,  // パス変数 /book_show/{isbn} の {isbn} と同じ変数名にする
			ModelAndView mav
			) {
		// ISBNから指定したレコードを取得する。 bookのデータを取得してる ISBN はユニークなので１つのデータが取得できる
		// リポジトリの辞書機能によって メソッド自動生成機能を使用してる 戻り値は  List<Book> だが、中身は１つ もしくは []空
		List<Book> bookDataList = bookService.findBookDataByIsbn(isbn);
		
		 //Mapに変換するをnewして確保しておく
		 Map<Book, String> statusMap = new LinkedHashMap<Book, String>();  // LinkedHashMapは、格納した順番を記憶する
				
		mav.setViewName("book/show");
		// mav.addObject("bookDataList", bookDataList);  // 書籍の情報を送る
		Book book = null;
		int id = 0;
		if(bookDataList.size() > 0) {
			book = bookDataList.get(0);
			id = book.getId();
		}
				
		// 書籍の状態(貸し出し中なのか 書架状態なのか)を表示するために
		List<Object[]> LastHistoryDatalist = historyService.getLastHistoryData(id);
//		
//		// これで最後の貸し出し履歴のHistoryのデータ！履歴がまだない時は []
	String status = library.getStatusStr(LastHistoryDatalist);
		
	// Mapに詰める
				statusMap.put(book, status);
				mav.addObject("statusMap",statusMap); 
			
		return mav;
	}
	
	/**
	 * 書籍 新規登録画面  編集画面 表示
	 * aリンクの ?以降のクエリー文字列を取得する
	 * 新規の時    ?action=add  になる  idのクエリー文字列はない 
	 * 編集の時     ?action=edit&id=2  などになる
	 * @param mav
	 * @return ModelAndView
	 */
	@RequestMapping(value="/book_form", method=RequestMethod.GET)
	public ModelAndView bookForm(
			@ModelAttribute("book") Book book,
			@RequestParam(name = "action")String action, // 必須パラメータ
			// aリンクの ？以降のクエリー文字列で、編集editの時に ?action=edit&id=2 などという形で送られてくる
			@RequestParam(name = "id", required = false)Integer id, // 任意パラメータ 編集の時だけ送られてきてるので
			ModelAndView mav
			) {
		Map<Integer, String> genreMap = viewBean.getGenreMap();
		mav.addObject("genreMap", genreMap);
		mav.setViewName("book/form");
		mav.addObject("action", action);  // フォームのビューへ送ること 必要
		
		switch(action) {
		case "add":
			// そのまま 
			break;			
		case "edit":
			// aリンクの idが クエリー文字列で送られてきてるので 
			// 主キーのidから、Bookオブジェクトを取得して、
			// リクエストハンドラで定義した@ModelAttribute("book") Book book の フォームのオブジェクトに上書きをする
			book = bookService.findBookDataById(id);
			mav.addObject("book", book);  // 必要 フォームに初期値として、表示するために
			break;
		}		
		return mav;		
	}
	
	/**
	 * 書籍 新規登録   編集  をする
	 * @param action
	 * @param id
	 * @param book
	 * @param result
	 * @param redirectAttributes
	 * @param request
	 * @param mav
	 * @return ModelAndView
	 */
	@RequestMapping(value="/book_add_edit", method=RequestMethod.POST)
	public ModelAndView bookAddEdit(
			@RequestParam( name = "action")String action,  // 必須のパラメータ hidden
			@RequestParam( name = "id")Integer id, // 必須のパラメータ  idは新規では 0なので 0で渡ってくる 編集の時にはidが入ってる
			@ModelAttribute("book")@Validated Book book,
			BindingResult result,
			RedirectAttributes redirectAttributes,  // 成功したら、リダイレクトするので必要
		    HttpServletRequest request, // requestオブジェクトから取得したい時に
			ModelAndView mav
			) {
		
		 if (result.hasErrors()) {
			// フォワード
        	mav.setViewName("book/form");       	
        	mav.addObject("msg", "入力エラーが発生しました。");
			// セレクトボックス用
        	Map<Integer, String> genreMap = viewBean.getGenreMap();       
    		// セレクトボックス表示用のgenreMap
    		mav.addObject("genreMap", genreMap);
    		mav.addObject("action", action);
        	return mav;  //returnで メソッドの即終了この後ろは実行されない
		 }
		 
		 // バリデーションエラーがなかったら actionによって分岐処理を進める
		  
	    boolean success = false;
	    String flashMsg = "";
		switch(action) {
		case "add":
			// 新規登録する  新規の時には フォームのオブジェクト を利用して
			// フォームオブジェクトにフォームの値が入ってるのでそのまま登録できる
			 success = bookService.create(book);
			if(success == false) { // データベース登録失敗
				// 失敗のメッセージとreturnする
				flashMsg = "書籍を新規登録できませんでした";
				mav.addObject("flashMsg", flashMsg);
				mav.setViewName("result");
				return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
			} else {
				// 成功してる
				flashMsg = "書籍を新規登録しました";
			}
			break;  // switch文を抜ける
		
		case "edit":
			// 書籍 編集する フォームオブジェクトにフォームの値が入ってるので そのまま引数にして更新できる
			success = bookService.update(book);
			if(success == false) { // データベース 更新 失敗
				// 失敗のメッセージとreturnする
				flashMsg = "書籍を更新できませんでした";
				mav.setViewName("result");
				return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
			} else {
				// 成功してる
				flashMsg = "書籍を更新しました";
			}
			break;  // switch文を抜ける
		}
		// 成功したら
		// リダイレクトします リダイレクトは、フォワードと違って、リダイレクト先のリクエストハンドラを実行させます。フォワードは、ビューを表示させるだけ
		// flashMsgは Flashスコープへ保存します スコープに置けるのは、参照型のインスタンスのみです。基本型(プリミティブ型)の変数は置けません intなら Integerの参照型にすれば置ける。
		// (また、自作のクラスのインスタンスは、サーブレットなら、Beanのクラスにすることが必要です)
		//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。
        // Flash Scop は Request Scope より長く、Session Scope より短いイメージ  リダイレクト先のリクエストハンドラでは、Flash Scopeから取り出すには、Modelインスタンスの getAttributeメソッドを使う
        // RedirectAttributesインスタンスの addFlashAttributeメソッドで Flash Scop に保存する
		redirectAttributes.addFlashAttribute("flashMsg" , flashMsg);
		// 書籍一覧を表示する
		 return new ModelAndView("redirect:/books");
	}
	
	
	/**
	 * 書籍 削除画面表示
	 * @param id
	 * @param mav
	 * @return
	 */
	@RequestMapping(value="/book_delete_confirm", method=RequestMethod.GET)
	public ModelAndView delete(
			@RequestParam(name = "id")Integer id, // 必須パラメータ
			ModelAndView mav
			) {
		mav.setViewName("book/confirm");
		Book book = bookService.findBookDataById(id);
		
		// Mapを渡す
		// 貸出中だったら、削除できないので、statusも表示する
		// メッセージも送る statusが 貸出中だったら、貸出中なので、削除できませんのメッセージを表示する
		
		
		mav.addObject("book", book);  // 必要 フォームに初期値として、表示するために
		return mav;
	}

	
	/**
	 * 書籍を削除する
	 * <a th:href="@{/book_delete(id=${book.id})}">
	 * formで method="post"  HTTPメソッドは POSTアクセスです
	 * ちなみに formタグの method="get"が デフォルトなので method属性を省略すると GETアクセスになる
	 * @param id
	 * @param redirectAttributes
	 * @param mav
	 * @return ModelAndView
	 */
	@RequestMapping(value="/book_delete", method=RequestMethod.POST)
	public ModelAndView delete(
			@RequestParam(name = "id")Integer id,  
			RedirectAttributes redirectAttributes,  // 成功したら、リダイレクトするので必要
			// HttpServletRequest request,  // requestオブジェクトから取得したい時に
			ModelAndView mav
			) {
		
		// 貸出中だったら、削除できませんので、それを書く
		
		String flashMsg = "";
		// 削除は 主キーidがあればできる
		boolean success = bookService.delete(id);
		
		if(success == false) { // データベース 更新 失敗
			// 失敗のメッセージとreturnする
			flashMsg = "書籍を削除できませんでした";
			mav.setViewName("result");
			return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
		} else {
			// 成功してる
			flashMsg = "書籍を削除しました";
		}
		redirectAttributes.addFlashAttribute("flashMsg" , flashMsg);
		// 書籍一覧を表示する
		 return new ModelAndView("redirect:/books");
		
	}
	
}
