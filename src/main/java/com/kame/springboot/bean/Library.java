package com.kame.springboot.bean;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kame.springboot.entity.Book;
import com.kame.springboot.entity.History;
import com.kame.springboot.service.BookService;

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
	BookService bookService;  // これをエンティティのクラスのフィールドに書くと起動できなくなる　注意

	
	 /**
     * 貸出し情報を表示する
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
     * 貸し出し中なのか調べて 文字列で表示する
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
    	String  str1 = "タイトル: " + history.getBook().getTitle() + ", 著者: " +history.getBook().getAuthors() + ", 出版社: " 
    			+ history.getBook().getPublisher() + ", 発行年: " +  history.getBook().getPublishYear() 
    			+ ", 書架状況: " +  this.lentStr(history.getReturnDate());
    	String separator = System.lineSeparator();  // システムに依存する改行になる
    	String str2 = ", 会員ID: " + history.getMember().getId()+ ", 会員名: " +  history.getMember().getName();
    	String str3 = history.getLendDate().toString() + " ~ ";
    	String str4 = "";
    	if(history.getReturnDate() != null) {
    		str4 = history.getLendDate().toString();
    	}
    	return str1 + separator + str2 + str3 + str4;
    }
    
    
    /**
     * 貸し出しができるか判定する 
     * public をつけないと 他のパッケージからは見えないので publicつける
     * @param history   これから貸し出そうとしている History型の実体
     * @param histories  これから貸し出そうとする本に関するこれまでの貸し出し履歴であるHistory のリスト
     * @return true:貸出可能 <br /> false:貸出不可
     */
    
    // 第二引数変わりますので、このメソッドの内容を書き換えないとけません！！
    // 第二引数は、History実体の本に関しての、今までの貸し出し履歴の中で一番最新の貸し出し履歴の情報が入ってる List<Object[]> historiesObjList
      // 変更してから使うこと！！！！
   // Boolean canLend(History history, List<History> histories){
	   public Boolean canLend(History history, List<Object[]> historiesObjList){
       // まず、これから貸し出そうとしている本が，この図書館システムが管理している本のリストshelf に存在するかを判定
       // containsメソッドで コレクション変数shelfの中に 引数に渡された本が含まれているかを判定する
       //  ! で 判定を逆にしてるから 含まれていなければ falseを返す
	   List<Book> books = bookService.booksList();  // この図書館システムが保管する全ての本のリスト
       if(!books.contains(history.getBook())){
           return false;  // これから貸し出そうとする本は、図書館システムには無い本なので 貸出できません
//           // 貸出できないので、returnで即メソッドを終了させて、呼び出し元に引数の falseを返します。
//           // このメソッドはreturnしたので、この行以降は実行されない
        }

       // これから貸し出そうとする本は、この図書館システムに有るので、貸出できます。(貸出中じゃなければ)
       // 次に、貸し出し中かどうか調べます
       // その本の貸出履歴が存在しない場合 つまり histories.size() == 0 は，今まで借りられたことがないため，貸し出し可能
       // その本の貸出履歴があったら histories.size() > 0 だったら、貸出可能かどうかを調べる
      //  if(histories.size() > 0){  // その本に関する今までの貸出記録があれば
       if(historiesObjList.size() > 0){  // 最後の貸し出し記録があれば
           // その本に関する最後の貸出記録の実態を取得する
           // List<History> は、 右辺が new ArrayList<Hirstory>(); で初期化されてるため、ArrayListは、中身は配列と同じ構造なので、
           //  履歴は順に格納されるため，最後の要素が貸し出し中でなければ貸し出し可能です
          //  History lastHistory = histories.get(histories.size() - 1);
         //  if(this.isLent(lastHistory.getReturnDate())){  // 最後の貸出記録を調べてる isLent(Date returnDate)は 貸出中ならtrueを返す
    	   Object[] obj = historiesObjList.get(0);
    	   if(obj[2] != null) {
    	   return false;  // isLent()かtrueを返したので、貸出中だから falseを返す 貸出不可です
           }
       }
       return true;  // 貸出可能です
   }

    
    
}
