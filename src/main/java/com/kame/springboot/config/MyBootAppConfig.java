package com.kame.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kame.springboot.bean.Library;

@Configuration  // このクラスが構成クラスであることを表すアノテーション
public class MyBootAppConfig {
	
	/**
	 * @Configurationアノテーションをつけたクラスに
	 * @Beanアノテーションをつけたメソッドを定義すると、
	 * そのメソッドが、Beanとして登録するインスタンスを返すということを示しています
	 * これで、普通のクラスとして定義したLibraryクラスが、Beanとしてのクラスになります
	 * library()というメソッドを定義します
	 * 
	 * @return
	 */
	@Bean
	Library library() {  // メソッドの定義をしています
		return new Library();  // コンストラクタを使ってインスタンスを生成しています それをリターンしてる
	}

}
