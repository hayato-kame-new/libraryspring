package com.kame.springboot.service;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.entity.Book;
import com.kame.springboot.repository.BookRepository;

@Service
@Transactional  // クラスに対して記述した設定はメソッドで記述された設定で上書きされる このクラスで @Transactionalをつけて、コントローラにはつけない
public class BookService {
	
	// booksテーブルは historiesテーブルとリレーションがある booksテーブルが親テーブル

	@Autowired
	BookRepository bookRepository;
	
	// @PersistenceContextは一つしかつけれない コントローラなどの方につけてたら削除する
	@PersistenceContext // EntityManagerのBeanを自動的に割り当てるためのもの サービスクラスにEntityManagerを用意して使う。その他の場所には書けません。１箇所だけ
	private EntityManager entityManager;
	
	/**
	 * ページネーションとソートを使って 書籍 全ての情報を取得
	 * リポジトリのメソッド自動生成機能を使う
	 * org.springframework.data.domain.Pageを使ってページネーションを実装する
	 * @param pageable
	 * @return Page<Book>
	 */
	public Page<Book> getPageableBooks(Pageable pageable) {
		
         return bookRepository.findAll(pageable);		
    }
	
	
	
	/**
	 * 戻り値 List<Book> になるやり方  order by id asc 
	 * 登録してあるBookインスタンスを全て検索してListにして返す
	 * createNativeQuery(メソッドは JPQLではなく、素のSQL文  PostgreSQLなので、 テーブル名やカラム名は全てを小文字にすること
	 * order by employeeid を付けないと 順番が更新されたのが一番最後の順になってしまうのでorder byをつける
	 * createNativeQueryメソッドだと、戻り値が List<Book>にできる
	 *  query.getResultList()で取得したデータは List<Object[]>になってます  List<エンティティ> にキャストもできる 
	 *  Iterable にキャストもできる (List<Book>)にキャストもできる
	 * @return List<Object[]>
	 */
	 public List<Object[]> booksList() {
		// javax.persistence.Queryインタフェースの型のオブジェクトを生成する
		Query query = entityManager.createNativeQuery("select * from books order by id asc");  // order by employeeid を付けないと 順番が更新されたのが一番最後の順になってしまうのでorder byをつける
		// ページネーションを使うためには Listの代わりに Pageクラスを使いますが ここではList<Book> でしかできないので
		// query.getResultList()で取得したデータは List<Object[]>になってます  Iterable にキャストもできる (List<Book>)にキャストもできる
		List<Object[]> list = query.getResultList();  		
		return list;
	}
	 
	

