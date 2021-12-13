package com.kame.springboot.controller;

import java.util.Calendar;
import java.util.Date;
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

import com.kame.springboot.bean.Library;
import com.kame.springboot.entity.Book;
import com.kame.springboot.entity.History;
import com.kame.springboot.entity.Member;
import com.kame.springboot.form.LendingForm;
import com.kame.springboot.service.BookService;
import com.kame.springboot.service.HistoryService;
import com.kame.springboot.service.MemberService;

@Validated  // クラスがバリデーションを使えるようにする  メソッドの引数に@Validを付与すると、その引数はバリデーションされるようになる
@Controller
public class HistoryController {  // 貸し出しに関するコントローラ
	
	@Autowired
	HistoryService historyService;
	
	@Autowired
	BookService bookService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	Library library;  // Beanとして登録したクラスなので @Autowired でフィールド宣言して組み込めます
    
	/**
	 * 貸し出し画面を表示する
	 * @param mav
	 * @return
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
     * 貸し出しができるのかを確認してから、貸し出し処理をする
     * リクエストパラメーターをバリデーションする
     * まず、このクラス宣言に@Validatedをつける  このクラスがバリデーションを使えるようにする
     * そしてリクエストハンドラ(メソッド)の引数に@Validを付与すると、その引数はバリデーションされるようになる
     * @NotBlank を付与することで、空文字・空白・nullを許容しなくなる
     * @NotEmpty だと、 半角空白文字でも、OKになってしまうので、半角空白もバリデーションでエラーにするには @NotBlank にすること
     * @NotNullを付与することで、nullのみ許容しなくなる intや Dateなどに使う
     * Integerだったら @NotNull を使う
     * @param bookId
     * @param memberId
     * @param mav
     * @return
     */
    @RequestMapping( value = "/lend", method=RequestMethod.POST)
    public ModelAndView lend(
    		//@Valid @NotNull @RequestParam(name = "bookId")Integer bookId,  // 必須パラメータ バリデーションつき Integerだから @NotNullを使う
    		// @Valid @NotNull @RequestParam(name = "memberId")Integer memberId, // 必須パラメータ バリデーションつき Integerだから @NotNullを使う
    		@ModelAttribute("lendingForm")@Validated LendingForm lendingForm,  
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
    	// History実体を作り、調べる 貸出できないようなら、貸出フォームに戻り エラーメッセージを出す
    	// もし 3冊借りるなら 3つのHistory実体を作り出し、それぞれを調べる。
    	// History実体を作っても、まだ貸し出しをしてはいないので 貸し出しできるか調べて、できるならhistoriesテーブルに登録して 初めて貸し出し処理完了となる 
    	History history = new History(bookId, memberId);  // 貸出する日は、new java.util.Date();  本日の日付を入れてる
    	
    	// bookIdで絞って検索したその本に関する貸し出し履歴Historyが複数あるが、その中で最新の貸し出し履歴を取得する
    	// historiesテーブルから select * from histories where bookid = ?  order by id desk limit 1; で探す
    	// その本の一番最新の貸し出し履歴 の情報が入ってるリストです
    	List<Object[]> historiesObjList = historyService.getOneBookHistoriesList(bookId);
    	
    	// BeanクラスのLibraryクラスcanLendメソッドに使う
    	String flashMsg = ""; 
    	Boolean canLend = library.canLend(history, historiesObjList);
    	if(canLend == false) {  // その本は貸し出しできない
    		// フォワード
        	mav.setViewName("lending/lendingForm");  
    		mav.addObject("msg", "貸し出しできません(貸し出し中)");
    		return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
    		
    	} else { // その本は貸し出しできる 
    		
    		// 貸し出し処理をする historiesテーブルに登録する
    		// 上で生成した historyインスタンスを、引数に、登録する
    		boolean success = historyService.add(history);
    		if(success == false) { // データベース登録失敗
				// 失敗したら フォワード   失敗のメッセージとreturnする
				flashMsg = "貸し出し処理の登録ができませんでした";
				mav.addObject("flashMsg", flashMsg);
				mav.setViewName("lending/lendingForm");
				return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
			} else {
				// 成功してる
				flashMsg = "貸し出し処理しました";
			}
        	
    	}
    	
    	// 成功 貸し出し処理を完了したら リダイレクトする
    	// リダイレクトします リダイレクトは、フォワードと違って、リダイレクト先のリクエストハンドラを実行させます。フォワードは、ビューを表示させるだけ
		// flashMsgは Flashスコープへ保存します スコープに置けるのは、参照型のインスタンスのみです。基本型(プリミティブ型)の変数は置けません intなら Integerの参照型にすれば置ける。
		// (また、自作のクラスのインスタンスは、サーブレットなら、Beanのクラスにすることが必要です)
		//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。
        // Flash Scop は Request Scope より長く、Session Scope より短いイメージ  リダイレクト先のリクエストハンドラでは、Flash Scopeから取り出すには、Modelインスタンスの getAttributeメソッドを使う
        // RedirectAttributesインスタンスの addFlashAttributeメソッドで Flash Scop に保存する
		redirectAttributes.addFlashAttribute("flashMsg" , flashMsg);
		// 貸し出し完了ページへ  リダイレクトして  貸し出した本を表示する
		redirectAttributes.addFlashAttribute("history" , history);
		 Date lendDate = history.getLendDate();  // 貸し出した日にち
		 // 返却予定日を取得する
		 Calendar calendar = Calendar.getInstance();
	      calendar.setTime(lendDate);
	      // 14日後
	      calendar.add(Calendar.DAY_OF_MONTH, 14);
	      Date twoWeekAfter = calendar.getTime();
	      // 返却予定日をビューへ送る
	      redirectAttributes.addFlashAttribute("twoWeekAfter" , twoWeekAfter);
	      // 貸し出した本の情報を取得する それもビューへ送る
	      Book book = bookService.findBookDataById(bookId);
	      redirectAttributes.addFlashAttribute("book" , book);
	      // 貸し出した会員の情報を取得する それもビューへ送る
	      Member member = memberService.findMemberDataById(memberId);
	      redirectAttributes.addFlashAttribute("member" , member);
	   // 貸出結果の画面へリダイレクト
		 return new ModelAndView("redirect:/lending/result"); 

    }
	

}
