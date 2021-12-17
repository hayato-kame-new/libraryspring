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
	 * 
	 * @param bookId
	 * @param memberId
	 * @param count  全てを選ぶと nullが入ってくる
	 * @return
	 */
	 public List<Object[]> searchHistoryAND(Integer bookId, Integer memberId, Integer count) {
		 
		 StringBuilder sql = new StringBuilder();
		 
		// 注意！！　JPQL文ですので、Historyはエンティティです なので大文字から始める
	    	//sql.append("SELECT m From History m WHERE ");  
	    	sql.append("SELECT h From History as h WHERE ");  // JPQLの文なので Member はエンティティを示す
	    	sql.append(" ");  // 一応半角空白を明示的に入れておくと ミスが防げる
	    	boolean bookIdFlg = false;
	    	 boolean memberIdFlg= false;
	     		    	    	    	 
	    	 boolean andFlg= false;
//	    	 JPQLの中の、 ?1 や :from などは、クエリの可変なパラメータを表現するバインド変数である。
//	    			 バインド変数の詳細は次節で解説するが、? はインデックス指定、 : はパラメータ名の文字列指定を意味する（1つのクエリの中に両者は混在できない）。
		 
	    	 // 最近の記録履歴から取得するので order by id desc とする
	    	 // count には 10  20  30 または null が入ってくる limit 10 などとする nullだったら、 limit句がつかないで全部を取得する
	    	 if(bookId != null) {
	    		 // h.bookidと全て小文字にする h.そのエンティティのプロパティーの名前  hのエイリアスが必要  プレースホルダーの :bookid 小文字で
	    		 // 完全一致検索は = イコールを使う 曖昧検索は　LIKEを使って、("isbn", "%" + isbn + "%")
		    	 sql.append("h.bookId = :bookid");  // h.そのエンティティのプロパティーの名前
		    	 bookIdFlg = true;
		    	 andFlg= true;
		    	 }
	    	 
	    	 if(memberId != null) {
	    		   if (andFlg) sql.append(" AND ");  // 前後に半角空白が必要// JPQL文 大文字小文字を明確に区別する　予約語は大文字で書く
	    		// h.memberidと全て小文字にする    プレースホルダーの :memberid 小文字で
		    	   sql.append("h.memberId = :memberid ");
		    	   memberIdFlg = true;
		    	   andFlg = true;
		    	 }
	    	 
	    	 // LIMITはsetFirstResultやsetMaxResultsで複数結果の取得開始件数や取得件数を指定することができる。 
	    	 // これらのメソッドを実行すると、SQLにlimitやoffsetなどのデータベースにあわせた件数指定が付与される。
	    	 
	    	 // だから、ここでは countが null　でも nullじゃなくても、
	    	
	    		 sql.append(" ");  // 半角を入れないとできないので 明示的に入れとくといい
	    		 sql.append(" ORDER BY h.id DESC "); 	    		 
	    	 
	    		//  SELECT h From History as h WHERE  h.bookId = :bookid AND h.memberId = :memberid   ORDER BY h.id DESC 
	    		 
	    		 // リレーションのついた子テーブルだから,createQueryではできないのかも
	    		 // createNativeQueryで 作り直してやる 
	    	 Query query = entityManager.createQuery(sql.toString());
				if (bookIdFlg) query.setParameter("bookid",  bookId );  // 完全一致検索なので 第二引数は "%" + bookId + "%" では無い
				 if (memberIdFlg) query.setParameter("memberid", memberId );  // 完全一致検索
				  // 取得開始位置や件数の指定。
				 query.setFirstResult(1).setMaxResults(count);
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


}
