package com.kame.springboot.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity  // エンティティのクラスです
@Table(name = "histories")  // テーブル名全て小文字で
public class History {  // 子テーブルの方です ある本に対する貸出履歴オブジェクトを作成するためのクラス
	
	// フォームのクラスを作らないなら ここのクラスに、バリデーションのアノテーションもつける 
	
	//主キーの idカラムには@Idを必ずつける 無いとエラー @Entityをつけたら @Idをつけないと起動しないので注意する
	@Id
	@Column( name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)  //  GenerationType.AUTO  こっちじゃない これだとうまくいきません
	private int id;
	
	@Column( name = "lenddate") //  historiesテーブルの カラム名は全て小文字で
	private Date lendDate;  // 貸し出しをした日
	
	@Column( name = "returndate") //  historiesテーブルの カラム名は全て小文字で
	private Date returnDate;  // 返却した日
		
//	private Book book;  // これじゃなくて
//	private User user;  // これじゃなくて
	
	 // リレーションのあるフィールド
	@Column( name = "bookid") //  historiesテーブルの カラム名は全て小文字で
	private int bookId;  // 貸し出し本booksテーブルの の idを参照してる  リレーションのあるフィールド
	
	// リレーションのあるフィールド
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
     * 貸し出し中であるかを判定するメソッド 異なるパッケージから見えるようにするため publicにする
     * 貸し出し中であれば，true， 貸し出し中でなければfalseを返す
     * 貸し出し中である，ということは  まだ返却されていない  ということ
     * returnDateにまだ値が代入されていないことを表します
     * 貸し出された時に初めてHistory型のオブジェクトが生成されるこの時には returnDate は 参照型の規定値(デフォルト値) のnull になってます
     * @return true:貸出中 <br /> false:貸出中ではない
     */
    public Boolean isLent() {
        if(this.returnDate == null) {  // 貸出中である
            return true;  // 貸出中なら tureを返す
        }
        return false;  // 貸し出し中では無い falseを返す
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
	
	

}
