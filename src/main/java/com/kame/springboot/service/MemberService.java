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

import com.kame.springboot.entity.Member;
import com.kame.springboot.repository.MemberRepository;

@Service
@Transactional  // クラスに対して記述した設定はメソッドで記述された設定で上書きされる このクラスで @Transactionalをつけて、コントローラにはつけない
public class MemberService {
	
	@Autowired
	MemberRepository memberRepository;
	
	// @PersistenceContextは一つしかつけれない コントローラなどの方につけてたら削除する
	@PersistenceContext // EntityManagerのBeanを自動的に割り当てるためのもの サービスクラスにEntityManagerを用意して使う。その他の場所には書けません。１箇所だけ
	private EntityManager entityManager;
	
	
	/**
	 * ページネーションとソートを使って 会員Member 全ての情報を取得
	 * リポジトリのメソッド自動生成機能を使う
	 * @param pageable
	 * @return Page<Book>
	 */
	public Page<Member> getAllMembers(Pageable pageable) {
		
         return memberRepository.findAll(pageable);		
    }
	
	/**
	  * 会員 新規登録
	  * @param book
	  * @return true:成功<br /> false:失敗
	  */
	 public boolean create(Member member) {
	
		 // createNativeQueryは普通のSQL文です JPQLではない  PostgreSQLは テーブル名 カラム名全て小文字にすること
		 // id が serial シリアルなので、insertする時には、値がなくていい 自動採番するカラムです
		 // Query query = entityManager.createNativeQuery("insert into members (name, tel, address) values (?, ?, ?) ");
		 //  ? のプレースホルダーでもいいし こっちでもいい
		 Query query = entityManager.createNativeQuery("insert into members (name, tel, address) values (:a, :b, :c) ");
		 
		 query.setParameter("a", member.getName());
		 query.setParameter("b", member.getTel());
		 query.setParameter("c", member.getAddress());
		 
		 int result = query.executeUpdate(); // 戻り値は 更新や削除をしたエンティティの数か返る
		 
		 if(result != 1) {  // insert 1件 なので 1 が返れば成功   1以外 なら失敗
			 // 失敗
			 return false; // falseを返す 失敗したら、即returnして以下の行は実行されない 引数のfalseを呼び出し元へ返す
		 }
		 // 成功してるなら
		 return true;		 
	 }
	 
	 /**
	  * 会員 を主キーで検索して オブジェクトを返す 
	  *  query.getResultList()で 取得したデータは List<Object[]>になってます
	  * @param id
	  * @return Member
	  */
	 public Member findMemberDataById(Integer id) {
	 
		 Query query = entityManager.createNativeQuery("select * from members where id = :id ");
		 query.setParameter("id", id);
		 // List<Object[]>   になってます
		  List<Object[]> resultDataList = query.getResultList();
		  // Iterator<Object[]> になってます
		  Iterator<Object[]> itr = resultDataList.iterator();
		  
		  String name = "";
		  String tel = "";
		  String address = "";
		  while(itr.hasNext()) {
			  Object[] obj = (Object[]) itr.next();
			  
			// id は取得しなくてもいい
			// id = Integer.parseInt(String.valueOf(obj[0]));
			  name = String.valueOf(obj[1]);  // String型に キャストするんじゃなくて メソッドを使ってString型へ変換する
			  tel = String.valueOf(obj[2]);
			  address = String.valueOf(obj[3]);
		  }
		 Member member = new Member(id, name, tel, address);
		 return member;
	 }
	 
	 /**
	  * 会員 更新 
	  * @param member
	  * @return true:成功<br /> false:失敗
	  */
	 public boolean update(Member member) {
		 Query query = entityManager.createNativeQuery("update members set (name, tel, address) = (?, ?, ?) where id = ? ");
		 
		 query.setParameter(1, member.getName());
		 query.setParameter(2, member.getTel());
		 query.setParameter(3, member.getAddress());
		 query.setParameter(4, member.getId());
		 
		 int result = query.executeUpdate();
		if (result != 1) { // 失敗
			return false; // 失敗したら false が返る
			// ここでreturnしたら このメソッドは即終了するので以降は実行されない 引数の falseを呼び出し元へ返す
		}
		return true;  // 成功
	 }

}
