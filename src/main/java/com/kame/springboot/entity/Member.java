package com.kame.springboot.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 実際に図書館で本を借りる人が このMemberクラスのインスタンスです
 * 図書館アプリケーションにログインして アプリケーションを利用するのはUserDetailsImplクラスです(中でUserクラスを利用してる)
 * 
 * @author skame
 *
 */
@Entity  //// エンティティのクラスです 処理のメソッドは書かないLibraryクラスに書く リポジトリを組み込んだサービスをフィールドとしておかないこと
@Table(name = "members")  // 小文字で
public class Member {  // Memberの方が 主エンティティ   Historyエンティティが 従エンティティ
	
	
	// 別にフォームのクラスを作りましたのでこのクラスには入力チェックの バリデーションのアノテーションをつけない
	// このエンティティクラスには データベースとの関係のアノテーションだけつけてください
	
	/*
	 * membersテーブルは  historiesテーブルの親テーブルになる historiesテーブルとリレーションがある
	 * membersテーブルは usersテーブルとはリレーションの関係はない 全く関係ない
	 */
	
	@Id  // 主キー
	@Column( name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)  // GenerationType.AUTO  こっちじゃない これだとうまくいきません
	private int id;  // 参照されてるフィールド  リレーションあり Historyエンティティの memberIdフィールドとリレーションあり
	
	// フォームクラスがあるので、入力のバリデーションのアノテーションはフォームクラスの方でつけます
//	@NotEmpty( message = "名前を入力してください")
//	@Size(max = 100, message = "名前は100文字以内で入力してください") //   minは書かないこと エラーメッセージ2つ出さないようにするため
	@Column( name = "name")  // ユニーク制約はつけません
	private String name;  // このnameカラムを membersテーブルでは ユニークにしてるので、バリデーションにユニークを作ってカスタムアノテーションをつけること
	// membersテーブルでは nameカラムにUNIQUEはつけません usersテーブルのユーザ名はユニークだけど このmembersテーブルのユーザ名は同姓同名を許してるので
	// もしフォームクラスを作ったら、そちらにバリデーションのアノテーションをつける事になる
	
	// フォームクラスがあるので、入力のバリデーションのアノテーションはフォームクラスの方でつけます
//	@NotEmpty( message = "電話番号を入力してください")
//	@Pattern(regexp = "^\\d{2,4}-\\d{2,4}-\\d{4}$", message = "電話番号の形式で入力してください")
	@Column( name = "tel")
	private String tel;
	
	// フォームクラスがあるので、入力のバリデーションのアノテーションはフォームクラスの方でつけます
//	@NotEmpty( message = "住所を入力してください")
//	@Size(max = 100, message = "住所は100文字以内で入力してください")  // minは書かないこと エラーメッセージ2つ出さないようにするため
	@Column( name = "address")
	private String address;
	
	
	
	// 生年月日を追加
	@Column( name = "birthday")
	private LocalDate birthDay;

	
	// リレーションをHistoryエンティティクラス とつけること
	// ある１人の特定の会員は 沢山の履歴をもつ
	// mappedByに指定する値は「対応する(＠ManyToOneがついた)フィールド変数名」になります。
	@OneToMany(mappedBy = "memberId", cascade = CascadeType.ALL)
	List<History> histories;  // 複数形  List<エンティティ名> の変数名が複数系
	
	
	/**
	 * 引数なしコンストラクタ
	 */
	public Member() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * id以外の引数をとるコンストラクタ
	 * @param name
	 * @param tel
	 * @param address
	 * @param birthDay
	 */	
	public Member(String name, String tel, String address, LocalDate birthDay) {
		super();
		this.name = name;
		this.tel = tel;
		this.address = address;
		this.birthDay = birthDay;
	}
	
	/**
	 * 引数5つのコンストラクタ
	 * @param id
	 * @param name
	 * @param tel
	 * @param address
	 * @param birthDay
	 */
	public Member(int id, String name, String tel, String address, LocalDate birthDay) {
		super();
		this.id = id;
		this.name = name;
		this.tel = tel;
		this.address = address;
		this.birthDay = birthDay;
	}
	
	
	/**
	 * 生年月日を表示する
	 * @return String
	 */
	public String printBirthDay() {
		 return String.valueOf(this.birthDay.getYear()) + "年" + String.valueOf(this.birthDay.getMonthValue()) + "月" + String.valueOf(this.birthDay.getDayOfMonth()) + "日";
	}

	// アクセッサ
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LocalDate getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(LocalDate birthDay) {
		this.birthDay = birthDay;
	}
	

}
