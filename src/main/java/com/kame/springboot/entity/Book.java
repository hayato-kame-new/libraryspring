package com.kame.springboot.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity  // エンティティのクラスです
@Table(name = "books")  // 小文字で
public class Book {  // Bookの方が 主エンティティ   Historyエンティティが 従エンティティ
	
	// フォームのクラスを作らないので バリデーションのアノテーションもつける 
	/*
	 * booksテーブルは  historiesテーブルの親テーブルになる historiesテーブルとリレーションがある
	 * booksテーブルは membersテーブルとはリレーションの関係はない 
	 */
	
	//主キーの idカラムには@Idを必ずつける 無いとエラー
	@Id  // 主キー
	@Column( name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)  // GenerationType.AUTO  こっちじゃない これだとうまくいきません
	private int id;  // 主キー 自動採番  リレーションあり 他のテーブルから参照されています  こっちbooksテーブルが主テーブルです
	
	
	// 追加 ISBN は 13桁です
	@NotEmpty(message="ISBNを入力してください")
	 // @Size(min = 13, max = 13, message = "ISBNは13文字で入力してください")  // isbn VARCHAR(40) NOT NULL UNIQUE,  日本語の漢字なら 39文字までOK
	 @Pattern(regexp = "^[0-9]{13}$", message = "ISBNは13桁の半角数字で入力してください")
	@Column(name = "isbn")  // ユニーク 世界でただ一つ
	private String isbn;  // 2007年以降の新刊のコードから13ケタになっています。10桁 から 13桁に変更している
	// booksテーブルでは isbnカラムにUNIQUEをつけてるので、このカラムに入力チェックのバリデーションで ユニークを作ってつけてください
	// もしフォームクラスを作ったら、そちらにバリデーションのアノテーションをつける事になる
	
	
	// 追加
	@NotEmpty(message="ジャンルを入力してください")
	@Size(min = 1 ,max = 20, message = "ジャンルは20文字以内で入力してください")
	@Column(name = "genre") 
	private String genre;
	
	@NotEmpty(message="タイトルを入力してください")
	@Size(min = 1, max = 100, message = "タイトルは100文字以内で入力してください")
	@Column( name = "title")
	private String title;
	
	@NotEmpty(message="著者を入力してください")
	@Size(min = 1, max = 100, message = "著者は100文字以内で入力してください")
	@Column( name = "authors")
	private String authors;
	
	@NotEmpty(message="出版社を入力してください")
	@Size(min = 1, max = 100, message = "出版社は100文字以内で入力してください")
	@Column( name = "publisher")
	private String publisher;
	
	// @Max(9999) @Min(1000) のエラーメッセージが英語で出るので messages.propertiesに書くこと
	@Max(9999)  // typeMismatch.publishYear=発行年を半角数字 西暦4桁で入力してください   
	@Min(1000)  // typeMismatch.publishYear=発行年を半角数字 西暦4桁で入力してください
	@NotNull(message="発行年を半角数字 発行年を西暦で入力してください")
	@Column( name = "publishyear")  // PostgreSQLなので booksテーブルのカラムの名前は 全て小文字の publishyear
	private Integer publishYear;

	// リレーションをHistoryエンティティクラス とつけること
	// ある１冊の特定の本は 沢山の履歴をもつ
	// mappedByに指定する値は「対応する(＠ManyToOneがついた)フィールド変数名」になります。
	@OneToMany(mappedBy = "bookId", cascade = CascadeType.ALL)
	List<History> histories;  // 複数形  List<エンティティ名> の変数名が複数系
	
	
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
