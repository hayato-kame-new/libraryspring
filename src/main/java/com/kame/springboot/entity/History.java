package com.kame.springboot.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity  // エンティティのクラスです 処理のメソッドは書かないLibraryクラスに書く リポジトリを組み込んだサービスをフィールドとしておかないこと
@Table(name = "histories")  // テーブル名全て小文字で
// @DayCheck(hireDateProperty="hireDate", retirementDateProperty="retirementDate", message = "退社日は、入社日の後の日付にしてください")  // 相関チェック
public class History {  // 子テーブルの方です ある本に対する貸出履歴オブジェクトを作成するためのクラス

	// このHistoryクラスから リレーションのついてるServiceを呼び出したら、BookServiceは、BookRepositoryを
	// 組み込んでいるため、エラーになりますので、BookServiceをフィールドとして組み込まないでください！！
	//	@Autowired
	//	BookService bookService;  // これをするとエラーで起動できなくなる
// 上のように、他のリポジトリを組み込んだらエラーになる！！エンティティクラスにはロジックは書かないこと ロジッククラスに書く
	
	// フォームのクラスLendingFormを作リマスので  ここのクラスに、入力チェックのバリデーションのアノテーションはつけません
	
	
	//主キーの idカラムには@Idを必ずつける 無いとエラー @Entityをつけたら @Idをつけないと起動しないので注意する
	@Id
	@Column( name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)  //  GenerationType.AUTO  こっちじゃない これだとうまくいきません
	private int id;
	
	
	// 日付は後で、相関チェックのバリデーションのアノテーションを作成すること  
	// 相関チェックのアノテーションは 各フィールドにはつけずに
	// クラスの宣言の上にアノテーションをつけます
	
	// iso = ISO.DATE だと 最も一般的な ISO 日付形式 yyyy-MM-dd  たとえば、"2000-10-31"  
	// fallbackPatterns に設定したものは、エラーにしないで、受け取ってくれる
	// @DateTimeFormat(iso = ISO.DATE, fallbackPatterns = { "yyyy/MM/dd", "yyyy-MM-dd" })
	// @NotNull( message = "貸し出し日を入力してください") // 日付には、@NotNullを使う
	// フォームのクラスLendingFormを作リマスので  ここのクラスに、入力チェックのバリデーションのアノテーションはつけません
	@Column( name = "lenddate") //  historiesテーブルの カラム名は全て小文字で
	private Date lendDate;  // 貸し出しをした日  java.util.Date  
	
	
	
	/// iso = ISO.DATE だと 最も一般的な ISO 日付形式 yyyy-MM-dd  たとえば、"2000-10-31"  
	// fallbackPatterns に設定したものは、エラーにしないで、受け取ってくれる
	// @DateTimeFormat(pattern = "yyyy-MM-dd")  // これだけだと、他の形式だとエラーになるので
	//@DateTimeFormat(iso = ISO.DATE, fallbackPatterns = { "yyyy/MM/dd", "yyyy-MM-dd" })
	// フォームのクラスLendingFormを作リマスので  ここのクラスに、入力チェックのバリデーションのアノテーションはつけません
	@Column( name = "returndate") //  historiesテーブルの カラム名は全て小文字で  nullを許容する
	private Date returnDate;  // 返却した日
		
//	private Book book;  // これじゃなくて 参照するフィールドを持たせる
//	private User user;  // これじゃなくて 参照するフィールドを持たせる
	
	 // リレーションのあるフィールド
	
	// フォームのクラスLendingFormを作リマスので  ここのクラスに、入力チェックのバリデーションのアノテーションはつけません
	@Column( name = "bookid") //  historiesテーブルの カラム名は全て小文字で
	private int bookId;  // 貸し出し本booksテーブルの の idを参照してる  リレーションのあるフィールド
	
	// リレーションのあるフィールド
	  
	// フォームのクラスLendingFormを作リマスので  ここのクラスに、入力チェックのバリデーションのアノテーションはつけません
	@Column( name = "memberid")  // historiesテーブルの カラム名は全て小文字で
	private int memberId;  //  会員membersテーブルのの idを参照してる  リレーションのあるフィールド 
	
	
	// リレーションつけること  テーブル同志のリレーション  エンティティとして@Entityをつけたクラス同士で対応する記述をする 
	@ManyToOne  // 他はごちゃごちゃ要らない @ManyToOneにはMappedBy属性が存在しない
	Book book;   // @ManyToOneなので 単数形の Book book; にする １冊の本はたくさんの履歴をもつが  履歴はある特定１冊に対しての履歴（１つしか持たない)

