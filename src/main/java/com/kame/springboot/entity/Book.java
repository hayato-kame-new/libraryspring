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

@Entity  //// エンティティのクラスです 処理のメソッドは書かないLibraryクラスに書く リポジトリを組み込んだサービスをフィールドとしておかないこと
@Table(name = "books",  schema = "public")  // テーブル名は小文字で
public class Book {  // Bookの方が 主エンティティ   Historyエンティティが 従エンティティ
	// エンティティクラスに書くのは テーブルのからむの対応するフィールドだけ あとはコンストラクタとアクセッサだけ
	// フォームのクラスを作らないので バリデーションのアノテーションもつける 
	// ＠NotBlank を使用しています
		// ＠NotNull 又は ＠NotEmpty を使用した場合、半角スペースのみでもユーザー名として登録ができてしまいますが、この半角スペースのユーザー名ではログインすることができないためです
	// 	@NotBlank  // javax.validation.constraints.NotBlank            Null、空文字、空白をエラーとする
	/*
	 * booksテーブルは  historiesテーブルの親テーブルになる historiesテーブルとリレーションがある
	 * booksテーブルは membersテーブルとはリレーションの関係はない 
	 */
	
	//主キーの idカラムには@Idを必ずつける 無いとエラー
	@Id  // 主キー
	@Column( name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)  // GenerationType.AUTO  こっちじゃない これだとうまくいきません
	private int id;  // 主キー 自動採番  リレーションあり 他のテーブルから参照されています  こっちbooksテーブルが主テーブルです
	
	
	
	// アノテーションを作る
	
	// 追加 ISBN は 13桁です ユニークにはしません なぜなら、図書館システムが同じISBNの本を複数持つこともあるから
	@NotEmpty(message="ISBNを入力してください")
	 // @Size(min = 13, max = 13, message = "ISBNは13文字で入力してください")  // isbn VARCHAR(40) NOT NULL UNIQUE,  日本語の漢字なら 39文字までOK
	 @Pattern(regexp = "^[0-9]{13}$", message = "ISBNは13桁の半角数字で入力してください")
	@Column(name = "isbn")  // ユニークではありません  
	private String isbn;  // 2007年以降の新刊のコードから13ケタになっています。10桁 から 13桁に変更している
	// booksテーブルでは isbnカラムにUNIQUEをつけてるので、isbn VARCHAR(40) NOT NULL UNIQUE このカラムに入力チェックのバリデーションで ユニークを作ってつける
	// もしフォームクラスを作ったら、そちらにバリデーションのアノテーションをつける事になる
	
	
	// 追加
	@NotEmpty(message="ジャンルを選択してください")
	// @Size(max = 20, message = "ジャンルは20文字以内で入力してください") //  minは書かないこと エラーメッセージ2つ出さないようにするため 今回はセレクトボックスなのでいらない
	@Column(name = "genre") 
	private String genre;
	
	@NotEmpty(message="タイトルを入力してください")
	@Size(max = 100, message = "タイトルは100文字以内で入力してください") //  minは書かないこと エラーメッセージ2つ出さないようにするため
	@Column( name = "title")
	private String title;
	
	@NotEmpty(message="著者を入力してください")
	@Size( max = 100, message = "著者は100文字以内で入力してください") // minは書かないこと エラーメッセージ2つ出さないようにするため
	@Column( name = "authors")
	private String authors;
	
	@NotEmpty(message="出版社を入力してください")
	@Size( max = 100, message = "出版社は100文字以内で入力してください") // minは書かないこと エラーメッセージ2つ出さないようにするため
	@Column( name = "publisher")
	private String publisher;
	
	// @Max(9999) @Min(1000) のエラーメッセージが英語で出るので messages.propertiesに書くこと
	@Max(9999)  // typeMismatch.publishYear=発行年を半角数字 西暦4桁で入力してください   
	@Min(1000)  // typeMismatch.publishYear=発行年を半角数字 西暦4桁で入力してください
	@NotNull(message="発行年を半角数字 西暦で入力してください")
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
	
	/**
	 * equalsオーバーライドする
	 * リストのcontainsメソッドの名では equalsメソッドが使われているので、
	 * equalsメソッドをオーバーライドしないとBookオブジェクトが、同じだと判断できないので
	 */
	@Override
	public boolean equals(Object other) {
		// 引数で渡されたオブジェクトが、このオブジェクト自身であった場合 true
		if (this == other) {
			return true;
		}
		// 引数で渡されたオブジェクトが、Bookクラスのオブジェクトではない場合 false
		if (!(other instanceof Book)) {
			return false;
		}
		//フィールドを をそれぞれ比較し、等しければ true, 等しくなければ false
		Book otherBook = (Book) other;
		if (this.getId() == otherBook.getId()) {
			return true;
		}
		if(this.getIsbn().equals(otherBook.getIsbn())) {
			return true;
		}
		if(this.getGenre().equals(otherBook.getGenre())) {
			return true;
		}
		if(this.getTitle().equals(otherBook.getTitle())) {
			return true;
		}
		if(this.getAuthors().equals(otherBook.getAuthors())) {
			return true;
		}
		if(this.getPublisher().equals(otherBook.getPublisher())) {
			return true;
		}
		if (this.getPublishYear() == otherBook.getPublishYear()) {
			return true;
		}
		return false;
	}

	// equalsメソッドを実装した場合は、hashCodeメソッドも実装する必要があり
	// equalsメソッドの結果がtrueとなるオブジェクトは、hashCodeメソッド呼び出しの結果同じ値を返す必要がある。
	// (equalsがfalseとなるオブジェクトが同じhashCodeの結果を返すことは、かまわない)
	/**
	 * hashCode メソッドをオーバーライド
	 * equalsメソッドを実装した場合は、hashCodeメソッドも実装する必要があり
	 * equalsメソッドの結果がtrueとなるオブジェクトは、hashCodeメソッド呼び出しの結果同じ値を返す必要がある。
	 *  (equalsがfalseとなるオブジェクトが同じhashCodeの結果を返すことは、かまわない)
	 * インスタンスの同値性を確認できるメソッドが使えるようになりました
	 */
	@Override
	public int hashCode() {
		int result = 1; // 1でもいいです
		result = 31 * result + id;
		result = 31 * result + ((isbn == null) ? 0 : isbn.hashCode());
		result = 31 * result + ((genre == null) ? 0 : genre.hashCode());
		result = 31 * result + ((title == null) ? 0 : title.hashCode());
		result = 31 * result + ((authors == null) ? 0 : authors.hashCode());
		result = 31 * result + ((publisher == null) ? 0 : publisher.hashCode());
		result = 31 * result + publishYear;
		return result;
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
