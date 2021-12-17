package com.kame.springboot.controller;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kame.springboot.component.ViewBean;
import com.kame.springboot.form.SignupForm;
import com.kame.springboot.userDetailsService.UserDetailsServiceImpl;

@Controller
@RequestMapping("/")
public class UserRegisterLoginController {

	@Lazy // アプリケーション起動時に 循環サイクルで、エラーにならないように書く
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	// ユーザー登録に必要 @Autowired アノテーションを使用して、SecurityConfig クラスで Bean 定義した PasswordEncode を取得します
	@Autowired
    PasswordEncoder passwordEncoder;
	
	// ビューのためのBeanを用意する
	@Autowired
	ViewBean viewBean;
	
	/*
	 * このようにも書ける アノテーションを使えば newしてBeanを自動で生成してくれる
	 * リクエストハンドラの引数に宣言するだけでいい 依存性注入
	 * @AuthenticationPrincipal からユーザー情報を取得する
	 * ユーザー情報は、アノテーション @AuthenticationPrincipal を使用すると、より簡単に取得することができます
	 * SecurityContext context = SecurityContextHolder.getContext();
	 * Authentication authentication = context.getAuthentication();
	 * UserDetails principal = (UserDetails) authentication.getPrincipal();
	 * と同じことを、@AuthenticationPrincipal アノテーション をつけると自動でやってくれる
	 */
//	@GetMapping
//	public String index(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//		System.out.println(userDetails.getUsername());  // ユーザー名を表示
//        System.out.println(userDetails.getPassword());  // パスワードを表示
//        System.out.println(userDetails.getAuthorities().toString());  // 権限情報を表示
//        return "index";
//	}

	/**
	 * トップページ表示
	 * 
	 * @return
	 */
	@GetMapping
	public String index() {
		// SecurityContextHolder からユーザー情報を取得する
		// SecurityContextHolderからAuthenticationオブジェクトを取得
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();

		// Authenticationオブジェクトからユーザー情報を取得
		System.out.println(authentication.getName()); // ユーザー名を表示
		System.out.println(authentication.getAuthorities()); // 権限情報を表示

		// このようにも取得できる
		// Authenticationオブジェクトからユーザー情報を取得
		UserDetails principal = (UserDetails) authentication.getPrincipal();
		System.out.println(principal.getUsername()); // ユーザー名を表示
		System.out.println(principal.getPassword()); // パスワードを表示
		System.out.println(principal.getAuthorities()); // 権限情報を表示
		
		// パスワードハッシュ化を表示する
		System.out.println(passwordEncoder.encode("123456"));
		System.out.println(passwordEncoder.encode("password"));
		System.out.println(passwordEncoder.encode("password1"));
		System.out.println(passwordEncoder.encode("password2"));
		return "index";
	}

