package com.kame.springboot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 
 * Spring Security の設定を行うために、WebSecurityConfigurerAdapter というクラスを継承する必要がある
 * このクラスには「Spring Securityの設定情報が定義されており、対象のメソッドをオーバーライドすることで設定を変更することができる
 * 
 * ＠EnableWebSecurity ですが これを指定することで Spring Security の機能が有効化されます
 * 密に言うと、このアノテーションにより「Spring Security が提供しているコンフィギュレーションクラスがインポートされ、
 * Spring Security を利用するために必要となるコンポーネントの Been 定義が自動で行われる ＠Configuration と ＠EnableWebSecurity の両方を指定してもいい ＠Configuration のみを指定でもいい?
 * 
 * @author skame
 * 
 */
@EnableWebSecurity //  ＠EnableWebSecurity ですが、これを指定することで Spring Security の機能が有効化されます
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Lazy
	@Autowired
    private UserDetailsService userDetailsService;
	 /*
	  * クライアント側から入力された平文のパスワードと、データベースのハッシュ化されたパスワードを照合するために、
	  * パスワードエンコーダーを設定する必要があります
	  * @Been アノテーションを付けて Been 定義を行い、DI コンテナに登録します Bean 定義でコンポーネント化して、DIコンテナにインジェクションする
	  * @Beanと書いたメソッドでインスタンス化されたクラスがシングルトンクラスとしてDIコンテナに登録される。任意のクラスで@Autowiredで注入してアクセスできる
	  */
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	/*
	 * WebSecurity クラスを使用して、主にアプリケーション全体に関するセキュリティの設定を行います
	 * ignoring()      Spring Security が無視する RequestMatcher インスタンスを追加できる
	 * antMatchers (String... antPatterns)    ant パターンに一致するリソース（List）を適用対象にする
	 * 引数の型と数が違うので同じメソッド名で多重定義オーバーロードできる
	 */
	@Override
    public void configure(WebSecurity web) throws Exception {  // 引数の型と数が違うので同じメソッド名で多重定義オーバーロードできる
		// CSS フォルダにあるファイルに対して、Spring Security の処理を適用しないようにしています
        web.ignoring().antMatchers("/css/**");
    }
	
	

	// 引数の型と数が違うので同じメソッド名で多重定義オーバーロードできる
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {  // 引数の型と数が違うので同じメソッド名で多重定義オーバーロードできる
    	/*
    	 * データベースを使用するからこれに書き換えます auth.userDetailsService(userDetailsService);
    	 * これで データベースに登録したユーザー情報で、ログイン認証ができるようになります
    	 */
    	auth.userDetailsService(userDetailsService);
    	
    	/*
    	 * これは全てメモリ内認証の場合です
    	 * データベースはまだ使わないので
    	 * inMemoryAuthentication というメソッドを使用して、メモリ内にユーザー情報を格納して認証を行うようにしています
    	 * 後で、データベースを使用するときは、これをコメントアウトします
    	 * ユーザー名、パスワード等は、次の形で指定します
    	 * auth.inMemoryAuthentication().withUser(ユーザー名).password(パスワード).roles(権限情報);
    	 * inMemoryAuthentication()
			メモリ内認証を追加する
			withUser(ユーザー名)
			ユーザー名を指定
			password(パスワード)  
			パスワードを指定（ハッシュ化されたもの）パスワードは平文ではなく、ハッシュ化した文字列を記載する必要があります。
			roles(権限情報)
			ユーザーが保持する権限情報を指定
			あらかじめハッシュ化した文字列を指定したい場合は、どこか適宜の場所に次のコードを書けば、コンソール画面から取得できます
			System.out.println(new BCryptPasswordEncoder().encode("123456"));
			指定したユーザー名（yama3）とパスワード（123456）でログインできるかを確認
    	 */
//        auth.inMemoryAuthentication()
//            .withUser("yama3")  // ユーザー名
//            .password(passwordEncoder().encode("123456")) // パスワードは「123456」パスワードは「ハッシュ化された文字列」をセットする必要がありますので、BCryptPasswordEncoder の encode メソッドでハッシュ化しています
//            .roles("USER");
        
        /*
         *  当然ですが、次のように、ハッシュ化されたパスワードを直接記載しても、問題なく動作します
         */
//        auth.inMemoryAuthentication()  //メモリ内認証を追加する
//        .withUser("yama3")
//        .password("$2a$10$E55vg96856cWy4oyAUpQ6OH2mxO6eTt43A5lPwa3MszPbDpAOPiLG")
//        .roles("USER");
               
    }
    
    // 引数の型と数が違うので同じメソッド名で多重定義オーバーロードできる 
    @Override
    protected void configure(HttpSecurity http) throws Exception {  // HttpSecurity クラスを使用して、主にURLごとのセキュリティの設定を行う
    	/*
    	 * 1
    	 * 「全てのリクエストの承認は、ログインしていることが条件」
    	 * authorizeRequests メソッドでアクセス制限の設定を呼び出し
    	 * anyRequest で全てのリクエストを対象
    	 *  authenticated メソッドで認証済み（ログイン済み）のユーザーにのみリクエストを承認する
    	 */
//    	http.authorizeRequests()  
//    	.anyRequest()
//    	.authenticated();
    	
    	/*
    	 * 2
    	 * formLogin メソッドでフォーム認証を使用することを指定しています フォーム認証をサポートすることを指定
    	 * loginPage メソッドでログイン画面のURLを /login と指定  ログインが必要な場合にユーザーを送信する URL を指定する
    	 * defaultSuccessUrl メソッドで認証後にリダイレクトされるページを指定  認証後にユーザーがリダイレクトされる場所を指定
    	 * permitAll メソッドで全てのユーザーにアクセスの許可を与えています
    	 * なお、Spring Security では、フォーム認証のほか、Basic 認証、Digest 認証、Remember Me 認証用のサーブレットフィルタクラスも提供されています（Spring 徹底入門423ページ）
    	 */
//    	http.formLogin()
//    	.loginPage("/login")
//    	.defaultSuccessUrl("/")
//    	.permitAll();
    	
    	/*
    	 * 3
    	 * 
    	 * gout() メソッドでログアウト機能を有効にして
    	 * permitAll() で全てのユーザーに対してログアウト機能に関するアクセス権を付与しています
    	 */
//    	http.logout()
//        .permitAll();
    	
    	
    	/*
    	 * 上の1 2 3 を 次のようにメソッドを繋げてコードを記載しています
    	 * 
    	 */
//            http.authorizeRequests().anyRequest().authenticated().and()
//                .formLogin().loginPage("/login").defaultSuccessUrl("/").permitAll().and()
//                .logout().permitAll();
    	
    	/*
    	 * 上の1 2 3 を 次のようにメソッドを繋げてコードを記載しています
    	 * ユーザー登録も加えてます .antMatchers("/signup").permitAll() を加えているだけです 
    	 * これにより、ログインしていないユーザーにも「ユーザー登録画面（/signup）」にアクセスすることが許可されます
    	 */
    	 http.authorizeRequests()
         .antMatchers("/signup").permitAll()
         .anyRequest().authenticated().and()
         .formLogin().loginPage("/login").defaultSuccessUrl("/").permitAll().and()
         .logout().permitAll();
    	
    }

}
