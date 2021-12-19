package com.kame.springboot.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kame.springboot.entity.Book;
import com.kame.springboot.entity.History;
import com.kame.springboot.entity.Member;

@Controller
public class CSVController {
	
	// セッションスコープに保存したものを取り出して使うので 
	@Autowired
	HttpSession session;

	@RequestMapping(value = "/csv", method = RequestMethod.GET )
	public String csv(
			RedirectAttributes redirectAttributes  // Flash Scope を使うので必要 この後、また、貸し出し一覧ページへリダイレクトをするので
			) {
		
		// 異なるコントローラ間なので、セッションスコープから取得する 
		Map<Book, String> statusMap = (Map<Book, String>) session.getAttribute("statusMap");		
		Member member = (Member)session.getAttribute("member");		
		String twoWeekAfter = (String)session.getAttribute("twoWeekAfter");		
		History history = (History)session.getAttribute("history");
		
		// セッションスコープに置いたら、明示的に セッションスコープから 削除することが必要です
		session.removeAttribute("statusMap");  // 重要
		session.removeAttribute("member"); // 重要
		session.removeAttribute("twoWeekAfter"); // 重要
		session.removeAttribute("history"); // 重要
		
		String file_name = "/csv_result.csv"; //  拡張子も書く
		// Fileクラスのオブジェクトを作成 ユーザのデスクトップにファイルを作ろうとしているなら
		// File file = new File(System.getProperty("user.home") + "/Desktop" + file_name);
		
		File file = new File("src/main/resources" + file_name);

		try {
			file.createNewFile();  // その名前のファイルがまだ存在していない場合だけ、ファイルを作る。  戻り値は 指定されたファイルが存在せず、ファイルの生成に成功した場合はtrue、示されたファイルがすでに存在する場合はfalse
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(file.exists()) {
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			BufferedWriter bw = null;
			try {
				fos = new FileOutputStream(file);
				// 文字コードを指定して ファイルに書き込みます
				osw = new OutputStreamWriter(fos, "UTF-8");
				bw = new BufferedWriter(osw);
				// 見出し部分
				bw.write(String.format("%s,%s,%s,%s,%s\n",  "ID", "ISBN", "タイトル", "著者", "書架状態", "貸し出し日", "返却予定日"));

				// Mapから取り出す
				for(Iterator<Map.Entry<Book, String>> iterator = statusMap.entrySet().iterator() ; iterator.hasNext() ;){
				    Map.Entry<Book, String> entry = iterator.next();
				    
				    bw.write(String.format("%d,%s,%s,%s,%s,%s,%s\n",  entry.getKey().getId(), entry.getKey().getIsbn(), entry.getKey().getTitle(), entry.getKey().getAuthors(), entry.getValue(), history.getLendDate().toString(), twoWeekAfter.toString() ));
				 
				}
				
//				for(Employee employee : employeeNewList) {  
//					 bw.write(String.format("%s,%s,%d,%s,%d,%s,%s,%tF,%tF\n", employee.getEmployeeId(), employee.getName(), employee.getAge(), employee.getStringGender(employee.getGender()), employee.getPhotoId(), employee.getFullAddress(), employee.getDepartmentId(), employee.getHireDate(), employee.getRetirementDate()));
//				 }
//				bw.write(String.format("%s\n", "貸し出し日:" + history.getLendDate().toString()));  // javaは 参照型の変数はtoStoring()を呼び出せる 文字列に変換してくれる  プリミティブ型は呼び出せない
//				bw.write(String.format("%s\n", "返却予定日:" + twoWeekAfter.toString()));  // javaは 参照型の変数はtoStoring()を呼び出せる 文字列に変換してくれる  プリミティブ型は呼び出せない
				
				bw.write(String.format("%s\n", "会員ID:" + member.getId()));  // javaは 文字列と違う型を + で連結すると 自動的に文字列に変換してくれる
				 bw.flush();  // 必要です bw.flush(); 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally { // 最後にfinally句で  bw.close() osw.close() fos.close() の順番で クローズ処理する
				if(bw != null) {
					try {
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(osw != null) {
					try {
						osw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// セッションスコープから取得したものを、セッションスコープからは削除しましたので
		// リダイレクトのために、取り出しておいたものを、今度はFlash Scope へ保存します。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
		// セッションスコープへは なるべく置かないで、一回きりのリクエスト レスポンスで有効で良ければ Flash Scopeの方を使います 
		// この後また 貸し出し中一覧ページへリダイレクトするから
						
	    	//  フラッシュメッセージも Flash Scop へ保存する、Flash Scopへ インスタンスをセットできます。 Flash Scopは、１回のリダイレクトで有効なスコープです。 Request Scope より長く、Session Scope より短いイメージ
	    	// addFlashAttributeメソッドです  Flash Scope 使う RedirectAttributes redirectAttributes をリクエストハンドラの引数に書く
		
			// String flashMsg = "デスクトップに CSVファイルを出力しました。";
			String flashMsg = " CSVファイルを出力しました。";
			redirectAttributes.addFlashAttribute("flashMsg", flashMsg);
			redirectAttributes.addFlashAttribute("statusMap", statusMap);
			redirectAttributes.addFlashAttribute("member" , member);
			redirectAttributes.addFlashAttribute("twoWeekAfter" , twoWeekAfter);
			redirectAttributes.addFlashAttribute("history" , history);
			// 貸し出し中一覧ページへ リダイレクトする
			return "redirect:/on_loan";

	}
}
