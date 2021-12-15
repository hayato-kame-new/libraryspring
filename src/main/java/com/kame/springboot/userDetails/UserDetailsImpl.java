package com.kame.springboot.userDetails;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kame.springboot.entity.User;

/**
 * UserDetails インターフェイスの実装クラス
 * 
 * @author skame
 *
 */
public class UserDetailsImpl implements UserDetails {
	// メソッドなどを使う時には、userDetails.genderStr()  などと使います UserDetailsImpl.genderStr() では無い
	
	/*
	 * UserDetails インターフェイスには次のようなメソッドが用意されています 抽象メソッドが宣言されてる {}の本体は無い
	 * 
	 * Collection<? extends GrantedAuthority> getAuthorities();  // 権限リストを返す 抽象メソッドが宣言されてる {}の本体は無い
	 * データ型 <? extends GrantedAuthority> は   GrantedAuthority 型のすべてのサブクラスを表している
    String getPassword();  // パスワードを返す  抽象メソッドが宣言されてる {}の本体は無い
    String getUsername();  // ユーザー名を返す 抽象メソッドが宣言されてる {}の本体は無い
    boolean isAccountNonExpired();  // アカウントの有効期限の判定 抽象メソッドが宣言されてる {}の本体は無い
    boolean isAccountNonLocked();  // アカウントのロック状態の判定 抽象メソッドが宣言されてる {}の本体は無い
    boolean isCredentialsNonExpired();  // 資格情報の有効期限の判定 抽象メソッドが宣言されてる {}の本体は無い
    boolean isEnabled();  // 有効なユーザーであるかの判定 抽象メソッドが宣言されてる {}の本体は無い
	 * 
	 */

	// privateフィールド
	/**
	 * シリアル番号UID
	 */
	private static final long serialVersionUID = -6248351796526777243L;
	/*
	 usernameフィールド  passwordフィールドは、User user;をフィールに宣言したので、コメントアウトする
	 */
	
	// private String username;  // Userの方へ移動
	
	// private String password; // Userの方へ移動
	
	private Collection<GrantedAuthority> authorities;
	
	// エンティティクラスをフィールドに宣言して、手動でゲッターを追加すること userインスタンスの各ゲッターを使用して
	// 手動でゲッターを追加すること getPassword()  getUsername() 
	private User user;  // このエンティティのクラスをフィールドとして宣言したので、 username passwordフィールドをコメントアウトした
	
	
	/**
	 * 引数なしのコンストラクタ
	 */
	public UserDetailsImpl() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	
	/**
	 * 引数ありのコンストラクタ
	 * @param username
	 * @param password
	 * @param authorities
	 */
//	public UserDetailsImpl(String username, String password, Collection<GrantedAuthority> authorities) {
//		super();
//		this.username = username;
//		this.password = password;
//		this.authorities = authorities;
//	}

	/**
	 * 引数ありのコンストラクタ
	 * @param authorities
	 * @param user
	 */
	public UserDetailsImpl(Collection<GrantedAuthority> authorities, User user) {
		super();
		this.authorities = authorities;
		this.user = user;  // これに変更しました
	}
	
	 /**
     * 性別を文字で表すインスタンスメソッド
     * @return str
     */
    public String genderStr() {
        String str = "";
        if(this.getGender() == 1) {
            str = "男";
        }else if (this.getGender() == 2) {
            str = "女";
        }
        return str;
    }
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities; // コンストラクタで設定した値を返す
	}

	// 修正をする
    @Override
    public String getPassword() {   	
    	return user.getPassword();  // ここ修正
    }
    
    // 修正をする
    @Override
    public String getUsername() {   	
    	return user.getUsername();  // ここ修正
    	// // コントローラで System.out.println(userDetails.getUsername)  で、アクセスして取得できます
    }
    // ポイント！！
    // さらに手動でゲッターを追加すること これはスーパークラスに無いのでオーバーライドではない
    public Integer getId() {  	
    	return user.getId();  // ここ重要！！！手動で追加すること
    	// コントローラで System.out.println(userDetails.getId)  で、アクセスして取得できます
    }

    // ポイント！！
    // さらに手動でゲッターを追加すること これはスーパークラスに無いのでオーバーライドではない
    public String getEmail() {  	
    	return user.getEmail();  // ここ重要！！！手動で追加すること
    	// コントローラで System.out.println(userDetails.getEmail)  で、アクセスして取得できます
    }
    
 // ポイント！！
    // さらに手動でゲッターを追加すること これはスーパークラスに無いのでオーバーライドではない
    public Integer getAge() {  	
    	return user.getAge();  // ここ重要！！！手動で追加すること
    	// コントローラで System.out.println(userDetails.getAge)  で、アクセスして取得できます
    }
    // ポイント！！
    // さらに手動でゲッターを追加すること これはスーパークラスに無いのでオーバーライドではない
    public Integer getGender() {  	
    	return user.getGender();  // ここ重要！！！手動で追加すること
    	// コントローラで System.out.println(userDetails.getGender)  で、アクセスして取得できます
    }
    // ポイント！！
    // さらに手動でゲッターを追加すること これはスーパークラスに無いのでオーバーライドではない
    public String getTel() {  	
    	return user.getTel();  // ここ重要！！！手動で追加すること
    	// コントローラで System.out.println(userDetails.getGender)  で、アクセスして取得できます
    }


//	@Override
//	public String getPassword() {
//		return password;
//	}
//	@Override
//	public String getUsername() {
//		return username;
//	}

	// 最後の4つは、特に判定せず、全て true を返すようにしておきます
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		 return true;
	}

}
