package com.kame.springboot.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		 * @return List<Object[]>
		 */
		public List<Object[]> getOneBookHistoriesList(int bookId) {
			// historiesテーブルから 本のbookidで絞り込んで検索 ソートして １つだけを取得 limit 1
			Query query = entityManager.createNativeQuery("select * from histories where bookid = ?  order by id desk limit 1");  // order by id を付けないと 順番が更新されたのが一番最後の順になってしまうのでorder byをつける
			
			query.setParameter(1, bookId);
			
			// query.getResultList()で取得したデータは List<Object[]>になってます  List<エンティティ> にキャストもできる 
			List<Object[]> Datalist = query.getResultList();
			// limit 1 だから このリストの中には 1つの要素だけ入ってる または 何も入ってない
			
			return Datalist;
		}

	

}
