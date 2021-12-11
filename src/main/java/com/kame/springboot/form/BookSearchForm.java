package com.kame.springboot.form;

import javax.validation.constraints.Size;

//Formクラスに、相関チェック用のアノテーションをつけておきます 
//相関チェックに関しては、各フィールドにはつけないでクラスだけにつけます
//フォームのクラス
public class BookSearchForm {
	
	// 曖昧検索 で 指定した条件で AND検索を行うためのフォーム フィールドに何も入力しなければ 条件を指定していないことになる
	// ただし、5つとも全て何も条件を入れない場合は、何もせずに、コントローラーでは、リダイレクトをして、また検索画面へ戻るようになってる

	@Size(max=13 , message = "isbnは13文字以内で入力してください")
	private String isbn;  //曖昧検索する  入力しなくてもいいので @NotEmpty などはつけない
	
	private String genre;  // 曖昧検索する 入力しなくてもいいので @NotEmpty などはつけない セレクトボックス使用 "選択しない" 欄もある
	
	@Size(max = 100, message = "タイトルに含まれる文字は100文字以内で入力してください")
	private String title;   // 曖昧検索する 入力しなくてもいいので @NotEmpty などはつけない
	
	@Size(max = 100, message = "著者に含まれる文字は100文字以内で入力してください")
	private String authors;  // 曖昧検索する 入力しなくてもいいので @NotEmpty などはつけない
	
	@Size( max = 100, message = "出版社に含まれる文字は100文字以内で入力してください") // 曖昧検索する 入力しなくてもいいので @NotEmpty などはつけない
	private String publisher;

	// アクセッサ	
	public String getIsbn() {
		return isbn;
	}
	
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	public String getGenre() {
		return genre;
	}
	
	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
}
