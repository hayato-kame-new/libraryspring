package com.kame.springboot.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component  // コンポーネントにする Bean化して使うクラスになる
public class ViewBean {  // 必要！！  このクラスを コンポーネントにするには、 コンストラクタに、@Autowired　を付けます

	// フィールド宣言があればここに書く
	
	
	//  このクラスを コンポーネントにするには、 コンストラクタに、@Autowired　を付けます
	/**
	 * コンストラクタです コンストラクタに@Autowired をつけると 
	 * 他のクラスで、@Autowiredをつけてフィールド宣言すると、Bean化して コンポーネントのクラスとして使えるようになります
	 */
	@Autowired
	public ViewBean() {
		super();
	}
	
	// あとは、使用したい、インスタンスメソッドを定義すればいい
	/**
	 * 性別のラジオボタンのためのMapを作って返す. 
	 * @return Map<Integer, String>
	 */
	public Map<Integer, String> getGenderMap() {
		Map<Integer, String> genderMap = new LinkedHashMap<Integer, String>();  // LinkedHashMap  は追加された順番を保持する
		genderMap.put(1, "男性");
		genderMap.put(2, "女性");
		return genderMap;
	}
	
	/**
	 * ジャンルのセレクトボックス用にMapを作って返す
	 * @return
	 */
	public Map<Integer, String> getGenreMap() {
		//  LinkedHashMap  は追加された順番を保持する
		 Map<Integer, String> genreMap = new LinkedHashMap<Integer, String>(); // LinkedHashMap格納した順序を記憶する
		 List<String> list = new ArrayList<String>(Arrays.asList("文学", "推理", "エッセイ", "プログラミング", "漫画", "宗教")); // LinkedHashMap  は追加された順番を保持する
		 
		 for(int i = 0; i < list.size(); i++) {
			 genreMap.put(i, list.get(i));
			}
			return genreMap;		 
	}

}
