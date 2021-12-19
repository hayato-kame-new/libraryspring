package com.kame.springboot.form;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

// Formクラスに、相関チェック用のアノテーションをつけておきます 
// 相関チェックに関しては、各フィールドにはつけないでクラスだけにつけます
// フォームのクラス
public class MemberForm {
	
	
	// idも必要 @NotNullつけない hiddenで使うので入力チェックのアノテーションはいらない
	private int id;
	
	@NotEmpty( message = "名前を入力してください") //	@NotEmpty だと、 半角空白文字でも、OKになってしまうので、半角空白もバリデーションでエラーにするには @NotBlank にすること
	@Size(max = 100, message = "名前は100文字以内で入力してください") //   minは書かないこと エラーメッセージ2つ出さないようにするため
	private String name;  
	// membersテーブルでは nameカラムにUNIQUEはつけません usersテーブルのユーザ名はユニークだけど このmembersテーブルのユーザ名は同姓同名を許してるので

//	@NotEmpty だと、 半角空白文字でも、OKになってしまうので、半角空白もバリデーションでエラーにするには @NotBlank にすること
	@NotEmpty( message = "電話番号を入力してください")
	// @Pattern(regexp = "0\\d{1,4}-\\d{1,4}-\\d{4}", message = "電話番号の形式で入力してください")
	@Pattern(regexp = "^\\d{2,4}-\\d{2,4}-\\d{4}$", message = "電話番号の形式で入力してください")
	@Column( name = "tel")
	private String tel;
	
//	@NotEmpty だと、 半角空白文字でも、OKになってしまうので、半角空白もバリデーションでエラーにするには @NotBlank にすること
	@NotEmpty( message = "住所を入力してください")
	@Size(max = 100, message = "住所は100文字以内で入力してください")  // minは書かないこと エラーメッセージ2つ出さないようにするため
	private String address;
	
	
	// フォームでは 生年月日を追加を 年 月 日 で 各フィールドにする
	@NotNull(message = "年を選択してください") //@NotNullを付与することで、nullのみ許容しなくなる intや Dateなどに使う
	private Integer year;  // 年 selectタグからの送信 name属性の値が year  範囲は現在の年を計算した範囲にする

	
	@NotNull(message = "月を選択してください")  //@NotNullを付与することで、nullのみ許容しなくなる intや Dateなどに使う
	@Range(min= 1 , max= 12 )  // @Rangeは数値の値で、最小値と最大値を指定するものです。@Min、@Maxをまとめて設定するもの
	private Integer month;   // 月 selectタグからの送信 name属性の値が month

	
	@NotNull(message = "日を選択してください")
	@Range(min= 1 , max= 31 )  // @Rangeは数値の値で、最小値と最大値を指定するものです。@Min、@Maxをまとめて設定するもの
	private Integer day;  // 日 selectタグからの送信 name属性の値が day

	
	// 誕生日を表示する
	public String birthdayPrint() {
		return this.getYear().toString() + "年" + this.getMonth().toString() + "月" + this.getDay().toString() + "日";
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


	public Integer getYear() {
		return year;
	}


	public void setYear(Integer year) {
		this.year = year;
	}


	public Integer getMonth() {
		return month;
	}


	public void setMonth(Integer month) {
		this.month = month;
	}


	public Integer getDay() {
		return day;
	}


	public void setDay(Integer day) {
		this.day = day;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	

}
