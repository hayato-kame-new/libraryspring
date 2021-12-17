package com.kame.springboot.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.entity.History;
import com.kame.springboot.repository.HistoryRepository;

@Service
@Transactional  // クラスに対して記述した設定はメソッドで記述された設定で上書きされる このクラスで @Transactionalをつけて、コントローラにはつけない
public class HistoryService {
	
	// historiesテーブルは booksテーブルとリレーションがある historiesテーブルが子テーブル  booksテーブルが親テーブル
	// historiesテーブルは membersテーブルとリレーションがある historiesテーブルが子テーブル  membersテーブルが親テーブル

		@Autowired
		HistoryRepository historyRepository;
			
		// @PersistenceContextは一つしかつけれない コントローラなどの方につけてたら削除する
		@PersistenceContext // EntityManagerのBeanを自動的に割り当てるためのもの サービスクラスにEntityManagerを用意して使う。その他の場所には書けません。１箇所だけ
		private EntityManager entityManager;
		
		
		
		// historiesテーブルから select * from histories where bookid = ?  order by id desk limit 1; で探すと　最後の貸し出し履歴が取れる
		/**
		 * historiesテーブルから
		 * 本のbookidで絞り込んで検索し、主キーidで逆にソートし １つだけ取得する 
		 * 一番最近の貸し出し履歴のデータHistoryエンティティの実体を取得する
		 * // query.getResultList()で取得したデータは List<Object[]>になってます  List<エンティティ> にキャストもできる 
		 * Iterable にキャストもできる
		 * @return List<Object[]>
		 */
		public List<Object[]> getLastHistoryData(int bookId) {
			// historiesテーブルから 本のbookidで絞り込んで検索 ソートして １つだけを取得 limit 1
			Query query = entityManager.createNativeQuery("select * from histories where bookid = ?  order by id desc limit 1");  // order by id を付けないと 順番が更新されたのが一番最後の順になってしまうのでorder byをつける
			
			query.setParameter(1, bookId);
			
			// query.getResultList()で取得したデータは List<Object[]>になってます  List<エンティティ> にキャストもできる 
			List<Object[]> Datalist = query.getResultList();
			// limit 1 だから このリストの中には 1つの要素だけ入ってる または 何も入ってない
			
			return Datalist;
		}

		/**
		 * 貸し出し履歴Historyを historiesテーブルへ新規登録する
		 * @param history
		 * @return
		 */
	public boolean add(History history) {

		Query query = entityManager.createNativeQuery("insert into histories (lenddate, returndate, bookid, memberid) values (:a, :b, :c, :d) " );
		
		// history.getLendDate()  history.getReturnDate() は java.util.Date なので java.sql.Date へ変換をしてから
		// query.setParameter の 第2引数へ渡すこと
		
		java.util.Date utilLendDate = history.getLendDate();
		java.sql.Date sqlLendDate = new java.sql.Date( utilLendDate.getTime());
		
		// history.getReturnDate() は nullの可能性ある というか null
		java.sql.Date sqlReturnDate = null;
		if(history.getReturnDate() != null) { // nullエラー対策する
			 sqlReturnDate = new java.sql.Date(history.getReturnDate().getTime());
		}
		// TemporalType.DATE  をつけないと もし sqlLendDate nullの時エラーになりますので つけること
		// TemporalType.DATE は java.sql.Dataで登録するという意味
		query.setParameter("a", sqlLendDate , TemporalType.DATE);
		 // TemporalType.DATE は java.sql.Dataで登録するという意味 
		// TemporalType.DATE  をつけないと、PostgresSQLだと nullをdate型のカラムに入れようとするとエラーになるため、これをつけてください
		query.setParameter("b", sqlReturnDate, TemporalType.DATE);  // null になってる
		query.setParameter("c", history.getBookId() );
		query.setParameter("d", history.getMemberId() );
		// insert などの更新系の時には executeUpdate()を使う
		int result = query.executeUpdate(); // 戻り値は 更新や削除をしたエンティティの数か返る
		 
		 if(result != 1) {  // insert 1件 なので 1 が返れば成功   1以外 なら失敗
			 // 失敗
			 return false; // falseを返す 失敗したら、即returnして以下の行は実行されない 引数のfalseを呼び出し元へ返す
		 }
		 // 成功してるなら
		 return true;			
	}
	
