package com.kame.springboot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
	
	@Column( name = "name")
	private String name;  // このnameカラムを membersテーブルでは ユニークにしてるので、バリデーションにユニークを作ってカスタムアノテーションをつけること
	// membersテーブルでは nameカラムにUNIQUEをつけてるので、このカラムに入力チェックのバリデーションで ユニークを作ってつけてください
	// もしフォームクラスを作ったら、そちらにバリデーションのアノテーションをつける事になる
	
	
	@Column( name = "tel")
	private String tel;
	
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
