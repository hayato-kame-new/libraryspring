package com.kame.springboot.service;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
	 * リポジトリを使用したやり方です 戻り値  Page<Book>
	 * こっちではなくて 戻り値 　List<Book> になるやり方の下のメソッドを使ってる
	 * @param pageable
	 * @return Page<Book>
	 */
	public Page<Book> getBooks(Pageable pageable) {
		
         return bookRepository.findAll(pageable);		
    }
	
	
	
	/**
	 * 戻り値 List<Book> になるやり方  order by id asc にしたい
	 * 登録してあるBookインスタンスを全て検索してListにして返す
	 * createNativeQuery(メソッドは JPQLではなく、素のSQL文  PostgreSQLなので、 テーブル名やカラム名は全てを小文字にすること
	 * order by employeeid を付けないと 順番が更新されたのが一番最後の順になってしまうのでorder byをつける
	 * Listクラスの代わりに org.springframework.data.domain.Pageを使ってページネーションを実装するが、
	 * createNativeQueryメソッドだと、戻り値が List<Book> じゃないとダメなので
	 * リクエストハンドラは index2メソッド   表示は books2.htmlです
	 * 
	 * @return
	 */
//	 public List<Book> booksList() {
//		// javax.persistence.Queryインタフェースの型のオブジェクトを生成する
//		Query query = entityManager.createNativeQuery("select * from books order by id asc");  // order by employeeid を付けないと 順番が更新されたのが一番最後の順になってしまうのでorder byをつける
//		// ページネーションを使うためには Listの代わりに Pageクラスを使いますが ここではList<Book> でしかできないので
//		 List<Book> list = (List<Book>)query.getResultList();  		
//		return list;
//	}
	 
	

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
	  * @return
	  */
	 public Book findBookDataById(Integer id) {
		
		 Query query = entityManager.createNativeQuery("select * from books where id = ?");
		 query.setParameter(1, id);
		 
		 // query.getResultList()で取得したデータは List<Object[]>になってます
		 List<Object[]> resultDataList = query.getResultList();
				
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
		 // Bookインスタンスを生成する
		 Book book = new Book(id, isbn, genre, title, authors, publisher, publishYear);
		 // Bookインスタンスを返す
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

}
