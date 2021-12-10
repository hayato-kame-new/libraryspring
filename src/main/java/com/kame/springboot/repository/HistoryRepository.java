package com.kame.springboot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kame.springboot.entity.History;

public interface HistoryRepository extends JpaRepository<History, Integer> {
	
	
	// インタフェースなので 宣言だけ 抽象メソッド abstract メソッド本体{}書かない
		 public Page<History> findAll(Pageable pageable);

}
