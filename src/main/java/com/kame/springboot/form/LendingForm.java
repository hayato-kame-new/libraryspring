package com.kame.springboot.form;

import javax.validation.constraints.NotNull;

//Formクラスに、相関チェック用のアノテーションをつけておきます 
// 相関チェックに関しては、各フィールドにはつけないでクラスだけにつけます
// フォームのクラス
public class LendingForm {

	
	@NotNull(message = "書籍IDを入力してください")
	private Integer bookId;
	
	@NotNull(message = "会員IDを入力してください")
	private Integer memberId;

	
	/**
	 * コンストラクタ
	 */
	public LendingForm() {
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

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	
	
	
}