	 /**
	  * 書籍 新規登録
	  * @param book
	  * @return true:成功<br /> false:失敗
	  */
	 public boolean create(Book book) {
		 // createNativeQueryは普通のSQL文です JPQLではない  PostgreSQLは テーブル名 カラム名全て小文字にすること
		 // id が serial シリアルなので、insertする時には、値がなくていい 自動採番するカラムです
		 // Query query = entityManager.createNativeQuery("insert into books (isbn, genre,title, authors, publisher, publishyear) values (?, ?, ?, ?) ");
		 //  ? のプレースホルダーでもいいし こっちでもいい
		 Query query = entityManager.createNativeQuery("insert into books (isbn, genre, title, authors, publisher, publishyear) values (:a, :b, :c, :d, :e, :f) ");
		 
		 query.setParameter("a", book.getIsbn());
		 query.setParameter("b", book.getGenre());
		 query.setParameter("c", book.getTitle());
		 query.setParameter("d", book.getAuthors());
		 query.setParameter("e", book.getPublisher());
		 query.setParameter("f", book.getPublishYear());
		 
		 int result = query.executeUpdate(); // 戻り値は 更新や削除をしたエンティティの数か返る
		 
		 if(result != 1) {  // insert 1件 なので 1 が返れば成功   1以外 なら失敗
			 // 失敗
			 return false; // falseを返す 失敗したら、即returnして以下の行は実行されない 引数のfalseを呼び出し元へ返す
		 }
		 // 成功してるなら
		 return true;		 
	 }
	 
	 
	 /**
	  * 書籍を主キーで検索して オブジェクトを返す 
	  *  query.getResultList()で 取得したデータは List<Object[]>になってます
	  * @param id
	  * @return Book
	  */
	 public Book findBookDataById(Integer id) {
		
		 Query query = entityManager.createNativeQuery("select * from books where id = ?");
		 query.setParameter(1, id);
		 
		 // query.getResultList()で取得したデータは List<Object[]>になってます
		 //  Iterable にキャストもできる (List<Book>)にキャストもできる
		 List<Object[]> resultDataList = query.getResultList();
		 // まず、nullを代入
		 Book book = null;  // resultDataList が []だった場合に Bookインスタンスの中身は nullにして返す
		 if(resultDataList.size() > 0) {
			 
			 Iterator itr =  resultDataList.iterator();
			 
			 String isbn = "";
			 String genre = "";
			 String title = "";
			 String authors = "";
			 String publisher = "";
			 Integer publishYear =null;
			 
			 while(itr.hasNext()) {
				 Object[] obj = (Object[]) itr.next();
				 // id は取得しなくてもいい
				 // id = Integer.parseInt(String.valueOf(obj[0]));
				 isbn = String.valueOf(obj[1]);
				 genre = String.valueOf(obj[2]);
				 title = String.valueOf(obj[3]);
				 authors = String.valueOf(obj[4]);
				 publisher = String.valueOf(obj[5]);
				 publishYear = Integer.parseInt(String.valueOf(obj[6]));
			 }
			 // Bookインスタンスをnewで生成する
			  book = new Book(id, isbn, genre, title, authors, publisher, publishYear);
		 }
				
		 return book;		 
	 }
	 
	 
	 /**
	  * 書籍 更新 
	  * @param book
	  * @return true:成功<br /> false:失敗
	  */
	 public boolean update(Book book) {
		 Query query = entityManager.createNativeQuery("update books set (isbn, genre, title, authors, publisher, publishyear) = (?, ?, ?, ?, ?, ?) where id = ? ");
		 
		 query.setParameter(1, book.getIsbn());
		 query.setParameter(2, book.getGenre());
		 query.setParameter(3, book.getTitle());
		 query.setParameter(4, book.getAuthors());
		 query.setParameter(5, book.getPublisher());
		 query.setParameter(6, book.getPublishYear());
		 query.setParameter(7, book.getId());
		 
		 int result = query.executeUpdate();
		if (result != 1) { // 失敗
			return false; // 失敗したら false が返る
			// ここでreturnしたら このメソッドは即終了するので以降は実行されない 引数の falseを呼び出し元へ返す
		}
		return true;  // 成功
	 }
	 
	 /**
	  * 書籍 削除
	    *  ロールバックの注意点として、非検査例外(RuntimeException及びそのサブクラス)が発生した場合はロールバックされるが、検査例外(Exception及びそのサブクラスでRuntimeExceptionのサブクラスじゃないもの)が発生した場合はロールバックされずコミットされる
	 * RuntimeException以外の例外が発生した場合もロールバックしたいので @Transactional(rollbackFor =
	 * Exception.class)としてExceptionおよびExceptionを継承しているクラスがthrowされるとロールバックされるように設定します.
	 * 呼び出し元つまりコントローラ のメソッドでtry-catchする ここで例外処理をしてはいけない コントローラには @Transactional
	 * をつけないこと つけると、UnexpectedRollbackException発生する トランザクションをコミットしようとした結果、予期しないロールバックが発生した場合にスローされます
	 * 
	 * @Transactional(readOnly=false, rollbackFor=Exception.class) をつけること throws
	 *                                PersistenceException が必要
	  * @param id
	  * @return true:成功<br /> false:失敗
	  * @throws PersistenceException
	  */
	 @Transactional(readOnly = false, rollbackFor = Exception.class) // ここに@Transactionalをつけて、コントローラにはつけないでください
	 public boolean delete(Integer id)throws PersistenceException{// throwsして、呼び出しもとで
		 Query query = entityManager.createNativeQuery("delete from books where id = ?"); 
		 query.setParameter(1, id);
		 int result = query.executeUpdate();
		if (result != 1) { // 失敗
			return false; // 失敗したら false が返る
			// ここでreturnしたら このメソッドは即終了するので以降は実行されない 引数の falseを呼び出し元へ返す
		}
		return true;  // 成功
	 
	 }
	 
