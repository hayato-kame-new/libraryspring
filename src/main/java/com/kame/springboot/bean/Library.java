package com.kame.springboot.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kame.springboot.entity.Book;
import com.kame.springboot.entity.History;
import com.kame.springboot.entity.Member;
import com.kame.springboot.service.BookService;
import com.kame.springboot.service.MemberService;

// 普通のシンプルなJavaのクラス(POJO)
// 何のアノテーションもつけないただのクラスとして宣言をして、このクラスをBeanとして登録します まず、MyBootAppConfigクラスを定義する
/**
 * このクラスは何のアノテーションもつけないただのクラスとして宣言をして
 * MyBootAppConfigクラスのコンフィグのクラスでこのクラスをBeanとなるように設定していますので
 * このクラスは他のクラスでは @Autowired　をつけてフィールドに宣言して使えます。
 * @author skame
 *
 */
public class Library {  // Beanとして使えるクラスにしています MyBootAppConfigクラスで設定しました
	// ＠Entityのついたエンティティクラスに @Service @Repository のついてるのを @Autowiredで組み込むと
	// リレーションついてるとエラーになるので 組み込まないで このLibraryクラスに書く
	// @Entityクラスには テーブルについての記述だけ
	// @Repository を組み込んでる @Serviceのクラスにも、データベースとの間の記述だけ書くこと
	
	@Autowired
	BookService bookService;  // @Autowiredをつけた@Repositoryを組み込んだサービスクラスをエンティティのクラスのフィールドに書くと起動できなくなる　注意

	@Autowired
	MemberService memberService;  // @Autowiredをつけた@Repositoryを組み込んだサービスクラスをエンティティのクラスのフィールドに書くと起動できなくなる　注意
	
	 /**
     * 貸出し情報を表示する
     * このクラス内で使ってるメソッドです
     * @param history
     */
//    void printHistory(History history) {
//        this.printBook(history.getBook());
//        manager.print(history.getUser());
//        System.out.println( history.getLendDate() + " ~ " + history.getReturnDate());
//    }

	
	
	  /**
     * 貸し出し中であるかを判定するメソッド 異なるパッケージから見えるようにするため publicにする
     * 貸し出し中であれば，true， 貸し出し中でなければfalseを返す
     * 貸し出し中である，ということは  まだ返却されていない  ということ
     * returnDateにまだ値が代入されていないことを表します
     * 貸し出された時に初めてHistory型のオブジェクトが生成されるこの時には returnDate は 参照型の規定値(デフォルト値) のnull になってます
     * また，貸し出された時に初めてHistory型のオブジェクトが生成されるために， このオブジェクトについては，貸し出し前の状態を考える必要はありません
     * @return true:貸出中 <br /> false:貸出中ではない
     */
    public Boolean isLent(Date returnDate) {
        if(returnDate == null) {  // 貸出中である
            return true;  // 貸出中なら tureを返す
        }
        return false;  // 貸し出し中では無い falseを返す
    }
    
   
    /**
     * 貸し出しの状態を調べて 文字列で返す
     * LastHistoryDatalist サイズが 0なら、貸し出し履歴はないので まだ一度も貸出されていませんので 貸し出しOK
     * @param LastHistoryDatalist
     * @return String
     */
    public String getStatusStr(List<Object[]> LastHistoryDatalist) {
    	// 最後の貸し出し履歴のHistoryのデータ 履歴がまだない時は []
		int id = 0;
		Date lendDate = null;
		 Date returnDate = null;
		 //  int bookId = 0;
		 int memberId = 0;	
		 
		for(Object[] obj : LastHistoryDatalist) {
//			System.out.println(obj[0]);
//			System.out.println(obj[1]);
//			System.out.println(obj[2]);
//			System.out.println(obj[3]);  
//			System.out.println(obj[4]);
			id =  Integer.parseInt(String.valueOf(obj[0])); 
			// lendDate = (Date) obj[1];
			returnDate = (Date) obj[2];
			// bookIdは取らなくていい
			memberId = Integer.parseInt(String.valueOf(obj[4])); 
		}
    	String status = "";
    	if(LastHistoryDatalist.size() == 0) { // 該当の本は貸し出しの履歴は今まで一度も無い
    		status = "書架";
    	} else if(LastHistoryDatalist.size() > 0){ // 該当の本は貸し出しの最新の履歴１件はあります
    		// 最新の履歴の状態は 返却済みなのかどうか、 returnDate;  // 返却した日が nullなのかどうか
    		if(returnDate == null) {
    			// 貸し出し中です
    			status = "貸し出し中";
    		} else {
    			status = "書架";
    		}				
    	}
    	return status;
    }
   	// Iteratorを使ったやり方でもいい
// Iterator itr =  LastHistoryDatalist.iterator();
// while(itr.hasNext()) {
//		Object[] obj = (Object[]) itr.next();
//		id = Integer.parseInt(String.valueOf(obj[0]));
//		lendDate = (Date) obj[1];
//	returnDate = (Date) obj[2];
//	memberId = Integer.parseInt(String.valueOf(obj[4])); 
//	 }
// String status = "";
//// HistoryDatalistに要素がない サイズが 0なら、貸し出し履歴はないので まだ一度も貸出されていませんので
//// 貸出可能 書架の状態です
//if(LastHistoryDatalist.size() == 0) { // 該当の本は貸し出しの履歴は今まで一度も無い
//	status = "書架";
//} else if(LastHistoryDatalist.size() > 0){ // 該当の本は貸し出しの最新の履歴１件はあります
//	// 最新の履歴の状態は 返却済みなのかどうか、 returnDate;  // 返却した日が nullなのかどうか
//	if(returnDate == null) {
//		// 貸し出し中です
//		status = "貸し出し中";
//	} else {
//		status = "書架";
//	}				
//}
 	 

