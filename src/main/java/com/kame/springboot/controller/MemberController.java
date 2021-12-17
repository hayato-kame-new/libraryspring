package com.kame.springboot.controller;

import java.time.LocalDate;

import javax.persistence.PersistenceException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.entity.Member;
import com.kame.springboot.form.MemberForm;
import com.kame.springboot.service.MemberService;
// @Transactional をつけないことサービスクラスのメソッドにつけますので、ダブルでつけると、ロールバックがうまく働かない
// このクラスのリクエストハンドラでは、@Transaction　せずに、try-catchしたいので つけないこと
@Controller
public class MemberController {
	
	//  コントローラには @Transactional をつけないこと サービスクラスで @Transactionalをつけるから、つけたら、ネストされた状態になるので 
	// ここのリクエストハンドラでtry-catchできなくなるので、つけないこと
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	ViewBean viewBean;
	
	/**
	 * 会員一覧表示する
	 * ページネーションと idでソートして表示する sort = { "id" } が必要
	 * @param pageable
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/members", method=RequestMethod.GET)
	public String index(
			@PageableDefault(page = 0, size = 10, sort = { "id" }) Pageable pageable, // id でソートしてる 重要
			
			Model model  // リダイレクトをしてくるので このmodelインスタンスのメソッドで Flash Scope から 取り出す
			) {
		
		// 会員を 新規登録 編集 削除 が成功したら リダイレクトしてくるので このリクエストハンドラを実行する
		// Flash Scope から 取り出すには Modelインスタンスの getAttributeメソッドを使用する
		String flashMsg = null;
		if(model.getAttribute("flashMsg") != null) {
			flashMsg = (String) model.getAttribute("flashMsg");  // Flash Scopeに保存してあるのは Object型のインスタンスなので キャストする
		}
		
		// ページネーションと idで ソートしたコレクション を取得
		Page<Member> memberPage = memberService.getAllMembers(pageable);
		model.addAttribute("page", memberPage);
		model.addAttribute("members", memberPage.getContent());
		
		model.addAttribute("flashMsg", flashMsg);
		
		return "member/members";
	}
	
	
	/**
	 * 会員 詳細画面を表示
	 * @param id
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "member_show", method=RequestMethod.GET)
	public ModelAndView show(
			@RequestParam(name = "id")Integer id,
			ModelAndView mav
			) {
		// 主キーid から Memberオブジェクトを取得する
		Member member = memberService.findMemberDataById(id);
		
		mav.setViewName("member/show");
		mav.addObject("member", member);
		return mav;
	}
	
	/**
	 * 会員 新規登録画面  編集画面 	 
	 * aリンクの ?以降のクエリー文字列を取得する
	 * 新規の時    ?action=add  になる  idのクエリー文字列はない 
	 * 編集の時     ?action=edit&id=2  などになる
	 * @param memberForm
	 * @param action
	 * @param id
	 * @param mav
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/member_form", method=RequestMethod.GET)
	public ModelAndView memberForm(
			@ModelAttribute("memberForm") MemberForm memberForm,  // フォームクラスを使う
			@RequestParam(name = "action")String action, // 必須パラメータ
		
			 @RequestParam(name = "id", required = false)Integer id, 
			ModelAndView mav			
			) {
		
		mav.setViewName("member/form");
		mav.addObject("action", action);  // フォームのビューへ送ること 必要
		
		
		Member member = null;
		switch(action) {
		case "add":
			// そのまま フォームのオブジェクトは 初期値のままでいい(各データ型の既定値(デフォルト値)）
			break;
			
		case "edit":
			// aリンクの idが クエリー文字列で送られてきてるので 
			// 主キーのidから、Memberオブジェクトを取得して、
			// リクエストハンドラで定義した@ModelAttribute("memberForm") MemberForm memberForm の フォームのオブジェクトに上書きをする
			// そして、それを mav.addObject("memberForm", memberForm);することが必要
			
			// member = memberService.findMemberDataById(id);  // 編集の時にはどっちでもいい こっちでもいい
			member = memberService.findMemberDataById(memberForm.getId());
			
			// フォームオブジェクトに上書きする
			memberForm.setName(member.getName());
			memberForm.setTel(member.getTel());
			memberForm.setAddress(member.getAddress());
			
			LocalDate localDateBirthday = member.getBirthDay();
			 Integer year = localDateBirthday.getYear();
			Integer month = localDateBirthday.getMonthValue();
			 Integer day = localDateBirthday.getDayOfMonth();
			memberForm.setYear(year);
			memberForm.setMonth(month);
			memberForm.setDay(day);
			// 上書きしたフォームオブジェクトをビューに送る
			mav.addObject("memberForm", memberForm);  // 必要 フォームに初期値として、表示するために
			break;		
		}		
		return mav;		
	}
		
	
	/**
	 *  会員を登録  編集 をする
	 * @param action
	 * @param id
	 * @param member
	 * @param result
	 * @param redirectAttributes
	 * @param request
	 * @param mav
	 * @return ModelAndView
	 */
	@RequestMapping( value = "/member_add_edit", method=RequestMethod.POST)
	public ModelAndView memberAddEdit(
			@RequestParam( name = "action")String action,  // 必須のパラメータ hidden
			@RequestParam( name = "id", required = false)Integer id, // 新規の時には無いのでnull  required = false で任意パラメータにする
			@ModelAttribute("memberForm")@Validated MemberForm memberForm,   // @Validatedをつけることによってバリデーション機能が働きます
			BindingResult result,
			RedirectAttributes redirectAttributes,  // 成功したら、リダイレクトするので必要
		    HttpServletRequest request, // requestオブジェクトから取得したい時に
			ModelAndView mav
			) {
		
		// もし、バリデーションエラーがあったら フォワード
		if (result.hasErrors()) {
			// フォワード
        	mav.setViewName("member/form");       	
        	mav.addObject("msg", "入力エラーが発生しました。");
			mav.addObject("action", action);
			mav.addObject("memberForm", memberForm);  // 必要
        	return mav;  //returnで メソッドの即終了この後ろは実行されない
		 }
		
		// バリデーションエラーがなかったら 処理を進める フォームのオブジェクトから取得する
	  // int id = memberForm.getId();  // 編集の時には入ってる 新規の時にはフォームはないのでint型のデフォルト値(規定値) 0 のままです
		String name = memberForm.getName();
		String tel = memberForm.getTel();
		String address = memberForm.getAddress();
		int year = memberForm.getYear();
		int month = memberForm.getMonth();
		int day = memberForm.getDay();
		LocalDate birthday = LocalDate.of(year, month, day);
		
		Member member = null;
		
		// バリデーションエラーがなかったら actionによって分岐処理を進める
		
		boolean success = false;
	    String flashMsg = "";
	    switch(action) {
	    case "add":
	    	// 新規登録する  新規の時には フォームのオブジェクト を利用して
	    	member = new Member(name, tel, address, birthday);
	    	success = memberService.create(member);
	    	if(success == false) { // データベース登録失敗
				// 失敗のメッセージとreturnする
				flashMsg = "会員を新規登録できませんでした";
				mav.addObject("flashMsg", flashMsg);
				mav.setViewName("result");
				return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
			} else {
				// 成功してる
				flashMsg = "会員を新規登録しました";
			}
	    	break; // switch文を抜ける
	    	
	    case "edit":
	    	// 書籍 編集する フォームオブジェクトにフォームの値が入ってるので そのまま引数にして更新できる
	    	member = new Member(id, name, tel, address, birthday);
	    	success = memberService.update(member);
	    	
	    	if(success == false) { // データベース更新失敗
				// 失敗のメッセージとreturnする
				flashMsg = "会員を更新できませんでした";
				mav.addObject("flashMsg", flashMsg);
				mav.setViewName("result");
				return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
			} else {
				// 成功してる
				flashMsg = "会員を更新しました";
			}
	    	break; // switch文を抜ける
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
 		 return new ModelAndView("redirect:/members");
	}
	
	
	
	// 削除確認画面を表示する* 削除する際も、一度内容を確認してからなので 削除の確認画面を表示する
	@RequestMapping( value = "/member_delete_confirm" , method=RequestMethod.GET)
	public ModelAndView deleteConfirm(
			// required = falseが必要です リダイレクトの時には 無いから  任意パラメータにすること 必須パラメータにしたらダメ aリンクの idが クエリー文字列で送られてきてるけど、リダイレクトしてきた時には無いのでエラーになるから
			@RequestParam(name = "id", required = false)Integer id,  // required = falseが必要です 任意パラメータにすること 必須パラメータにしたらダメ
			@ModelAttribute("memberForm") MemberForm memberForm,
			ModelAndView mav,
			Model model   // 会員削除できない時には、この確認画面を表示するのに リダイレクトしてくるので 必要
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
			
			mav.setViewName("member/confirm");
				
			Member member = memberService.findMemberDataById(id);  // 削除ときには idは aリンクのクエリー文字列から取得する
				 
			// フォームオブジェクトに上書きする
		    memberForm.setId(member.getId());
			memberForm.setName(member.getName());
			memberForm.setTel(member.getTel());
			memberForm.setAddress(member.getAddress());
			
			LocalDate localDateBirthday = member.getBirthDay();
			Integer year = localDateBirthday.getYear();
			Integer month = localDateBirthday.getMonthValue();
			Integer day = localDateBirthday.getDayOfMonth();
			memberForm.setYear(year);
			memberForm.setMonth(month);
			memberForm.setDay(day);
			// 上書きしたフォームオブジェクトをビューに送る
			mav.addObject("memberForm", memberForm);  // 必要 フォームに初期値として、表示するために
			
			// リダイレクトしてきた時に表示
			mav.addObject("flashMsg", flashMsg);
			return mav;
		
	}
	
	
	// このリクエストハンドラや　このクラスには @Transactionアノテーションをつけないこと
	/**
	 * 会員を削除する
	 * @param id
	 * @param redirectAttributes
	 * @param mav
	 * @return ModelAndView
	 */
	@RequestMapping( value = "/member_delete" , method=RequestMethod.POST)
	public ModelAndView delete(
			@RequestParam( name = "id")Integer id,
			RedirectAttributes redirectAttributes,  // 成功したら、リダイレクトするので必要
			// HttpServletRequest request,  // requestオブジェクトから取得したい時に
			ModelAndView mav			
			) {
		
		// idがあれば Memberデータを削除できるがmembersテーブルが親テーブルでリレーションがあるので
		// historiesテーブルは membersテーブルの子テーブルなので、historiesテーブルに参照する memeberidカラムがあるので
		// historiesテーブルにデータがある場合は、会員削除できません
		// ERROR: update or delete on table "members" violates foreign key constraint "histories_memberid_fkey" on table "histories"
		//   詳細: Key (id)=(11) is still referenced from table "histories".
		// 子テーブルのhistoriesテーブルは
		// ここで、エラーをキャッチしたいので try-catchをつけること、なので、このクラスやリクエストハンドラには @Transaction　つけずに、サービスクラスのメソッドにだけつけること
		String flashMsg = "";
		boolean success = false;  // 削除処理が成功したら true 失敗したら false
		try {
			// このdeleteメソッドは PersistenceExceptionの例外インスタンスを投げる可能性があるので tryの中で実行する
			// このdeleteメソッドは  宣言の時に @Transactional と  throws文をつけてる  throws PersistenceException
			success = memberService.delete(id);  // 例外インスタンスを投げたら catch文へ行く
			// deleteメソッドが例外を投げなかったら、処理を続ける 
			if(success == false) {  // データベースで削除の失敗
				flashMsg = "会員を削除できませんでした";
				
				redirectAttributes.addFlashAttribute("flashMsg", flashMsg);	
				redirectAttributes.addFlashAttribute("id", id);	// 注意
				// Flash Scopeに保存して、削除確認画面を表示するために リダイレクトする
				return new ModelAndView("redirect:/member_delete_confirm");  // return ここでメソッドの即終了 引数を呼び出し元へ返す 以降の行は実行されない

			}
			// success が true だったら 会員一覧にリダイレクトする
			flashMsg = "会員を削除しました";
			
		} catch(PersistenceException e) {
			// ここでキャッチする  やりたい処理を書くPersistenceException発生するというのは、
			// リレーションのテーブルにデータがあるからなので
			flashMsg = "削除しようとした会員には、貸出履歴があるので、貸出履歴を削除しないと この会員は削除できません。";
			redirectAttributes.addFlashAttribute("flashMsg", flashMsg);	
			redirectAttributes.addFlashAttribute("id", id);	
			// Flash Scopeに保存して、削除確認画面を表示するために リダイレクトする
			return new ModelAndView("redirect:/member_delete_confirm");  // return ここでメソッドの即終了 引数を呼び出し元へ返す 以降の行は実行されない
			
		}
		// 削除に成功したら 会員一覧にリダイレクトする
	//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ

		//  リダイレクトは、フォワードと違って、リダイレクト先のリクエストハンドラを実行させます。フォワードは、ビューを表示させるだけ
		// flashMsgは Flashスコープへ保存します スコープに置けるのは、参照型のインスタンスのみです。基本型(プリミティブ型)の変数は置けません intなら Integerの参照型にすれば置ける。
		// (また、自作のクラスのインスタンスは、サーブレットなら、Beanのクラスにすることが必要です)
		//  Flash Scop へ、インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。
        // Flash Scop は Request Scope より長く、Session Scope より短いイメージ 
		// リダイレクト先のリクエストハンドラでは、Flash Scopeから取り出すには、Modelインスタンスの getAttributeメソッドを使う
      
		redirectAttributes.addAttribute("flashMsg", flashMsg);   // RedirectAttributesインスタンスの addFlashAttributeメソッドで Flash Scop に保存する
		return new ModelAndView("redirect:/members"); // Flash Scopeに保存して、リダイレクトする		
	}
	
	// 会員を検索する
	
}
