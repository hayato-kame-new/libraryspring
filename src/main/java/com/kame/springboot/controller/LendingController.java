package com.kame.springboot.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.bean.Library;
import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.Book;
import com.kame.springboot.entity.History;
import com.kame.springboot.entity.Member;
import com.kame.springboot.form.LendingForm;
import com.kame.springboot.service.BookService;
import com.kame.springboot.service.HistoryService;
import com.kame.springboot.service.MemberService;

@Validated  // クラスがバリデーションを使えるようにする  メソッドの引数に@Validを付与すると、その引数はバリデーションされるようになる
@Controller
public class LendingController {  // 貸し出しに関するコントローラ
	
	
	@Autowired
	HistoryService historyService;
	
	@Autowired
	BookService bookService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	Library library;  // Beanとして登録したクラスなので @Autowired でフィールド宣言して組み込めます
	
	@Autowired
	ViewBean viewBean;
	
	// CSVへ出力するために、結果のリストを他のコントローラへ送るのでセッションスコープへ保存する
	@Autowired
	HttpSession session;
    
	
	 // th:href="@{/onLoan( id=${member.id})}" members.htmlから aリンクでもくるし、CSV出力後にリダイレクトでもくる
    // 指定した会員の貸し出し一覧を表示する  リダイレクトは HTTPメソッドは GETになるので GETにする
    
    @RequestMapping( value = "/on_loan", method=RequestMethod.GET)  // リダイレクトは HTTPメソッドは GETになるので GETにする
    public ModelAndView onLoan(
    		@RequestParam(name = "id", required = false)Integer id,  // 会員ID  required = false必要 任意パラメータにする aリンクの時だけ ? 以降のクエリー文字列で送られてくる
    	
    		ModelAndView mav,
    		Model model  // Flash Scopeから取得するので Modelインスタンスが必要 リダイレクトしてくるので
    		) {
    	
    	// もし、あればFlashスコープから取得する 
    	String flashMsg = "";
		Member member = null;		
		
    	if(model.getAttribute("flashMsg") != null) {
    		flashMsg = (String) model.getAttribute("flashMsg");
    	}
    	if(model.getAttribute("member") != null) {
    		member = (Member) model.getAttribute("member");
    	}
    	
    	// リダイレクトしてきた時には null になってますのでスルー
    	if(id != null) {  // member.htmlから aリンクでアクセスしてきたら そのidでmemberを上書き
    		member = memberService.findMemberDataById(id);
    	}
    	int memberId = member.getId();  // これを使う
    	// 取得した member  主キーidを使う.   親テーブル membersテーブルのidカラム（主キー)は、
    	// 子テーブル historiesテーブルのmemberidカラム から参照されているので
    	// historiesテーブルから その会員ID(memberidカラム)で絞ったデータで、 
    	// returnDate の値が nullの貸し出し中Historyデータを全て取得してくる 
    	//  select * from histories where memberid = 14 and returndate is null;
    	// List<Object[]>   になってます
    	List<Object[]> returndateIsNullDataList = historyService.getReturndateIsNullData(memberId);
    	int isNullDataCount = returndateIsNullDataList.size();
    	String countMsg = "貸し出し件数は " + isNullDataCount + " 件です";
    	// Mapにして送る
    	// Map<Book, History>()　型にして送ります
    	
    	Map<Book, History> isNullMap = new LinkedHashMap<Book, History>();  // newで確保 中身 {}
    	// returndateIsNullDataList List<Object[]>   になってます ので  Iterator<Object[]> にします
    	// returndateIsNullDataList　見つからない時には []になってるので、
		  History history = null;  // 見つからない時には []になってるので nullを代入してそれをreturnしてます
		  Book book = null;
		 int history_id = 0;
		   Date lendDate = null;  
		   Date returnDate = null;
		  int bookId = 0;
		  //  int memberId = 0;  // これはパラメータで送られてきてる idを使うので要らない
		  
		  
		  if(returndateIsNullDataList.size() > 0) {  // 貸し出し中が ある時
			  // 見つかったので History型のオブジェクトに詰め替えてる
			  Iterator<Object[]> itr = returndateIsNullDataList.iterator();
			  while(itr.hasNext()) {
				  Object[] obj = (Object[]) itr.next();
				  
				  history_id = Integer.parseInt(String.valueOf(obj[0]));
				  java.sql.Date sqlLendDate = (java.sql.Date)(obj[1]);
				  java.util.Date utilLendDate = new Date(sqlLendDate.getTime());
				  // nullとわかってるので変換するとエラーだし、要らない
//				  java.sql.Date sqlReturnDate = (java.sql.Date)(obj[2]);
//				  java.util.Date utilReturnDate = new Date(sqlLendDate.getTime());
				  bookId = Integer.parseInt(String.valueOf(obj[3]));
				  // public History(int id, Date lendDate, Date returnDate, int bookId, int memberId)
				  // Historyインスタンスにして生成して これをMapのバリュー(値)に追加する
				  history = new History(history_id, utilLendDate, bookId, memberId);
				  
				  // Bookインスタンスを取得する これをMapのキーにする bookIdから探せる
				   book = bookService.findBookDataById(bookId);
				  isNullMap.put(book, history);
				  
				  }
		  } else { //貸し出し中のがなかったら
			  // Historyインスタンス Bookインスタンス は null のまま isNullMapも[]のまま
			  
			  
		  }
		
    	mav.addObject("flashMsg", flashMsg);
    	mav.addObject("member", member);
    	mav.addObject("countMsg", countMsg);
    	mav.addObject("isNullMap", isNullMap);
    	// mav.addObject("list", returndateIsNullDataList);
    	mav.setViewName("history/onLoan");
    	
    	return mav;
    }
	
	
	/**
	 * 貸し出し画面を表示する
	 * @param mav
	 * @return ModelAndView
	 */
    @RequestMapping( value = "/lending_form", method=RequestMethod.GET)
    public ModelAndView lendingForm(
    		@ModelAttribute("lendingForm")LendingForm lendingForm,
    		ModelAndView mav
    		) {
    	
    	mav.setViewName("lending/lendingForm");

    	return mav;
    }
    
