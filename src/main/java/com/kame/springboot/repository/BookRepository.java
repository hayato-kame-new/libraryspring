package com.kame.springboot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kame.springboot.entity.Book;

@Repository  // リポジトリもコンポーネントです
public interface BookRepository extends JpaRepository<Book, Integer> {

	// インタフェースなので 宣言だけ 抽象メソッド abstract メソッド本体{}書かない
	 public Page<Book> findAll(Pageable pageable);
	
}
