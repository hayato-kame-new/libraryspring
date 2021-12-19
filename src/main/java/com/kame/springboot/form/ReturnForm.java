package com.kame.springboot.form;

import javax.validation.constraints.NotNull;

//Formクラスに、相関チェック用のアノテーションをつけておきます 
// 相関チェックに関しては、各フィールドにはつけないでクラスだけにつけます
// フォームのクラス
public class ReturnForm {
	
	@NotNull(message = "書籍IDを入力してください")  //@NotNullを付与することで、nullのみ許容しなくなる intや Dateなどに使う
	private Integer bookId;
		
	/**
	 * コンストラクタ
	 */
	public ReturnForm() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	// アクセッサ
	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

}