	  	/**
	  	 * And検索メソッド 曖昧検索 LIKE   リポジトリの自動生成機能は使えない
	  	 * JPQLの文なので Book はエンティティを示す
	  	 * 検索に必要なクエリを完成させ それの問い合わせにより得た結果を返す
	  	 * 検索フォームにはユーザーによって入力されないフィールドもある なのでそれぞれの引数に値が入力されているのかを確認しています
	  	 * 値が入力されていれば、条件の一部としてクエリに条件を付け足していきます
	  	 * また、値が入力されている引数が複数あるようであればクエリに「AND」を付け足すようにしています
	  	 * 例: authorに「村上春樹」、titleに「ノルウェイの森」と入力されており、それ以外は入力されなかった場合
	  	 * SELECT b From books b WHERE b.author LIKE :author AND b.title LIKE :title
	  	 * このクエリにsetParameterで値を割り当てています
	  	 *  query.getResultList()で取得したデータは List<Object[]>になってます
	  	 *   Iterable にキャストもできる (List<Book>)にキャストもできる
	  	 *   
	  	 *  List<Object[]>
	  	 * @param isbn
	  	 * @param genre
	  	 * @param title
	  	 * @param authors
	  	 * @param publisher
	  	 * @return List<Object[]>
	  	 */
	    public List<Object[]> searchBookAND(String isbn, String genre, String title, String authors, String publisher) {
	    
	    	StringBuilder sql = new StringBuilder();
	    	
	    	// 注意！！　JPQL文ですので、Bookはエンティティです なので大文字から始める
	    	//sql.append("SELECT b From Book b WHERE ");  
	    	sql.append("SELECT b From Book as b WHERE ");  // JPQLの文なので Book はエンティティを示す
	    	sql.append(" ");  // 一応半角空白を明示的に入れておくと ミスが防げる 
	    	boolean isbnFlg = false;
	    	 boolean genreFlg= false;
	    	 boolean titleFlg= false;	    	
	    	 boolean authorsFlg = false;
	    	 boolean publisherFlg = false;
	    	    	 
	    	 boolean andFlg= false;
	    	 
	    	 if(!"".equals(isbn)) {
	    	 sql.append("b.isbn LIKE :isbn");  
	    	 isbnFlg = true;
	    	 andFlg= true;
	    	 }
	    
	    	if(!( genre == null ||  "".equals(genre))) {
	    	  if (andFlg) sql.append(" AND ");  // 前後に半角空白が必要です
	    	  sql.append("b.genre LIKE :genre");  // b.エンティティのプロパティ名 bのエイリアスは省略できません
	    	  genreFlg = true;
	    	  andFlg = true;
	    	 }
	    	
	    	 if(!"".equals(title)) {
    		   if (andFlg) sql.append(" AND "); // 前後に半角空白が必要です
	    	   sql.append("b.title LIKE :title");// b.エンティティのプロパティ名 bのエイリアスは省略できません
	    	   titleFlg = true;
	    	   andFlg = true;
	    	 }
	    	 
	    	 if(!"".equals(authors)) {
	    		 if (andFlg) sql.append(" AND "); // 前後に半角空白が必要です
	    		 sql.append("b.authors LIKE :authors"); 
	    		 authorsFlg = true;
	    		 andFlg = true;
	    	 }
	    	 // 追加
	    	 if(!"".equals(publisher)) {
	    	 if (andFlg) sql.append(" AND "); // 前後に半角空白が必要です
	    	  sql.append("b.publisher LIKE :publisher");
	    	  publisherFlg = true;
	    	 andFlg = true;
	    	 }
			
	    	 
	    	 Query query = entityManager.createQuery(sql.toString());
				if (isbnFlg) query.setParameter("isbn", "%" + isbn + "%");
				 if (genreFlg) query.setParameter("genre", "%" + genre + "%");
				if (authorsFlg) query.setParameter("authors", "%" + authors + "%");
				if (titleFlg) query.setParameter("title", "%" + title + "%");
				// 追加
				if (publisherFlg) query.setParameter("publisher", "%" + publisher + "%");
				
				return query.getResultList();
				// query.getResultList()で取得したデータは List<Object[]>になってます
	    }
	    
	    
	    /**
	     * OR検索 書籍検索
	     * 指定した条件で OR検索 曖昧検索 をする
	     * @param isbn
	     * @param genre
	     * @param title
	     * @param authors
	     * @param publisher
	     * @return List<Object[]>
	     */
	    public List<Object[]> searchBookOR(String isbn, String genre, String title, String authors, String publisher) {
		    
	    	StringBuilder sql = new StringBuilder();
	    	
	    	// 注意！！　JPQL文ですので、Bookはエンティティです なので大文字から始める
	    	//sql.append("SELECT b From Book b WHERE ");  
	    	sql.append("SELECT b From Book as b WHERE ");  // JPQLの文なので Book はエンティティを示す
	    	 boolean isbnFlg = false;
	    	 boolean genreFlg= false;
	    	 boolean titleFlg= false;	    	
	    	 boolean authorsFlg = false;
	    	 boolean publisherFlg = false;
	    	    	 
	    	 boolean orFlg= false;
	    	 
	    	 if(!"".equals(isbn)) {
	    	 sql.append("b.isbn LIKE :isbn");  
	    	 isbnFlg = true;
	    	 orFlg= true;
	    	 }
	    	 // ここ変更した  後で直す 文学を選択したのに "0"になってる おかしい
	    	//  if(!( genre == null ||  "選択しない".equals(genre))) {
	    	if(!( genre == null ||  "".equals(genre))) {
	    	  if (orFlg) sql.append(" OR ");
	    	  sql.append("b.genre LIKE :genre");
	    	  genreFlg = true;
	    	  orFlg = true;
	    	 }
	    	 if(!"".equals(title)) {
    		   if (orFlg) sql.append(" OR ");
	    	   sql.append("b.title LIKE :title");
	    	   titleFlg = true;
	    	   orFlg = true;
	    	 }
	    	 if(!"".equals(authors)) {
	    		 if (orFlg) sql.append(" OR ");
	    		 sql.append("b.authors LIKE :authors"); 
	    		 authorsFlg = true;
	    		 orFlg = true;
	    	 }
	    	 // 追加
	    	 if(!"".equals(publisher)) {
	    	 if (orFlg) sql.append(" OR ");
	    	  sql.append("b.publisher LIKE :publisher");
	    	  publisherFlg = true;
	    	 orFlg = true;
	    	 }
			
	    	 
	    	 Query query = entityManager.createQuery(sql.toString());
				if (isbnFlg) query.setParameter("isbn", "%" + isbn + "%");
				 if (genreFlg) query.setParameter("genre", "%" + genre + "%");
				if (authorsFlg) query.setParameter("authors", "%" + authors + "%");
				if (titleFlg) query.setParameter("title", "%" + title + "%");
				// 追加
				if (publisherFlg) query.setParameter("publisher", "%" + publisher + "%");
				
				return query.getResultList();
				// query.getResultList()で取得したデータは List<Object[]>になってます
	    }
	    
