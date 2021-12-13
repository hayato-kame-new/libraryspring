package com.kame.springboot.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
		public List<Object[]> getOneBookHistoriesList(int bookId) {
			// historiesテーブルから 本のbookidで絞り込んで検索 ソートして １つだけを取得 limit 1
			Query query = entityManager.createNativeQuery("select * from histories where bookid = ?  order by id desc limit 1");  // order by id を付けないと 順番が更新されたのが一番最後の順になってしまうのでorder byをつける
			
			query.setParameter(1, bookId);
			
			// query.getResultList()で取得したデータは List<Object[]>になってます  List<エンティティ> にキャストもできる 
			List<Object[]> Datalist = query.getResultList();
			// limit 1 だから このリストの中には 1つの要素だけ入ってる または 何も入ってない
			
			return Datalist;
		}

	public boolean add(History history) {

		Query query = entityManager.createNativeQuery("insert into histories (lenddate, returndate, bookid, memberid) values (:a, :b, :c, :d) " );
		query.setParameter("a", history.getLendDate() );
		query.setParameter("b", null );  // 返却はしてないので null   history.getReturndate() でもいい
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

}