	/*
	 * ログインする http://localhost:8080/login にアクセスすると、独自のログインフォームが表示されます
	 * 正しいパスワードを入力すれば、 SecurityConfig.java の defaultSuccessUrl
	 * メソッドで指定したリダイレクト先（"/"）の画面が表示されます 誤ったパスワードを入力するとエラーメッセージも表示されます
	 * エラーメッセージの日本語化をしてます 英語メッセージを日本語に置き換えるための空ファイル messages.properties を、
	 * （/src/main/resources の直下）に作成しておきます。実際は、どのディレクトリに作成しても大丈夫なようです
	 * このファイルにメッセージを記載することで、メッセージのカスタマイズ（日本語化）をすることができます Jar
	 * ファイルの中に日本語メッセージが用意されているので、それを使う Package Explorer の Maven Dependencies にある Jar
	 * ファイルを見ていきます
	 * 
	 * プロジェクトを右クリックして 上のウィンドウタブをクリック ビューの表示 その他 を選ぶ Java を選ぶ パッケージエクスプローラを選んで 開くボタン
	 * Maven依存関係にある Jarファイルを見ていきます Maven Dependencies の中から
	 * spring-security-core-5.5.0.jar を探し出して org.springframework.security フォルダを開きます
	 * このフォルダの中に、様々な言語の messages.properties が用意されています
	 * これらのファイルのうち、次の2つのファイルが重要です（日本人にとっては） messages.properties Spring Security
	 * が用意しているメッセージ（デフォルト） messages_ja.properties Spring Security が用意しているメッセージ（日本語)
	 * 日本語が unicode の文字コードで表されています
	 * 文字コードの上にカーソルを合わせると、次のような表示がされてメッセージの内容を確認することができます この messages_ja.properties
	 * のテキストを全てコピーして 最初に作成した /src/main/resources 直下の空ファイル messages.properties
	 * に貼り付けます メッセージの日本語化は完了 messages.properties の内容が文字コード（unicode）なので
	 * 文字コード表記を日本語文字に変換する messages.properties ファイルを右クリックして Properties を開き
	 * ファイルの文字コードを UTF-8 にしておく なってればいい messages.properties
	 * ファイルに日本語文字を記載することができるようになります ConvertTest クラスファイルを一つ作ります（テストをするわけではないです）
	 * 
	 */
	/**
	 * ログイン画面表示
	 * 
	 * @return
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	/**
	 * ユーザー登録画面表示
	 * 
	 * @param signupForm
	 * @return
	 */
	@GetMapping("/signup")
	public ModelAndView newSignup(SignupForm signupForm, ModelAndView mav) {
		
		mav.setViewName("signup");
		// 表示のための Mapを作成して、ビューへ送ります
		Map<Integer, String> genderMap = viewBean.getGenderMap();
		
		mav.addObject("genderMap", genderMap);
		
		return mav;
	}

