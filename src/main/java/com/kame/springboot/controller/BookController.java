package com.kame.springboot.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
// @Transactional をつけないことサービスクラスのメソッドにつけますので、ダブルでつけると、ロールバックがうまく働かない
//このクラスのリクエストハンドラでは、@Transaction　せずに、try-catchしたいので つけないこと

@Controller
public class BookController {
	
//  コントローラには @Transactional をつけないこと サービスクラスで @Transactionalをつけるから、つけたら、ネストされた状態になるので 
	// ここのリクエストハンドラでtry-catchできなくなるので、つけないこと
	
	@Autowired
	BookService bookService;
	
	@Autowired
	ViewBean viewBean;
	
	@Autowired
	HistoryService historyService;
	
	@Autowired
	Library library;
	
	// リクエストハンドラでセッションスコープ使う、異なるコントローラと共有する セッションスコープBeanをつかうため
		@Autowired
		HttpSession session;
	
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
	 * 検索結果の表示は、searchAfterShow リクエストハンドラで処理してます
	 * idをURLのクエリーパラメータで送ってくる
	 * idから指定したレコードを取得して表示をする。
	 * @param id
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "/book_show/{id}", method=RequestMethod.GET)
	
	public ModelAndView show(
			@PathVariable Integer id,  // パス変数 /book_show/{id} の {id} と同じ変数名にする 必須パラメータ
			
			ModelAndView mav
			) {
		
	
		//  主キーid で探す bookのデータを取得 ISBNでは探さない はテーブル定義では ユニークにしなてない、なぜなら、同じ書籍を複数図書館システムが持つこともあるから
		// リポジトリの辞書機能によって メソッド自動生成機能を使用してる 戻り値は  List<Book> だが、中身は複数もありうる 無い時は []空
		// List<Book> books = bookService.findBookDataByIsbn(isbn); // 複数ありうるので違う
		Book book = bookService.findBookDataById(id);
		// bookは nullもありうる
		if(book == null) {
			// 主キーで探してもなかった、場合、図書館システムに登録されてない書籍なので
			// エラーメッセージを出して
			// フォワードすること
			
		}
		 //Mapに変換するをnewして確保しておく
		 Map<Book, String> statusMap = new LinkedHashMap<Book, String>();  // LinkedHashMapは、格納した順番を記憶する
				
		// mav.addObject("bookDataList", bookDataList);  // 書籍の情報を送る
//		Book book = null;
//		int id = 0;
//		if(bookDataList.size() > 0) {
//			book = bookDataList.get(0);
//			id = book.getId();
//		}
				
		// 書籍の状態(貸し出し中なのか 書架状態なのか)を表示するために
		// List<Object[]> LastHistoryDatalist = historyService.getLastHistoryData(id);
		List<Object[]> LastHistoryDatalist = historyService.getLastHistoryData(id);
//		
//		// これで最後の貸し出し履歴のHistoryのデータ！履歴がまだない時は []
		String status = library.getStatusStr(LastHistoryDatalist);
		
	// Mapに詰める
		statusMap.put(book, status);
		mav.addObject("statusMap",statusMap);
		
		
		
		mav.setViewName("book/show");
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
			@ModelAttribute("book")@Validated Book book,// これと 下のBindingResultは、すぐ下につけないと エラーになる BindingError
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
		 
		 // 新規登録でも、編集でも、同じISBNの本でも、複数冊所蔵することもありうる。人気本などは
		 // それぞれの本は主キーで管理されてるので、別々の本だけど、同じISBNの本だから、3冊あったら3冊のうち
		 // １さつだけ貸出中とか、わかるようにしたい。
		 
		 
		 
		  
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
			// required = falseが必要です リダイレクトの時には 無いから  任意パラメータにすること 必須パラメータにしたらダメ aリンクの idが クエリー文字列で送られてきてるけど、リダイレクトしてきた時には無いのでエラーになるから
			@RequestParam(name = "id", required = false)Integer id,  // required = falseが必要です 任意パラメータにすること 必須パラメータにしたらダメ
			ModelAndView mav,
			Model model   // 書籍削除できない時には、この確認画面を表示するのに リダイレクトしてくるので 必要
			) {
		// リダイレクトもしてくるので Flash Scopeから取り出すのに
				//   Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
				 // Flash Scop は Request Scope より長く、Session Scope より短いイメージ 
				// リダイレクト先のリクエストハンドラでは、Flash Scopeから取り出すには、Modelインスタンスの getAttributeメソッドを使う 取り出すのは Object型になってる
				String flashMsg = "";
				if(model.getAttribute("flashMsg") != null ) {
					flashMsg = (String) model.getAttribute("flashMsg");
				}
				if(model.getAttribute("id") != null ) {
					id = (Integer) model.getAttribute("id");
				}
		
		mav.setViewName("book/confirm");
		Book book = bookService.findBookDataById(id);
		
		// リダイレクトしてきた時に表示
		mav.addObject("flashMsg", flashMsg);
		
		mav.addObject("book", book);  // 必要 フォームに初期値として、表示するために
		return mav;
	}

	// このリクエストハンドラや　このクラスには @Transactionアノテーションをつけないこと
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
		
		// 貸出中だったら、削除できませんし、貸出履歴が一つでもあれば返却済みでも削除できないようにしてある
				
		// ここで、エラーをキャッチしたいので try-catchをつけること、なので、このクラスやリクエストハンドラには @Transaction　つけずに、サービスクラスのメソッドにだけつけること
				String flashMsg = "";
				boolean success = false;  // 削除処理が成功したら true 失敗したら false
				try {
					// このdeleteメソッドは PersistenceExceptionの例外インスタンスを投げる可能性があるので tryの中で実行する
					// このdeleteメソッドは  宣言の時に @Transactional と  throws文をつけてる  throws PersistenceException
					success = bookService.delete(id);  // 例外インスタンスを投げたら catch文へ行く
					// deleteメソッドが例外を投げなかったら、処理を続ける 
					if(success == false) {  // データベースで削除の失敗
						flashMsg = "書籍を削除できませんでした";
						
						redirectAttributes.addFlashAttribute("flashMsg", flashMsg);	
						redirectAttributes.addFlashAttribute("id", id);	// 注意
						// Flash Scopeに保存して、削除確認画面を表示するために リダイレクトする
						return new ModelAndView("redirect:/book_delete_confirm");  // return ここでメソッドの即終了 引数を呼び出し元へ返す 以降の行は実行されない

					}
					// success が true だったら 会員一覧にリダイレクトする
					flashMsg = "書籍を削除しました";
					
				} catch(PersistenceException e) {
					// ここでキャッチする  やりたい処理を書くPersistenceException発生するというのは、
					// リレーションのテーブルにデータがあるからなので
					flashMsg = "削除しようとした書籍には、貸出履歴があるので、貸出履歴を削除しないと この会員は削除できません。";
					redirectAttributes.addFlashAttribute("flashMsg", flashMsg);	
					redirectAttributes.addFlashAttribute("id", id);	
					// Flash Scopeに保存して、削除確認画面を表示するために リダイレクトする
					return new ModelAndView("redirect:/book_delete_confirm");  // return ここでメソッドの即終了 引数を呼び出し元へ返す 以降の行は実行されない
					
				}
				// 削除に成功したら 書籍一覧にリダイレクトする
			//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ

				//  リダイレクトは、フォワードと違って、リダイレクト先のリクエストハンドラを実行させます。フォワードは、ビューを表示させるだけ
				// flashMsgは Flashスコープへ保存します スコープに置けるのは、参照型のインスタンスのみです。基本型(プリミティブ型)の変数は置けません intなら Integerの参照型にすれば置ける。
				// (また、自作のクラスのインスタンスは、サーブレットなら、Beanのクラスにすることが必要です)
				//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。
		        // Flash Scop は Request Scope より長く、Session Scope より短いイメージ 
				// リダイレクト先のリクエストハンドラでは、Flash Scopeから取り出すには、Modelインスタンスの getAttributeメソッドを使う
		      
				redirectAttributes.addAttribute("flashMsg", flashMsg);   // RedirectAttributesインスタンスの addFlashAttributeメソッドで Flash Scop に保存する
				return new ModelAndView("redirect:/books"); // Flash Scopeに保存して、リダイレクトする		
			}
			
		
		
	
}
