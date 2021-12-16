package com.kame.springboot.form;

//Formクラスに、相関チェック用のアノテーションをつけておきます 
//相関チェックに関しては、各フィールドにはつけないでクラスだけにつけます
//フォームのクラス
public class HistorySearchForm {
	
	// 曖昧検索 で 指定した条件で AND検索を行うためのフォーム フィールドに何も入力しなければ 条件を指定していないことになる
	// ただし、全て何も条件を入れない場合は、何もせずに、コントローラーでは、リダイレクトをして、また検索画面へ戻るようになってる

	
	private Integer bookId;  //曖昧検索する  入力しなくてもいいので @NotNull などはつけない
	
	
	private Integer memberId;  // 曖昧検索する 入力しなくてもいいので @NotNull などはつけない 
	
	// String型
	private String countDisplay;  // 曖昧検索する 入力しなくてもいいので @NotBlank などはつけない セレクトボックス使用 "全て" 欄もある


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


	public String getCountDisplay() {
		return countDisplay;
	}


	public void setCountDisplay(String countDisplay) {
		this.countDisplay = countDisplay;
	}

	
}
