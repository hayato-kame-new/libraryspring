package com.kame.springboot.userDetailsService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.entity.User;
import com.kame.springboot.userDetails.UserDetailsImpl;


@Service  // @Service が必要です
public class UserDetailsServiceImpl implements UserDetailsService {
	
	// @Autowiredをつけると フィールドとして 自動でBeanインスタンスを生成してくれる @Autowiredは記述するだけで他のクラスを呼び出すことができる
	// @Autowiredを使わないといちいちnewを書いてクラスを呼び出さないといけないです
	@Autowired
    JdbcTemplate jdbcTemplate;
	
	// ユーザー登録に必要 @Autowired アノテーションを使用して、SecurityConfig クラスで Bean 定義した PasswordEncode を取得します
	@Autowired
    PasswordEncoder passwordEncoder;

	/*
	  UserDetailsService インターフェイスには次のようになっています
	 public interface UserDetailsService {
	 	// abstractな 抽象メソッドが宣言だけされてる インタフェースだから 抽象メソッドになる
    	// ユーザー名からユーザー情報を取得  {}メソッドの本体は無い 宣言だけ  {}メソッドの本体は無いので {}も書いてはいけない
    	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;  
	}
	この、loadUserByUsername抽象メソッドを、実装したクラスでは、@Overrideをして、public修飾子をつけて、実装する つまり {}の本体を書く
	JdbcTemplate を使用し、SQL文でレコードを抽出して、取得したユーザー情報を UserDetails（UserDetailsImpl）に詰め込んでいます
	メソッドは、例外を発生させる可能性があるので、throw宣言を書きます 例外が発生した場合は、このメソッド内で処理をしないで、
	このメソッドの呼び出し元へ例外を投げます。
	注意  例外処理（UsernameNotFoundException）
	例外が発生した場合は、UsernameNotFoundException がスローされます
	しかし、Spring Security のデフォルトの動作では、UsernameNotFoundException は
	 BadCredentialsException という例外に変換してからエラー処理が行われるようになっています（参考記事「認証例外情報の隠蔽」
	 認証例外情報の隠蔽するために 
	そのため、クライアント側では「Bad credentials（ユーザー名かパスワードが正しくありません）」という通知を受け取リマス！！！
	 */
	/**
	 * ユーザー名でログイン認証する　
	 * usersテーブルを変更してるので
	 * Userインスタンスを作成するところ id email tel age gender のところ、変更してます
	 * 
	 * login.htmlには メールアドレスのフォームも追加する
	 * <label for="password">パスワード</label>
	 * <input type="password" id="password" name="password"><br>
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			// 注意 PostgreSQLなので、テーブル名 カラム名を全て小文字に書くこと
            String sql = "SELECT * FROM users WHERE name = ?";  // usersテーブルのカラム名は name です
            Map<String, Object> map = jdbcTemplate.queryForMap(sql, username);
            String password = (String)map.get("password");
            // ここポイント
            // 追加  
            String email = (String)map.get("email");
            // 追加
            int id = (int) map.get("id");  // キャストIntegerにしたほうがいいかな Userの 主キーIntegerのほうがいいかな
            // 追加
            String tel = (String)map.get("tel");
            // 追加
            Integer age = (Integer) map.get("age");
            // 追加
            Integer gender = (Integer) map.get("gender"); 
            
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority((String)map.get("authority")));
            // ここを修正します
            // return new UserDetailsImpl(username, password, authorities);
            // まず、データベースから取得してきた検索してきたuserの情報でユーザーインスタンスを生成します
            User user = new User(id, username, password, gender, age, email, tel);  
            return new UserDetailsImpl(authorities, user);
        } catch (Exception e) {
            throw new UsernameNotFoundException("user not found.", e);  // 呼び出し元へUsernameNotFoundExceptionインスタンスを投げてます
            // 呼び出し元へUsernameNotFoundExceptionインスタンスを投げてますが,,,しかし、
            // 認証例外情報の隠蔽するために クライアント側ではBadCredentialsException という例外に変換されてエラー表示されています
            
        }

	}
	
	/**
	 * ユーザー登録
	 * このメソッドは、カラムを追加したので作り直します
	 *  注意 テーブル名 usersです！！dbcTemplate の update メソッドで、データベースにユーザー情報を登録します
	 *  パスワードは、PasswordEncoder（BCrypt）でハッシュ化しておきます
	 *  
	 * @param username
	 * @param password
	 * @param authority
	 */
//	@Transactional
//    public void register(String username, String password, String authority) {
//        String sql = "INSERT INTO users(name, password, authority) VALUES(?, ?, ?)";
//        jdbcTemplate.update(sql, username, passwordEncoder.encode(password), authority);
//    }

	/**
	 * ユーザー新規登録  usersテーブルにInsertする
	 * 引数の順番をSQLのInsertに合わせてください
	 * @param username
	 * @param password
	 * @param authority
	 * @param email
	 * @param gender
	 * @param age
	 * @param tel
	 */
	@Transactional
	  public void register(String username, String password, String authority, String email, Integer gender, Integer age, String tel ) { // 引数の順番注意Insertに合わせて
			
			// JdbcTemplate の update メソッドで、データベースにユーザー情報を登録します
			// パスワードは、PasswordEncoder（BCrypt）でハッシュ化しておきます。
		// 注意 テーブル名users  ユーザの名前のカラムは name です
		// INSERT INTO users(name, password, authority, email, gender, age, tel)
		// VALUES('yama3', '$2a$10$KBVjUqJO8wdYklH9dV4RFOOMzd0rJhjEJtwJDeEep3GTAefMbCynO', 'ROLE_USER','yama3@yama3.com', 1, 20, '0299-00-0000' );
		// Insertするカラムの順番に注意 updateメソッドの引数の順番にも注意
	      String sql = "INSERT INTO users(name, password, authority, email, gender, age, tel) VALUES(?, ?, ?, ?, ?, ?, ?)";
	      jdbcTemplate.update(sql, username, passwordEncoder.encode(password), authority, email, gender, age, tel);
	  }

	
	/**
	 * データベースに同一ユーザー名が既に登録されているかを確認する
	 * @param username
	 * @return
	 */
	public boolean isExistUser(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE name = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, new Object[] { username });
        if (count == 0) {
            return false;
        }
        return true;
    }
}