		/**
		 * 社員検索
		 * @param departmentId
		 * @param employeeId
		 * @param word
		 * @return List
		 */
//		@SuppressWarnings("unchecked")
//		public List<Employee> find(String departmentId, String employeeId, String word) {
//			// 注意 引数のdepartmentId は 空文字とnullの可能ある   employeeId と word は ""空文字の可能性ある
	//
//			String sql = "select * from employee";
//			String where = ""; // where句
//			int depIdIndex = 0; // プレースホルダーの位置を指定する 0だと、プレースホルダーは使用しないことになる
//			int empIdIndex = 0;
//			int wordIndex = 0;
	//
//			if (departmentId == null) {
//				departmentId = "";
//			}
//			if (departmentId.equals("")) {
//				// 未指定の時 何もしない depIdIndex 0 のまま変更無し
//			} else {
//				where = " where departmentid = ?"; // 代入する 注意カラム名を全て小文字にすること departmentid また、前後半角空白入れてつなぐので注意
//				depIdIndex = 1; // 変更あり
//			}
	//
//			if (employeeId.equals("")) {
//				// 未指定の時 何もしない 
//			} else {
//				if (where.equals("")) { 
//					where = " where employeeid = ?"; // 代入する カラム名を全て小文字 employeeid
//					empIdIndex = 1;
//				} else {
//					where += " and employeeid = ?"; // where句はすでにあるので 二項演算子の加算代入演算子を使って連結 												
//					empIdIndex = depIdIndex + 1;
//				}
//			}
	//
//			if (word.equals("")) {
//				// 未指定の時何もしない
//			} else {
//				if (where.equals("")) { 
//					where = " where name like ?"; // 代入  
//					 wordIndex = 1;
//				} else if (where.equals(" where departmentid = ?")) {
//					where += " and name like ?"; // 二項演算子の加算代入演算子を使って連結 
//					 wordIndex = depIdIndex + 1;
//				} else if (where.equals(" where employeeid = ?")) {
//					where += " and name like ?"; // 二項演算子の加算代入演算子を使って連結 
//					 wordIndex = empIdIndex + 1;
//				} else if (where.equals(" where departmentid = ? and employeeid = ?")) {
//					where += " and name like ?"; // 二項演算子の加算代入演算子を使って連結 
//					 wordIndex = depIdIndex + empIdIndex + 1;
//				}
//			}
	//
//			Query query = entityManager.createNativeQuery(sql + where);
//			if (depIdIndex > 0) {
//				query.setParameter(depIdIndex, departmentId);
//			}
//			if (empIdIndex > 0) {
//				query.setParameter(empIdIndex, employeeId);
//			}
//			if (wordIndex > 0) {
//				query.setParameter(wordIndex, "%" + word + "%");
//			}
//			return query.getResultList(); // 結果リスト 型のないリストを返す 
//		}