	/**
	 * 貸し出し記録を更新する 返却日を入れる
	 * @param id
	 * @return true:成功<br /> false:失敗
	 */
	public boolean update(int id) {
		
		 Query query = entityManager.createNativeQuery("update histories set returndate = ? where id = ? ");
		 // 本日が返却日なので java.sql.Dateで本日を取得
		 java.util.Date Utiltoday = new java.util.Date();
		 java.sql.Date Sqltoday = new java.sql.Date(Utiltoday.getTime());
		 
		 query.setParameter(1, Sqltoday);
		 query.setParameter(2, id);
		 
		 int result = query.executeUpdate();  // 更新をした件数が返る
			if (result != 1) { // 失敗
				return false; // 失敗したら false が返る
				// ここでreturnしたら このメソッドは即終了するので以降は実行されない 引数の falseを呼び出し元へ返す
			}
			return true;  // 成功		 
	}
	
	
	
	/**
	 * 貸出記録を AND検索 完全一致検索
	 * 引数は nullの可能性もあるので int じゃなくて Integer
	 * @param bookId
	 * @param memberId
	 * @param count
	 * @return
	 */
	 public List<Object[]> searchHistoryAND(Integer bookId, Integer memberId, Integer count) {
		 
		 StringBuilder sql = new StringBuilder();
		 
		// 注意！！　JPQL文ですので、Memberはエンティティです なので大文字から始める
	    	//sql.append("SELECT m From Member m WHERE ");  
	    	sql.append("SELECT m From Member as m WHERE ");  // JPQLの文なので Member はエンティティを示す
	    	 boolean bookIdFlg = false;
	    	 boolean memberIdFlg= false;
	    	 boolean countFlg= false;	    	
	    	    	    	 
	    	 boolean andFlg= false;
		 
	    	 // 最近の記録履歴から取得するので order by id desc とする
	    	 // count には 10  20  30 または null が入ってくる limit 10 などとする nullだったら、 limit句がつかないで全部を取得する
	    	 if(bookId != null) {
	    		 // m.bookid   m.bookidと全て小文字にする    プレースホルダーの :bookId はこれでいい
		    	 sql.append("m.bookid LIKE :bookId");  // bookIdフィールドは PostgreSQLではbookid 全て小文字で書く
		    	 bookIdFlg = true;
		    	 andFlg= true;
		    	 }
	    	 
	    	 if(memberId != null) {
	    		   if (andFlg) sql.append(" AND ");
	    		// m.memberid   m.memberidと全て小文字にする    プレースホルダーの :memberId はこれでいい
		    	   sql.append("m.memberid LIKE :memberId");
		    	   memberIdFlg = true;
		    	   andFlg = true;
		    	 }
	    	 
	    	 if(count != null) {
	    		 if (andFlg) sql.append(" AND ");
	    		 sql.append("order by desc limit :count"); // :count は プレースホルダー
	    		 
	    		 countFlg = true;
	    		 andFlg = true;
	    	 }
	    	 
	    	 Query query = entityManager.createQuery(sql.toString());
				if (bookIdFlg) query.setParameter("bookid",  bookId );  // 完全一致検索なので 第二引数は "%" + bookId + "%" では無い
				 if (memberIdFlg) query.setParameter("memberid", memberId );  // 完全一致検索
				 if (countFlg) query.setParameter("count",  count ); 
				 
				 return query.getResultList();
					// query.getResultList()で取得したデータは List<Object[]>になってます
	 }
	

}
