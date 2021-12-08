package com.kame.springboot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kame.springboot.entity.Member;

@Repository  // リポジトリもコンポーネントです
public interface MemberRepository extends JpaRepository<Member, Integer>{

	// インタフェースなので 宣言だけ 抽象メソッド abstract メソッド本体{}書かない
	public Page<Member> findAll(Pageable pageable);
}