	    /**
	     * ISBNから指定したレコードを取得する。 
	     * ISBN で探した時には、同じISBNの本が複数見つかることもある
	     * 人気本は、ハリーポッター１巻など 図書館システムに複数所蔵することもあるので
	     * 
	     *  リポジトリの辞書機能によって メソッド自動生成機能を使っている
	     *  戻り値は  List<Book> 複数見つかる場合もある
	     * @param isbn
	     * @return  List<Book> 複数見つかる場合もある
	     */
	    public List<Book> findBookDataByIsbn(String isbn){
	    	// これはリポジトリのメソッド自動生成機能を使っています
	      return bookRepository.findByIsbn(isbn);
	    }

	    /**
		 * これはエラーになるので使えない 参考にとっておく
		 * 
		 * 貸出記録を AND検索 完全一致検索
		 * 引数は nullの可能性もあるので int じゃなくて Integer
		 * 
		 * @param bookId
		 * @param memberId
		 * @param count  全てを選ぶと nullが入ってくる
		 * @return
		 */
//		 public List<Object[]> searchHistoryAND(Integer bookId, Integer memberId, Integer count) {
//			 
//			 StringBuilder sql = new StringBuilder();
//			 
//			// 注意！！　JPQL文ですので、Historyはエンティティです なので大文字から始める
//		    	//sql.append("SELECT m From History m WHERE ");  
//		    	sql.append("SELECT h From History as h WHERE ");  // JPQLの文なので Member はエンティティを示す
//		    	sql.append(" ");  // 一応半角空白を明示的に入れておくと ミスが防げる
//		    	boolean bookIdFlg = false;
//		    	 boolean memberIdFlg= false;
//		     		    	    	    	 
//		    	 boolean andFlg= false;
////		    	 JPQLの中の、 ?1 や :from などは、クエリの可変なパラメータを表現するバインド変数である。
////		    			 バインド変数の詳細は次節で解説するが、? はインデックス指定、 : はパラメータ名の文字列指定を意味する（1つのクエリの中に両者は混在できない）。
//			 
//		    	 // 最近の記録履歴から取得するので order by id desc とする
//		    	 // count には 10  20  30 または null が入ってくる limit 10 などとする nullだったら、 limit句がつかないで全部を取得する
//		    	 if(bookId != null) {
//		    		 // h.bookidと全て小文字にする h.そのエンティティのプロパティーの名前  hのエイリアスが必要  プレースホルダーの :bookid 小文字で
//		    		 // 完全一致検索は = イコールを使う 曖昧検索は　LIKEを使って、("isbn", "%" + isbn + "%")
//			    	 sql.append("h.bookId = :bookid");  // h.そのエンティティのプロパティーの名前
//			    	 bookIdFlg = true;
//			    	 andFlg= true;
//			    	 }
//		    	 
//		    	 if(memberId != null) {
//		    		   if (andFlg) sql.append(" AND ");  // 前後に半角空白が必要// JPQL文 大文字小文字を明確に区別する　予約語は大文字で書く
//		    		// h.memberidと全て小文字にする    プレースホルダーの :memberid 小文字で
//			    	   sql.append("h.memberId = :memberid ");
//			    	   memberIdFlg = true;
//			    	   andFlg = true;
//			    	 }
//		    	 
//		    	 // LIMITはsetFirstResultやsetMaxResultsで複数結果の取得開始件数や取得件数を指定することができる。 
//		    	 // これらのメソッドを実行すると、SQLにlimitやoffsetなどのデータベースにあわせた件数指定が付与される。
//		    	 
//		    	 // だから、ここでは countが null　でも nullじゃなくても、
//		    	
//		    		 sql.append(" ");  // 半角を入れないとできないので 明示的に入れとくといい
//		    		 sql.append(" ORDER BY h.id DESC "); 	    		 
//		    	 
//		    		//  SELECT h From History as h WHERE  h.bookId = :bookid AND h.memberId = :memberid   ORDER BY h.id DESC 
//		    		 
//		    		 // リレーションのついた子テーブルだから,createQueryではできないのかも
//		    		 // createNativeQueryで 作り直してやる 
//		    	 Query query = entityManager.createQuery(sql.toString());
//					if (bookIdFlg) query.setParameter("bookid",  bookId );  // 完全一致検索なので 第二引数は "%" + bookId + "%" では無い
//					 if (memberIdFlg) query.setParameter("memberid", memberId );  // 完全一致検索
//					  // 取得開始位置や件数の指定。
//					 query.setFirstResult(1).setMaxResults(count);
//					 return query.getResultList();
//						// query.getResultList()で取得したデータは List<Object[]>になってます
//		 }
		 
	    
	   
	    /**
	     * idで検索した本が、図書館システムに存在してるかどうかを調べる (idがnullじゃない時に)
	     * @param id
	     * @return
	     */
	    public boolean exist(int id) {
	    	Query query = entityManager.createNativeQuery("select * from books where id = ? ");
	    	query.setParameter(1, id);
	    	List<Object[]> list = query.getResultList();
	    	if(list.size() > 0) {
	    		return true;
	    	}
	    	return false;
	    }

}
