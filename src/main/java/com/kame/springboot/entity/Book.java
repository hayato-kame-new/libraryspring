package com.kame.springboot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity  // エンティティのクラスです
@Table(name = "books")  // 小文字で
public class Book {
	
	// フォームのクラスを作らないなら 後で、バリデーションのアノテーションもつける 
	/*
	 * booksテーブルは  historiesテーブルの親テーブルになる historiesテーブルとリレーションがある
	 * booksテーブルは usersテーブルとはリレーションの関係はない 
	 */
	
	//主キーの idカラムには@Idを必ずつける 無いとエラー
	@Id  // 主キー
	@Column( name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)  // GenerationType.AUTO  こっちじゃない これだとうまくいきません
	private int id;  // 主キー 自動採番
	
	// 追加
	@NotEmpty(message="ISBNを入力してください")
	@Column(name = "isbn")  // ユニーク 世界でただ一つ
	private String isbn;  // 2007年以降の新刊のコードから13ケタになっています。10桁 から 13桁に変更している
	// booksテーブルでは isbnカラムにUNIQUEをつけてるので、このカラムに入力チェックのバリデーションで ユニークを作ってつけてください
	// もしフォームクラスを作ったら、そちらにバリデーションのアノテーションをつける事になる
	
	
	// 追加
	@NotEmpty(message="ジャンルを入力してください")
	@Column(name = "genre") 
	private String genre;
	
	@NotEmpty(message="タイトルを入力してください")
	@Column( name = "title")
	private String title;
	
	@NotEmpty(message="著者を入力してください")
	@Column( name = "authors")
	private String authors;
	
	@NotEmpty(message="出版社を入力してください")
	@Column( name = "publisher")
	private String publisher;
	
	@Max(2070)
	@Min(1900)
	@NotNull
	@Column( name = "publishyear")  // PostgreSQLなので booksテーブルのカラムの名前は 全て小文字の publishyear
	private Integer publishYear;

	// リレーションをHistoryとつけること
	
	/**
	 * 引数なしコンストラクタ
	 */
	public Book() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	
	/**
	 * 引数6つありコンストラクタ idは いらない PostgreSQLで id serial primary key なので
	 * idの主キー シーケンスとはINSERTで値を入れなくとも、自動で採番される列
	 * 使ってないかも
	 * @param isbn
	 * @param genre
	 * @param title
	 * @param authors
	 * @param publisher
	 * @param publishYear
	 */
	public Book(String isbn, String genre, String title, String authors, String publisher, Integer publishYear) {
		super();
		this.isbn = isbn;
		this.genre = genre;
		this.title = title;
		this.authors = authors;
		this.publisher = publisher;
		this.publishYear = publishYear;
	}
	
	
	/**
	 * 引数7つのコンストラクタ
	 * @param id
	 * @param isbn
	 * @param genre
	 * @param title
	 * @param authors
	 * @param publisher
	 * @param publishYear
	 */
	public Book(int id, @NotEmpty(message = "ISBNを入力してください") String isbn,
			@NotEmpty(message = "ジャンルを入力してください") String genre, @NotEmpty(message = "タイトルを入力してください") String title,
			@NotEmpty(message = "著者を入力してください") String authors, @NotEmpty(message = "出版社を入力してください") String publisher,
			@Max(2070) @Min(1900) @NotNull Integer publishYear) {
		super();
		this.id = id;
		this.isbn = isbn;
		this.genre = genre;
		this.title = title;
		this.authors = authors;
		this.publisher = publisher;
		this.publishYear = publishYear;
	}


	// アクセッサ
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

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

	public Integer getPublishYear() {
		return publishYear;
	}

	public void setPublishYear(Integer publishYear) {
		this.publishYear = publishYear;
	}
	
	
	

}