    /**
     *  貸し出しができるのかを確認してから、貸し出し処理をする
     *  Integer Date だったら @NotNull を使う
     *  @NotEmpty だと、 半角空白文字でも、OKになってしまうので、半角空白もバリデーションでエラーにするには @NotBlank にすること
     * @param lendingForm
     * @param result
     * @param redirectAttributes
     * @param request
     * @param mav
     * @return
     */
    @RequestMapping( value = "/lend", method=RequestMethod.POST)
    public ModelAndView lend(
    		// リクエストパラメーターをバリデーションする時には このクラスの宣言に @Validatedをつける このクラスがバリデーションを使えるようにする
    		// そしてリクエストハンドラ(メソッド)の引数に@Validを付与すると、その引数はバリデーションされるようになる
    		// @NotNullを付与することで、nullのみ許容しなくなる intや Dateなどに使う
    		//@Valid @NotNull @RequestParam(name = "bookId")Integer bookId,  // 必須パラメータ バリデーションつき Integerだから @NotNullを使う
    		// @Valid @NotNull @RequestParam(name = "memberId")Integer memberId, // 必須パラメータ バリデーションつき Integerだから @NotNullを使う
    		@ModelAttribute("lendingForm")@Validated LendingForm lendingForm,  // これと 下のBindingResultは、すぐ下につけないと エラーになる BindingError
    		BindingResult result,  // バリデーションエラーを取得するため
			RedirectAttributes redirectAttributes,  // 成功したら、リダイレクトするので必要
		    HttpServletRequest request, // requestオブジェクトから取得したい時に
    		ModelAndView mav
    		) {
    	
    	Integer bookId = lendingForm.getBookId();
    	Integer memberId = lendingForm.getMemberId();
    	
    	// 最初にバリデーションエラー発生してるかどうか
    	if (result.hasErrors()) {
			// フォワード
        	mav.setViewName("lending/lendingForm");       	
        	mav.addObject("msg", "入力エラーが発生しました。");
        	mav.addObject("lendingForm", lendingForm);  // 必要です！！ 
        	return mav;  //returnで メソッドの即終了この後ろは実行されない
		 }
    	
    	
    	// 貸出できるのか確認をまずする
    	
    	// 会員ID  memberIdから、その会員がこの図書館システムに登録している会員なのか調べる
    	// historiesテーブルの memberIdカラムは リレーションで 親テーブルのmembersテーブルの 主キーidを参照していますので
    	// membersテーブルから探す
    	Member member = memberService.findMemberDataById(memberId);
    	if(member == null) {
    	// このIDの会員は この図書館システムには 存在しない会員なので 
    		// フォワード returnでこのリクエストハンドラを即終了する 以降の行は実行されない
    		mav.setViewName("lending/lendingForm");  
    		mav.addObject("msg", "貸し出しできません(会員は登録されていません)");
    		return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
    		
    	}
    	
    	// History実体を作り、調べる 貸出できないようなら、貸出フォームに戻り エラーメッセージを出す
    	// もし 3冊借りるなら 3つのHistory実体を作り出し、それぞれを調べる。
    	// History実体を作っても、まだ貸し出しをしてはいないので 貸し出しできるか調べて、できるならhistoriesテーブルに登録して 初めて貸し出し処理完了となる 
    	History history = new History(bookId, memberId);  // 貸出する日は、new java.util.Date();  本日の日付を入れてる
    	
    	// bookIdで絞って検索したその本に関する貸し出し履歴Historyが複数あるが、その中で最新の貸し出し履歴を取得する
    	// historiesテーブルから select * from histories where bookid = ?  order by id desk limit 1; で探す
    	// その本の一番最新の貸し出し履歴 の情報が入ってるリストです このデータの returnDateが nullなら、ただ今貸し出し中とわかる
    	List<Object[]> LastHistoryData = historyService.getLastHistoryData(bookId);
    	
  
    	String flashMsg = ""; 
    	// まず、housesメソッドで その書籍がこの図書館システムの所蔵されている本なのかどうか、調べる
    	// trueを返せば この図書館システムの所蔵されている本
    	Boolean houses = library.houses(history, LastHistoryData);
    	if(houses == false) {
    		// そのIDの書籍は図書館システムにはない
    		// フォワード returnでこのリクエストハンドラを即終了する 以降の行は実行されない
    		mav.setViewName("lending/lendingForm");  
    		mav.addObject("msg", "貸し出しできません(書籍は所蔵されていません)");
    		return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
    	}
    	// この図書館システムの所蔵されている本なので、次に canLend メソッドで その本が貸出可能なのか調べる
    	// canLend メソッドでは、returnDateの値を調べ、returnDate が null だったら、ただ今貸し出し中となる
    	Boolean canLend = library.canLend(history, LastHistoryData);
    	if(canLend == false) {  // その本は貸し出しできない
    		//  貸し出し中
    		// フォワード
        	mav.setViewName("lending/lendingForm");  
    		mav.addObject("msg", "貸し出しできません(貸し出し中)");
    		return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
    		
    	} else { // true その本は貸し出しできる 
    		
    		// 貸し出し処理をする historiesテーブルに登録する
    		// 上で生成した historyインスタンスを、引数に、登録する
    		boolean success = historyService.add(history);
    		if(success == false) { // データベース登録失敗
				// 失敗したら 貸し出しのフォームへフォワードする   失敗のメッセージとreturnする
				flashMsg = "貸し出し処理の登録ができませんでした";
				mav.addObject("flashMsg", flashMsg);
				mav.setViewName("lending/lendingForm");
				return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
			} else {
				// 成功してる 
				flashMsg = "貸し出し処理しました";
			}       	
    	}   	
    	// 成功したら 貸し出し処理結果ページへを完了したら 貸し出し完了ページへフォワードします
    	mav.setViewName("lending/result");
    	mav.addObject("flashMsg" , flashMsg);
		
		// 貸し出し完了ページへ   貸し出した本を表示する
    	mav.addObject("history" , history);
		
		 Date lendDate = history.getLendDate();
		// 貸し出した日にちの二週間後が 返却予定日
		 Calendar calendar = Calendar.getInstance();
	      calendar.setTime(lendDate);
	      // 14日後
	      calendar.add(Calendar.DAY_OF_MONTH, 14);
	      Date twoWeekAfter = calendar.getTime();
	      // 返却予定日を 貸し出し完了ページへ送る
	      mav.addObject("twoWeekAfter" , twoWeekAfter);
	      // 貸し出し完了ページへ送るためのデータをMapにする このMapはCSVファイル出力のため、セッションスコープへ保存もする
	      // 貸し出した本の情報を取得してMapのキーにする 
	      // その貸し出した本の状態statusをMapの値にする 
	      // データベースに登録した その本の最後の貸し出し記録が、今回の貸し出し記録になります
	      // まず、更新されたその本の最後の貸し出し記録が 今回の貸し出し記録なので 取得する historyDataから取り出した情報 は、セッションスコープにも置く
	      List<Object[]> historyData = historyService.getLastHistoryData(bookId); 
	     
	    	// 更新された最後の貸し出し記録historyData から、情報の文字列を取得する
	    	String status = library.getStatusStr(historyData);
	    	//Mapに変換するをnewして確保しておく  今後複数の本を同時に貸し出し 返却する時のためにMapで管理しておく
			 Map<Book, String> statusMap = new LinkedHashMap<Book, String>();  // LinkedHashMapは、格納した順番を記憶する
			 // Historyデータの bookidフィールドは Bookデータの主キーidを参照してるので Book情報を取得する
			 Book book = bookService.findBookDataById(bookId); // 貸し出した本の情報をMapのキーにして送る
	    	// Mapに追加
			 statusMap.put(book, status);
			 // ビューへ送る
	    	mav.addObject("statusMap", statusMap);
	    	
	    // この statusMap は、セッションスコープにも保存しておく CSVControllerで取り出して使うため 取り出したあとはセッションスコープから明示的に削除する
	    	session.setAttribute("statusMap", statusMap);
	    	session.setAttribute("member" , member);
	    	session.setAttribute("twoWeekAfter" , twoWeekAfter);
	    	session.setAttribute("history" , history);
	    	
	    
	    	// 貸し出した会員の情報も貸し出し完了ページへ送る   
	    	mav.addObject("member" , member);
	   // 貸出結果の画面へフォワードする
		 return mav;
    }
    

}