    /**
     * 貸し出し中なのか調べて 文字列で表示する
     * このメソッドないで使ってる
     * @return String
     */
    public String lentStr(Date returnDate) {
        String str = "";
        if(this.isLent(returnDate)) {
            str = "貸し出し中";
        } else {
            str = "配架中";
        }
        return str;
    }

    /**
     * 履歴の情報を画面に出力する
     * @return String
     */
    public String print(History history) {
    	Book book = bookService.findBookDataById(history.getBookId());
    	Member member = memberService.findMemberDataById(history.getMemberId());
    	String  str1 = "タイトル: " + book.getTitle() + ", 著者: " + book.getAuthors() + ", 出版社: " 
    			+ book.getPublisher() + ", 発行年: " +  book.getPublishYear() 
    			+ ", 書架状況: " +  this.lentStr(history.getReturnDate());
    	String separator = System.lineSeparator();  // システムに依存する改行になる
    	String str2 = ", 会員ID: " + member.getId()+ ", 会員名: " +  member.getName();
    	String str3 = history.getLendDate().toString() + " ~ ";
    	String str4 = "";
    	if(history.getReturnDate() != null) {
    		str4 = history.getLendDate().toString();
    	}
    	return str1 + separator + str2 + str3 + str4;
    }
    
    /**
     * この図書館システムに所蔵されているのかどうか調べる
     * このhousesメソッドで判断して、この図書館システムに所蔵されていれば
     * 次にcanLendメソッドで 貸出し可能か調べる
     * @param history
     * @param historiesObjList
     * @return true:この図書館に所蔵されてる <br /> false:この図書館に所蔵されていない
     */
    public Boolean houses(History history, List<Object[]> historiesObjList){
    	
    	// まず、これから貸し出そうとしている本が，この図書館システムが管理している本のリスト に存在するかを判定
        // containsメソッドで コレクション変数の中に 引数に渡された本が含まれているかを判定する
        //  ! で 判定を逆にしてるから 含まれていなければ falseを返す
 		   // query.getResultList()で取得したデータは List<Object[]>になってます  Iterable にキャストもできる (List<Book>)にキャストもできる
 	   List<Object[]> booksDataList = bookService.booksList();  // この図書館システムが保管する全ての本のリスト
 	   
 	   Iterator itr =  booksDataList.iterator();
 	   // booksDataList を  List<Book> のリストに詰め替える
 	   List<Book> booksList = new ArrayList<Book>();  // newして確保
 	   int id = 0;
 	   String isbn = "";
 		 String genre = "";
 		 String title = "";
 		 String authors = "";
 		 String publisher = "";
 		 Integer publishYear =null;
 		 
 		 while(itr.hasNext()) {
 			 Object[] obj = (Object[]) itr.next();
 			
 			 id = Integer.parseInt(String.valueOf(obj[0]));
 			 isbn = String.valueOf(obj[1]);
 			genre = String.valueOf(obj[2]);
 			title = String.valueOf(obj[3]);
 			authors = String.valueOf(obj[4]);
 			publisher = String.valueOf(obj[5]);
 			publishYear = Integer.parseInt(String.valueOf(obj[6]));
 			// Bookインスタンスを生成する
 			 Book book = new Book(id, isbn, genre, title, authors, publisher, publishYear);
 			 booksList.add(book);  // リストに追加する
 			 }

 		 // HistoryオブジェクトのbookIdフィールドから Bookがわかるので Bookインスタンスを取得
 		 Book book = bookService.findBookDataById(history.getBookId());
 		 // containsの中では equalsを使っています
 		 // Bookクラスでequalsメソッドと hashCode()をオーバーライドして、等しいの定義付けをする必要があります
 		 		 
 	   if(!booksList.contains(book)){  // equalsメソッドと hashCode()メソッドをOverrideしたので 機能する
            return false;  // これから貸し出そうとする本は、図書館システムには無い本なので 貸出できません
            // 貸出できないので、returnで即メソッドを終了させて、呼び出し元に引数の falseを返します。
            // このメソッドはreturnしたので、この行以降は実行されない
         }
 	   return true; // この図書館に所蔵されています
 			  
    }
    
    
    /**
     * housesメソッドで判断して、この図書館システムに所蔵されていれば このメソッドで貸し出しができるか判定する 
     * public をつけないと 他のパッケージからは見えないので publicつける
     * @param history   これから貸し出そうとしている History型の実体
     * @param histories  これから貸し出そうとする本に関するこれまでの貸し出し履歴であるHistory のリスト
     * @return true:貸出可能 <br /> false:貸出不可
     */
	   public Boolean canLend(History history, List<Object[]> historiesObjList){
    
       // 先に housesメソッドで調べてるので これから貸し出そうとする本は、この図書館システムに有るので、貸出できます。(貸出中じゃなければ)
       // 次に、貸し出し中かどうか調べます
       // その本の貸出履歴が存在しない場合 つまり histories.size() == 0 は，今まで借りられたことがないため，貸し出し可能
       // その本の貸出履歴があったら histories.size() > 0 だったら、貸出可能かどうかを調べる
        // その本に関する今までの貸出記録があれば
	   if(historiesObjList.size() == 0) {
		   // その本に関する貸し出し記録はまだ無い(まだ１冊も貸し出されたことがない)
    	   return true;  // 貸し出しできます ここでメソッド即終了して 引数の trueを呼び出し元へ返します
    	// return が実行されたら ここより下は実行されない
	   }else if(historiesObjList.size() > 0){  // 最後の貸し出し記録があれば
           // その本に関する最後の貸出記録の実態を取得する
    	  // List<Object[]>  historiesObjList です
    	   // historiesObjList 最後の貸し出し記録を取得してるので 要素の数は1つのみなので 先頭を取得すればいい
    	   Object[] obj = historiesObjList.get(0);  // [26, 2021-12-21, 2021-12-21, 40, 30]  返却済みだとこうなる
    	   if(obj[2] != null) {  //  returnDate が nullじゃない ので 返却済みで貸し出し可能
    	   return true;  // 貸し出しできます ここでメソッド即終了して 引数の trueを呼び出し元へ返します
    	   // return が実行されたら ここより下は実行されない
           } else {
        	   // nullだったら、現在は貸し出し中なので貸し出しができない状態です
        	   return false;  // 貸し出しできません  ここでメソッド即終了して 引数の falseを呼び出し元へ返します
        	// return が実行されたら ここより下は実行されない
           }
       }      
       return false;  
   }

    
    
}
