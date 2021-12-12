package com.kame.springboot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// 他のクラスの内部でフィールドとして使うクラスです @Entityをつけて @Id をつけないと@OneToManyがつけられない
// 認証に使うusername,passwordはセッションから参照できる
// UserDetailsをimplementsしたクラスにgetterをつければいいだけ
// まず、そのUserDetailsをimplementsしたクラスにフィールドとして利用するUserクラスを作る
// このUserクラスを作ったら UserDetailsImplクラスのフィールドに private User user; と書いて、フィールドを書き直す

/**
 * この図書館アプリを利用するユーザのエンティティのクラス (実際に本を借りるのは Memberクラスなので注意)
 * このUserクラスは UserDetailsImplクラスの フィールドとして利用してます。
 * UserDetailsImplクラスのインスタンスが、図書館アプリケーションにログインして、操作をする人です 本を借りる人ではありません。
 * 実際に 本を借りるのは 会員 Memberインスタンスです
 * @author skame
 *
 */
@Entity  // つけてください  @Entityをつけて @Id をつけないと@OneToManyがつけられないしエラーになる 処理のメソッドは書かないLibraryクラスに書く
@Table(name = "users")
public class User {
	// エンティティのクラスです ロジックは書かないこと リポジトリを組み込んだサービスをフィールドとしておかないこと
	// フォームクラスSignupFormクラスも見てください！！ 入力チェックのアノテーションはそちらのフォームクラスにつけます
	@Id // 主キー @Entityをつけたら これがないと起動しません
	@Column(name = "id") // カラム名は、postgreSQL 全て小文字なので小文字にする リレーションのあるカラム
	private int id; // 主キーになるもの idもゲッターで取得できるようにしたい
	
	
	// SpringSecurityの認証済みユーザの情報にusername,password以外をもたせるて、getterで参照できるようにしたい
	 // カラム名は、postgreSQL 全て小文字なので小文字にする
    @Column(name = "name")  // 注意
    private String username; // usersテーブルのカラム名は name   usernameでいい userNameにしない 注意UserDetailsImplに合わせること
    // Springセキュリティのため Springが ユーザ名をユニークにしてる
    
    
    @Column(name = "password")
	private String password;  // ハッシュ化したパスワード
    
    // 後で、ゲッターをUserDetailsImpleに定義すれば、ゲッターでアクセスできるようになる
    @Column(name = "authority")
	private String authority;  // 必要 コントローラで ユーザ新規登録するときに registerメソッドの実引数 に "ROLE_USER" を入れてます
    
    //  SpringSecurityの認証済みユーザの情報にusername,password以外をもたせるて、getterで参照できるようにしたい
    // 追加で  gender   age   email  tel
    // SpringSecurityの認証済みユーザの情報に追加するカラム
    @Column(name = "gender")
    private Integer gender;  // 性別 1: 男  2: 女    性別は Integerで管理する
    
 // SpringSecurityの認証済みユーザの情報に追加するカラム
    @Column(name = "age")
    private Integer age;
    
    // SpringSecurityの認証済みユーザの情報に追加するカラム
	@Column(name = "email")
	private String email;
    
	// SpringSecurityの認証済みユーザの情報に追加するカラム
	@Column(name = "tel")
	private String tel; // SpringSecurityの認証済みユーザの情報にusername,password以外をもたせるて、getterで参照できるようにしたい
    
	// @Entityをつけて @Id をつけないと@OneToManyがつけられない


	// mappedByに指定する値は「対応する(＠ManyToOneがついた)フィールド変数名」になります。
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // 相手側の@ManyToOneが着いてるのがUser user; なので mappedBy = "user"
//	List<Schedule> Schedules;
    
    /**
     * 引数なしのコンストラクタも、明示的に作ること　　必要
     */
	public User() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}

	
	/**
	 * 引数ありコンストラクタ
	 * @param id
	 * @param username
	 * @param password
	
	 * @param gender
	 * @param age
	 * @param email
	 * @param tel
	 */
	public User(int id, String username, String password, Integer gender, Integer age, String email,
			String tel) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		 
		this.gender = gender;
		this.age = age;
		this.email = email;
		this.tel = tel;
	}

	 /**
     * 性別を文字で表すインスタンスメソッド
     * @return str
     */
    public String genderStr() {
        String str = "";
        if(this.gender == 1) {
            str = "男";
        }else if (this.gender == 2) {
            str = "女";
        }
        return str;
    }

	// アクセッサ
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public String getAuthority() {
		return authority;
	}


	public void setAuthority(String authority) {
		this.authority = authority;
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

	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
	
}
