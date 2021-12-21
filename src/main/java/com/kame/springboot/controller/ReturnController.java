package com.kame.springboot.controller;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.kame.springboot.form.ReturnForm;
import com.kame.springboot.service.BookService;
import com.kame.springboot.service.HistoryService;
import com.kame.springboot.service.MemberService;

@Validated  // クラスがバリデーションを使えるようにする  メソッドの引数に@Validを付与すると、その引数はバリデーションされるようになる
@Controller
public class ReturnController { // 返却に関する処理を行うコントローラ

	@Autowired
	HistoryService historyService;
	
	@Autowired
	BookService bookService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	Library library;  // Beanとして登録したクラスなので @Autowired でフィールド宣言して組み込めます
	
	
	
	 /**
     * 返却画面を表示する
     * @param returnForm
     * @param mav
     * @return ModelAndView
     */
    @RequestMapping( value = "/return_form", method=RequestMethod.GET)
    public ModelAndView returnForm(
    		@ModelAttribute("returnForm")ReturnForm returnForm,
    		ModelAndView mav
    		) {
    	mav.setViewName("return/returnForm"); 
    	
    	return mav;
    }
    
 
    /**
     * 返却する 
     * メソッド名にreturnは使えない予約語なので
     * @param returnForm
     * @param result
     * @param redirectAttributes
     * @param request
     * @param mav
     * @return ModelAndView
     */
    @RequestMapping( value = "/return", method=RequestMethod.POST)
    public ModelAndView returnBook(  //  返却する メソッド名にreturnは使えない予約語なので
    		@ModelAttribute("returnForm")@Validated ReturnForm returnForm,
    		BindingResult result,  // バリデーションエラーを取得するため  これは@ModelAttributeのすぐ下に書かないとエラーになるので注意
			RedirectAttributes redirectAttributes,  // 成功したら、リダイレクトするので必要
		    HttpServletRequest request, // requestオブジェクトから取得したい時に
    		ModelAndView mav
    		) {
    	
    	// まず、バリデーションチェック エラーがあるかどうか
    	if (result.hasErrors()) {
			// フォワード
        	mav.setViewName("return/returnForm");       	
        	mav.addObject("msg", "入力エラーが発生しました。");
			
        	return mav;  //returnで メソッドの即終了この後ろは実行されない
		 }
    	// まず、その本のIDが この図書館システムに所蔵してあるのか（登録してるかどうか)を確認する
    	int bookId = returnForm.getBookId();
    	String flashMsg = "";
    	
    	boolean exist = bookService.exist(bookId);
    	if(exist == false) {  // falseの時に
    		flashMsg = "この本のIDは存在しません";
			mav.addObject("flashMsg", flashMsg);
			mav.setViewName("return/returnForm");
			return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない

    	}
    	 // true この本が図書館システムに所蔵されている本だったので
    	
    	// bookIdを元に、その書籍の一番最後の貸し出し履歴を取得します historiesテーブルから取得する
    	//  "select * from histories where bookid = ?  order by id desc limit 1"
    	// LastHistoryDatalistは 最後の１つを取得してるので [] か もしくは 要素数は 1 かのどちらかです
    	List<Object[]> LastHistoryDatalist = historyService.getLastHistoryData(bookId);
    	// updateするには  貸し出し記録Historyの主キーだけが分かればいいが、
    	// 確認のため、returnDate に null が入ってるかどうかの確認をする nullだったら、上書きして返却処理できますが、
    	// nullじゃなかったら 貸し出し処理をしていない可能性(貸し出し処理をせずに返却処理をしようとしてる可能性)があるので、エラーとする
    // そうなら、この本は貸し出し処理をしていませんのメッセージをつけて 返す
    	int id = 0;  // 貸し出し記録 主キーこれだけが必要
    	
		Date lendDate = null;  // 本当は不要
		 Date returnDate = null;  // 必要
		 // int bookId = 0;
		 int memberId = 0;	// 必要
    	// その最後の貸し出し履歴の 返却日に今日の日付を入れます 返却処理
		 // 拡張forでもいいし 下のように Iterator を使ってもいい
    	for(Object[] obj : LastHistoryDatalist) {
//			System.out.println(obj[0]);
//			System.out.println(obj[1]);
//			System.out.println(obj[2]);
//			System.out.println(obj[3]);  
//			System.out.println(obj[4]);
			id =  Integer.parseInt(String.valueOf(obj[0])); 
			lendDate = (Date) obj[1];   // 不要だけど一応
			returnDate = (Date) obj[2];   // 必要
			// bookIdはわかってるので要らない
			memberId = Integer.parseInt(String.valueOf(obj[4]));   // 必要
		}
    	
    	// Iteratorを使ったやり方でもいい
//    	 Iterator itr =  LastHistoryDatalist.iterator();
//    	 while(itr.hasNext()) {
// 			Object[] obj = (Object[]) itr.next();
// 			id = Integer.parseInt(String.valueOf(obj[0]));
// 			lendDate = (Date) obj[1];
//			returnDate = (Date) obj[2];
//			memberId = Integer.parseInt(String.valueOf(obj[4])); 
// 		 }
    	
    	// チェックする
    	
    	if(returnDate != null) {
    		// nullじゃ無い時は、そもそも貸し出し記録が無い可能性 貸し出し処理をしていないのに 返却処理をしようとした可能性ある
    		flashMsg = "返却処理ができませんでした(この本は貸し出し処理をしていません)";
    		mav.addObject("flashMsg", flashMsg);
			mav.setViewName("return/returnForm");
			return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
    	} 
    	// returnDate が nullだったら、貸し出し処理がされていて 返却処理がまだの状態なので 処理を進められる
    	
    	// それから、 データベースで updateします
    	// updateには 主キー idだけでいい
    	boolean success = historyService.update(id);
    	if(success == false) { // データベース更新 失敗 updateメソッドのデータベース側での何らかのエラー
			// 失敗のメッセージとreturnする
    		// 失敗したら 返却のフォーム へフォワードする   失敗のメッセージとreturnする
			flashMsg = "返却処理ができませんでした(貸し出し記録を更新できませんでした)";
			mav.addObject("flashMsg", flashMsg);
			mav.setViewName("return/returnForm");
			return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
		
		} else {
			// 成功してる
			flashMsg = "返却しました。(貸し出し記録を更新しました)";
		}
    	mav.addObject("flashMsg", flashMsg);
    	mav.setViewName("return/result");  // 返却処理 結果ページへフォワードする
    	// 返却処理 結果ページでは  返却をした本の情報や状態  会員のIDなどを 表示する 
    	// 更新した後に historiesテーブルから 書籍のIDで絞り込んで そして主キーでソートをして limit 1　で 最新の貸し出し履歴を取得してる
    	 LastHistoryDatalist = historyService.getLastHistoryData(bookId);  // 更新後の  最新の状態を上書きする
    	 
    	// これで最後の貸し出し履歴のHistoryのデータ！履歴がまだない時は []
    	String status = library.getStatusStr(LastHistoryDatalist);
    	 
    	 

    	//Mapに変換するをnewして確保しておく  今後複数の本を同時に貸し出し 返却する時のためにMapで管理しておく
		 Map<Book, String> statusMap = new LinkedHashMap<Book, String>();  // LinkedHashMapは、格納した順番を記憶する
		 // Historyデータの bookidフィールドは Bookデータの主キーidを参照してるので Book情報を取得する
		 Book book = bookService.findBookDataById(bookId);
    	statusMap.put(book, status);
    	mav.addObject("statusMap", statusMap);
    
    	return mav; //  return で メソッドの即終了で、引数を呼び出し元へ返す この下は実行されない
	
    }
    
}
