package com.kame.springboot.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity  // エンティティのクラスです
@Table(name = "histories")  // テーブル名全て小文字で
public class History {
	
	// フォームのクラスを作らないなら 後で、バリデーションのアノテーションもつける 
	
	//主キーの idカラムには@Idを必ずつける 無いとエラー
	@Id
	@Column( name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)  //  GenerationType.AUTO  こっちじゃない これだとうまくいきません
	private int id;
	
	@Column( name = "lenddate") //  historiesテーブルの カラム名は全て小文字で
	private Date lendDate;  // 貸し出しをした日
	
	@Column( name = "returndate") //  historiesテーブルの カラム名は全て小文字で
	private Date returnDate;  // 返却した日
	
	
//	private Book book;
//	private User user;
	 // リレーション
	@Column( name = "bookid") //  historiesテーブルの カラム名は全て小文字で
	private int bookId;  // 貸し出し本 の id
	
	
	@Column( name = "memberid")  // historiesテーブルの カラム名は全て小文字で
	private int memberId;  //  会員の id
	
	// リレーションつけること
	

	/**
	 * 引数なしのコンストラクタ
	 */
	public History() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}
	

}
