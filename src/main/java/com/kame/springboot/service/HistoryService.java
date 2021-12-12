package com.kame.springboot.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional  // クラスに対して記述した設定はメソッドで記述された設定で上書きされる このクラスで @Transactionalをつけて、コントローラにはつけない
public class HistoryService {
	
	// historiesテーブルは booksテーブルとリレーションがある historiesテーブルが子テーブル  booksテーブルが親テーブル
	// historiesテーブルは membersテーブルとリレーションがある historiesテーブルが子テーブル  membersテーブルが親テーブル

		
		
		// @PersistenceContextは一つしかつけれない コントローラなどの方につけてたら削除する
		@PersistenceContext // EntityManagerのBeanを自動的に割り当てるためのもの サービスクラスにEntityManagerを用意して使う。その他の場所には書けません。１箇所だけ
		private EntityManager entityManager;
		
		 

	

}