	/**
	 * ユーザー登録実行
	 * 
	 * @param signupForm
	 * @param result
	 * @param model
	 * @return String
	 * 
	 */
	@PostMapping("/signup")
	public ModelAndView signup(
			// @Validated SignupForm signupForm, 
			@ModelAttribute("signupForm")@Validated SignupForm signupForm,
			BindingResult result, 
			ModelAndView mav,
			HttpServletRequest request) {
		
		// @Validatedをつけて、バリデーションチェックを有効にしてる バリデーションエラーがあったら
		if (result.hasErrors()) { // BindingResult result を引数として追加しています ここに、エラー情報が格納されます
			mav.setViewName("signup");
			// バリデーションエラーがあったので、前に入力してある値を表示させるために resultから取得する
			// 表示用 Resultオブジェクトから、前に入力してある値を取得します！！！
			SignupForm target = (SignupForm) result.getTarget();  // resultから フォームオブジェクトが取得できる
			// 性別ラジオボタン表示用のインスタンスを取得
			Map<Integer, String> genderMap = viewBean.getGenderMap();
			mav.addObject("genderMap", genderMap);	// 表示させるために送る
			// もう一つ、前に何を選択したのかを送る
			// mav.addObject("selectedGender" , target.getGender());  // 前のフォームで選択していたものを 選択済みのデータとして送る
			return mav; // エラーの場合には /signup のページを表示する
		}
		// すでに登録されているユーザ名を使って登録しようとすると エラー
		if (userDetailsServiceImpl.isExistUser(signupForm.getUsername())) {
			mav.setViewName("signup");
			mav.addObject("signupError", "ユーザー名 " + signupForm.getUsername() + "は既に登録されています");
			// 追加
			SignupForm target = (SignupForm) result.getTarget();  // resultから フォームオブジェクトが取得できる
			// 性別ラジオボタン表示用のインスタンスを取得
			Map<Integer, String> genderMap = viewBean.getGenderMap();
			mav.addObject("genderMap", genderMap);	// 表示させるために送る
			// もう一つ、前に何を選択したのかを送る
			// mav.addObject("selectedGender" , target.getGender());  // 前のフォームで選択していたものを 選択済みのデータとして送る

			return mav;
		}

		/*
		 * ここを修正する サインアップのフォームを作り直してから
		 */
		try {
			// ここを修正した
			// userDetailsServiceImpl.register(signupForm.getUsername(), signupForm.getPassword(), "ROLE_USER");
			// INSERT INTO users(name, password, authority, email, gender, age, tel)
			// VALUES('yama3', '$2a$10$KBVjUqJO8wdYklH9dV4RFOOMzd0rJhjEJtwJDeEep3GTAefMbCynO', 'ROLE_USER','yama3@yama3.com', 1, 20, '0299-00-0000' );
			
			userDetailsServiceImpl.register(signupForm.getUsername(), signupForm.getPassword(), "ROLE_USER", signupForm.getEmail(), signupForm.getGender(), signupForm.getAge(), signupForm.getTel());
			
		} catch (DataAccessException e) {
			mav.setViewName("signup");
			mav.addObject("signupError", "ユーザー登録に失敗しました");
			// 追加
			SignupForm target = (SignupForm) result.getTarget();  // resultから フォームオブジェクトが取得できる
			// 性別ラジオボタン表示用のインスタンスを取得
			Map<Integer, String> genderMap = viewBean.getGenderMap();
			mav.addObject("genderMap", genderMap);	// 表示させるために送る
			// もう一つ、前に何を選択したのかを送る
			// mav.addObject("selectedGender" , target.getGender());  // 前のフォームで選択していたものを 選択済みのデータとして送る

			return mav;
		}

		/*
		 * ユーザー登録できたなら、自動でログインをするようにする処理を書く 既にログインしている場合は、一旦ログアウトさせるようにしてます。
		 * SecurityContext は、セキュリティ情報（コンテキスト）を定義するインターフェース
		 * 実行中のアプリケーションに関するセキュリティ情報（コンテキスト）は、SecurityContextHolder の getContext
		 * メソッドを使用することで取得することができます
		 * SecurityContextHolder には、現在アプリケーションとやり取りをしているユーザーに関する情報も含まれています
		 * セッション中のユーザー情報の取得は Authentication インターフェイスで定義されており、SecurityContextHolder の
		 * getAuthentication メソッドを使用することにより取得することができます
		 * この Authentication オブジェクトから、ユーザー名や権限情報（ロール）なども取得することができます
		 * 
		 */
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication(); // org.springframework.security.core.Authentication
		/*
		 * ここで、もし既にログインしている場合は、一旦ログアウトさせます
		 * 自動的にログインさせる処理を行う前に、既にログインしているユーザーをログアウトさせる処理を追加します 匿名ユーザー（ログインしていないユーザー）
		 * かどうかを判定する 匿名ユーザー（anonymousUser）かどうかは、Authentication オブジェクトが
		 * AnonymousAuthenticationToken インスタンスであるかどうかを確認することで判定することができます authentication
		 * instanceof AnonymousAuthenticationToken == false 条件式が trueを返せば 匿名ユーザー
		 * つまりログインしてないユーザー falseを返せば すでにログイン済みのユーザー
		 */
		if (authentication instanceof AnonymousAuthenticationToken == false) { // org.springframework.security.authentication.AnonymousAuthenticationToken;
			SecurityContextHolder.clearContext(); // セッションスコープから、ユーザー情報を削除してます つまりログアウト処理してる
			// 現在ログイン中と判定されたユーザーに対してログアウト処理を実行
			// ログアウト処理は、SecurityContextHolder に格納されているセキュリティ情報（コンテキスト）
			// を消去することで実行します。消去には、clearContext メソッドを使用します 現在のスレッドからコンテキスト値をクリアする
		}
		// HttpServletRequest オブジェクトの loginメソッドでログインをします
		// 新たに登録されたユーザー名とパスワードでログイン処理を行っています
		try {
			request.login(signupForm.getUsername(), signupForm.getPassword());
		} catch (ServletException e) {
			e.printStackTrace();
		}
		return new ModelAndView("redirect:/");
		// return "redirect:/";
	}
	// BindingResultは、@ModelAttributeのすぐ下につけないと エラーになる BindingError
}
