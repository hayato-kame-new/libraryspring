package com.kame.springboot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 実際に図書館で本を借りる人が このMemberクラスのインスタンスです
 * 図書館アプリケーションにログインして アプリケーションを利用するのはUserDetailsImplクラスです(中でUserクラスを利用してる)
 * 
 * @author skame
 *
 */
@Entity
@Table(name = "members")  // 小文字で
public class Member {
	
	// 後で、フォームの入力チェックのバリデーションも追加すること
	
	/*
	 * membersテーブルは  historiesテーブルの親テーブルになる historiesテーブルとリレーションがある
	 * membersテーブルは usersテーブルとはリレーションの関係はない 
	 */
	
	@Id  // 主キー
	@Column( name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)  // GenerationType.AUTO  こっちじゃない これだとうまくいきません
	private int id;
	
	@NotEmpty( message = "名前を入力してください")
	@Size(max = 100, message = "名前は100文字以内で入力してください")
	@Column( name = "name")
	private String name;  // このnameカラムを membersテーブルでは ユニークにしてるので、バリデーションにユニークを作ってカスタムアノテーションをつけること
	// membersテーブルでは nameカラムにUNIQUEをつけてるので、このカラムに入力チェックのバリデーションで ユニークを作ってつけてください
	// もしフォームクラスを作ったら、そちらにバリデーションのアノテーションをつける事になる
	
	@NotEmpty( message = "電話番号を入力してください")
	// @Pattern(regexp = "0\\d{1,4}-\\d{1,4}-\\d{4}", message = "電話番号の形式で入力してください")
	@Pattern(regexp = "^\\d{2,4}-\\d{2,4}-\\d{4}$", message = "電話番号の形式で入力してください")
	@Column( name = "tel")
	private String tel;
	
	
	@NotEmpty( message = "住所を入力してください")
	@Size(max = 100, message = "住所は100文字以内で入力してください")
	@Column( name = "address")
	private String address;

	
	// リレーションを Historyとつけること
	
	
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
	 */
	public Member(String name, String tel, String address) {
		super();
		this.name = name;
		this.tel = tel;
		this.address = address;
	}
	
	/**
	 * 引数4つのコンストラクタ
	 * @param id
	 * @param name
	 * @param tel
	 * @param address
	 */
	public Member(int id,
			@NotEmpty(message = "名前を入力してください") @Size(max = 100, message = "名前は100文字以内で入力してください") String name,
			@NotEmpty(message = "電話番号を入力してください") @Pattern(regexp = "^\\d{2,4}-\\d{2,4}-\\d{4}$", message = "電話番号の形式で入力してください") String tel,
			@NotEmpty(message = "住所を入力してください") @Size(max = 100, message = "住所は100文字以内で入力してください") String address) {
		super();
		this.id = id;
		this.name = name;
		this.tel = tel;
		this.address = address;
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
	

}
