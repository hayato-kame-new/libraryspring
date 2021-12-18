package com.kame.springboot.form;

import javax.validation.constraints.Size;

//Formクラスに、相関チェック用のアノテーションをつけておきます 
//相関チェックに関しては、各フィールドにはつけないでクラスだけにつけます
//フォームのクラス
public class MemberSearchForm {
	
	//  指定した条件で AND検索を行うためのフォーム フィールドに何も入力しなければ 条件を指定していないことになる
	// ただし、5つとも全て何も条件を入れない場合は、何もせずに、コントローラーでは、リダイレクトをして、また検索画面へ戻るようになってる

		// idは完全一致検索 idも必要 @NotEmptyつけない 
		private Integer id;  // int にすると 0 がデフォルト値としてフォームに表示されてしまうので Integerにする
		// Integerだと 何も入力しないでフォームを送信すると nullが入ってる
		
		// Stringは 何も入力しないでフォームを送信すると ""空文字が入って送られてる
		// 曖昧検索する 名前に含まれる文字を入れて 曖昧検索する 入力しなくてもいいので @NotEmpty などはつけない
		@Size(max = 100, message = "名前に含まれる文字の検索フォームには100文字以内で入力してください") //   minは書かないこと 
		private String name;  
		// membersテーブルでは nameカラムにUNIQUEはつけません usersテーブルのユーザ名はユニークだけど このmembersテーブルのユーザ名は同姓同名を許してるので

		public MemberSearchForm() {
			super();
			// TODO 自動生成されたコンストラクター・スタブ
		}
	

		public String getName() {
			return name;
		}


		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}
		
}
