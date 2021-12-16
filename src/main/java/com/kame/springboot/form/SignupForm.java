package com.kame.springboot.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// フォームのクラスなので@Entityはつけません このクラスに入力チェックのアノテーションをつけます 
// email gender age tel を追加する
public class SignupForm {
	
	// ここでは、＠NotBlank を使用しています
	// ＠NotNull 又は ＠NotEmpty を使用した場合、半角スペースのみでもユーザー名として登録ができてしまいますが
	// この半角スペースのユーザー名ではログインすることができないためです
	// なお、Spring MVC では「文字列の入力フィールドに未入力の状態でフォームを送信した場合、デフォルトではフォームオブジェクトにnullではなく、空文字がバインドされる
	// 入力チェック用のアノテーションだけつける
	@NotBlank(message = "ユーザ名を入力してください")  // ＠NotBlankにしてください  Null、空文字、空白をエラーとする
	@Size(min = 1, max = 50, message = "ユーザー名は1文字以上50文字以下で入力してください")
	private String username;   // usersテーブルの nameカラム
	
	@NotBlank(message = "パスワードを入力してください")  //  ＠NotBlankにしてください  Null、空文字、空白をエラーとする
    @Size(min = 6, max = 20, message = "パスワードは6文字以上20文字以下で入力してください")
	private String password;

	// usersテーブルのカラムをemail gender age tel の４つ追加したので フォームも追加する
	@NotBlank(message = "メールアドレスを入力してください")   // これにしてください
	@Email(message = "メールアドレスの形式で入力してください")  // javax.validation.constraints.Email
	private String email;
	
	@NotNull(message = "性別を選択してください") // Integerは @NotNull 
	@Min(value=1, message = "性別を選択してください")
	@Max(value=2, message = "性別を選択してください")
	private Integer gender; // 性別 1: 男  2: 女    性別は Integerで管理する
	
	// @PositiveOrZero  // これでもいい
	@NotNull(message = "年齢を入力してください") // Integerは @NotNull  
	@Min(0)
	@Max(110)
	private Integer age;
	
	@NotBlank(message = "電話番号を入力してください")  // 文字列は @NotBlankにしてください
	@Pattern(regexp = "0\\d{1,4}-\\d{1,4}-\\d{4}", message = "電話番号の形式で入力してください")
	private String tel;
	
	
	// アクセッサ
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
	

}