	@ManyToOne
	Member member;  // 単数形 一人の会員は たくさんの履歴をもつが 履歴はある特定の一人の会員の履歴
	
	
	/**
	 * 引数なしのコンストラクタ
	 */
	public History() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	/**
	 * 貸し出し日を本日とした 履歴インスタンスを生成するコンストラクタ 
	 * 引数は 貸し出しする本のid と 借りる会員の id が必要
	 * @param bookId
	 * @param memberId
	 */
	public History( int bookId, int memberId) {
		super();
		this.lendDate = new java.util.Date();  // 本日の日付を入れます
		this.bookId = bookId;
		this.memberId = memberId;
	}
	

	// アクセッサ
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getLendDate() {
		return lendDate;
	}

	public void setLendDate(Date lendDate) {
		this.lendDate = lendDate;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	
//	  /**
//     * 貸し出し中であるかを判定するメソッド 異なるパッケージから見えるようにするため publicにする
//     * 貸し出し中であれば，true， 貸し出し中でなければfalseを返す
//     * 貸し出し中である，ということは  まだ返却されていない  ということ
//     * returnDateにまだ値が代入されていないことを表します
//     * 貸し出された時に初めてHistory型のオブジェクトが生成されるこの時には returnDate は 参照型の規定値(デフォルト値) のnull になってます
//     * また，貸し出された時に初めてHistory型のオブジェクトが生成されるために， このオブジェクトについては，貸し出し前の状態を考える必要はありません
//     * @return true:貸出中 <br /> false:貸出中ではない
//     */
//    public Boolean isLent() {
//        if(this.returnDate == null) {  // 貸出中である
//            return true;  // 貸出中なら tureを返す
//        }
//        return false;  // 貸し出し中では無い falseを返す
//    }
//
//    /**
//     * 貸し出し中なのか調べて 文字列で表示する
//     * @return String
//     */
//    public String lentStr() {
//        String str = "";
//        if(this.isLent()) {
//            str = "貸し出し中";
//        } else {
//            str = "配架中";
//        }
//        return str;
//    }
//
//    /**
//     * 履歴の情報を画面に出力する
//     * @return String
//     */
//    public String print() {
//    	String  str1 = "タイトル: " + this.book.getTitle() + ", 著者: " + this.book.getAuthors() + ", 出版社: " 
//    			+ this.book.getPublisher() + ", 発行年: " +  this.book.getPublishYear()+ ", 書架状況: " +  this.lentStr();
//    	String separator = System.lineSeparator();  // システムに依存する改行になる
//    	String str2 = ", 会員ID: " + this.member.getId()+ ", 会員名: " +  this.member.getName();
//    	String str3 = this.lendDate.toString() + " ~ ";
//    	String str4 = "";
//    	if(this.returnDate != null) {
//    		str4 = this.lendDate.toString();
//    	}
//    	return str1 + separator + str2 + str3 + str4;
//    }
//    
//    
//    /**
//     * 貸し出しができるか判定する 
//     *
//     * @param history   これから貸し出そうとしている History型の実体
//     * @param histories  これから貸し出そうとする本に関する これまでの貸し出し履歴であるHistory のリスト
//     * @return true:貸出可能 <br /> false:貸出不可
//     */
//   Boolean canLend(History history, List<History> histories){
//       // まず、これから貸し出そうとしている本が，この図書館システムが管理している本のリストshelf に存在するかを判定
//       // containsメソッドで コレクション変数shelfの中に 引数に渡された本が含まれているかを判定する
//       //  ! で 判定を逆にしてるから 含まれていなければ falseを返す
//	//   List<Book> books = bookService.booksList();  // この図書館システムが保管する全ての本のリスト
////       if(!books.contains(history.getBook())){
////           return false;  // これから貸し出そうとする本は、図書館システムには無い本なので 貸出できません
////           // 貸出できないので、returnで即メソッドを終了させて、呼び出し元に引数の falseを返します。
////           // このメソッドはreturnしたので、この行以降は実行されない
////       }
//
//       // これから貸し出そうとする本は、この図書館システムに有るので、貸出できます。(貸出中じゃなければ)
//       // 次に、貸し出し中かどうか調べます
//       // その本の貸出履歴が存在しない場合 つまり histories.size() == 0 は，今まで借りられたことがないため，貸し出し可能
//       // その本の貸出履歴があったら histories.size() > 0 だったら、貸出可能かどうかを調べる
//       if(histories.size() > 0){  // その本に関する今までの貸出記録があれば
//           // その本に関する最後の貸出記録の実態を取得する
//           // List<History> は、 右辺が new ArrayList<Hirstory>(); で初期化されてるため、ArrayListは、中身は配列と同じ構造なので、
//           //  履歴は順に格納されるため，最後の要素が貸し出し中でなければ貸し出し可能です
//           History lastHistory = histories.get(histories.size() - 1);
//           if(lastHistory.isLent()){  // 最後の貸出記録を調べてる isLent()は 貸出中ならtrueを返す
//               return false;  // isLent()かtrueを返したので、貸出中だから falseを返す 貸出不可です
//           }
//       }
//       return true;  // 貸出可能です
//   }

}
