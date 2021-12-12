package com.kame.springboot.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.entity.History;
import com.kame.springboot.service.HistoryService;

@Validated  // クラスがバリデーションを使えるようにする  メソッドの引数に@Validを付与すると、その引数はバリデーションされるようになる
@Controller
public class HistoryController {  // 貸し出しに関するコントローラ
	
	@Autowired
	HistoryService historyService;
    
	/**
	 * 貸し出し画面を表示する
	 * @param mav
	 * @return
	 */
    @RequestMapping( value = "/lending_form", method=RequestMethod.GET)
    public ModelAndView lendingForm(
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
    		@Valid @NotNull @RequestParam(name = "bookId")Integer bookId,  // 必須パラメータ バリデーションつき Integerだから @NotNullを使う
    		@Valid @NotNull @RequestParam(name = "memberId")Integer memberId, // 必須パラメータ バリデーションつき Integerだから @NotNullを使う
    		ModelAndView mav
    		) {
    	
    	// 貸出できるのか確認をまずする
    	// History実体を作り、調べる 貸出できないようなら、貸出フォームに戻り エラーメッセージを出す
    	// もし 3冊借りるなら 3つのHistory実体を作り出し、それぞれを調べる。
    	// History実体を作っても、まだ貸し出しをしてはいないので 
    	History history = new History(bookId, memberId);  // 貸出する日は、new java.util.Date();  本日の日付を入れてる
    	
    	// bookIdで絞って検索したその本に関する貸し出し履歴Historyが複数あるが、その中で最新の貸し出し履歴を取得する
    	// historiesテーブルから select * from histories where bookid = ?  order by id desk limit 1; で探す
    	// その本の一番最新の貸し出し履歴 の情報が入ってるリストです
    	List<Object[]> historiesObjList = historyService.getOneBookHistoriesList(bookId);
    	// canLendメソッドに使いたいから Library.javaクラスのcanLendメソッドを中身を書き換えないといけない
    	
    	
    	// ここから
    	 
    	
    	// 貸出結果の画面へ行く
    	
    }
	

}
