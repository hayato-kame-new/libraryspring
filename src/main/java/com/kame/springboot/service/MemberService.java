package com.kame.springboot.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

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
	  * 会員 新規登録する
	  * @param book
	  * @return true:成功<br /> false:失敗
	  */
	 public boolean create(Member member) {
	
		 // createNativeQueryは普通のSQL文です JPQLではない  PostgreSQLは テーブル名 カラム名全て小文字にすること
		 // id が serial シリアルなので、insertする時には、値がなくていい 自動採番するカラムです
		 // Query query = entityManager.createNativeQuery("insert into members (name, tel, address, birthday) values (?, ?, ?, ?) ");
		 //  ? のプレースホルダーでもいいし こっちでもいい
		 Query query = entityManager.createNativeQuery("insert into members (name, tel, address, birthday) values (:a, :b, :c, :d) ");
		 
		 query.setParameter("a", member.getName());
		 query.setParameter("b", member.getTel());
		 query.setParameter("c", member.getAddress());
		 
		 // java.time.LocalDate から java.sql.Date へ変換してからセットする
		 LocalDate localDate = member.getBirthDay();
		 java.sql.Date javaSqlDate = java.sql.Date.valueOf(localDate);
		 // TemporalType.DATE は java.sql.Dataで登録するという意味 
			// TemporalType.DATE  をつけないと、PostgresSQLだと nullをdate型のカラムに入れようとするとエラーになるため、これをつけてください
		 query.setParameter("d", javaSqlDate, TemporalType.DATE);
		 
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
	  * @return Member 見つからない場合は null
	  */
	 public Member findMemberDataById(Integer id) {
	 
		 Query query = entityManager.createNativeQuery("select * from members where id = :id order by id asc");
		 query.setParameter("id", id);
		 // List<Object[]>   になってます
		  List<Object[]> resultDataList = query.getResultList();
		  // Iterator<Object[]> になってます
		  // 見つからない時には []になってるので、
		  Member member = null;  // 見つからない時には []になってるので nullを代入してそれをreturnしてます
		  if(resultDataList.size() > 0) {  // 見つかったので
			  // 見つかったので Memeber型のオブジェクトに詰め替えてる
			  Iterator<Object[]> itr = resultDataList.iterator();
			  
			  String name = "";
			  String tel = "";
			  String address = "";
			  java.sql.Date javaSqlDate = null;
			  LocalDate birthday = null;
			  while(itr.hasNext()) {
				  Object[] obj = (Object[]) itr.next();
				  
				  // id は取得しなくてもいい
				  // id = Integer.parseInt(String.valueOf(obj[0]));
				  name = String.valueOf(obj[1]);  // String型に キャストするんじゃなくて メソッドを使ってString型へ変換する
				  tel = String.valueOf(obj[2]);
				  address = String.valueOf(obj[3]);
				  javaSqlDate = (Date) (obj[4]);
				  birthday = javaSqlDate.toLocalDate();
			  }
			member = new Member(id, name, tel, address, birthday);
		  }
		 return member;  // 見つからない時には nullになってます
	 }
	 
	 /**
	  * 会員 更新する
	  * @param member
	  * @return true:成功<br /> false:失敗
	  */
	 public boolean update(Member member) {
		 Query query = entityManager.createNativeQuery("update members set (name, tel, address, birthday) = (?, ?, ?, ?) where id = ? ");
		 
		 query.setParameter(1, member.getName());
		 query.setParameter(2, member.getTel());
		 query.setParameter(3, member.getAddress());
		 
		 LocalDate localDate = member.getBirthDay();
		 java.sql.Date javaSqlDate = java.sql.Date.valueOf(localDate);
		 // TemporalType.DATE は java.sql.Dataで登録するという意味 
			// TemporalType.DATE  をつけないと、PostgresSQLだと nullをdate型のカラムに入れようとするとエラーになるため、これをつけてください
		 query.setParameter(4, javaSqlDate, TemporalType.DATE);
		 query.setParameter(5, member.getId());
		 
		 int result = query.executeUpdate();
		if (result != 1) { // 失敗
			return false; // 失敗したら false が返る
			// ここでreturnしたら このメソッドは即終了するので以降は実行されない 引数の falseを呼び出し元へ返す
		}
		return true;  // 成功
	 }
	 
	 
	 //Servlet.service() for servlet [dispatcherServlet] 
	 // in context with path [] threw exception [Request processing failed; nested exception is 
	 // javax.persistence.PersistenceException: org.hibernate.exception.ConstraintViolationException: 
	 // could not execute statement] with root cause
	 /**
	  * 会員 削除する
	  *  ロールバックの注意点として、非検査例外(RuntimeException及びそのサブクラス)が発生した場合はロールバックされるが、検査例外(Exception及びそのサブクラスでRuntimeExceptionのサブクラスじゃないもの)が発生した場合はロールバックされずコミットされる
	 * RuntimeException以外の例外が発生した場合もロールバックしたいので @Transactional(rollbackFor =
	 * Exception.class)としてExceptionおよびExceptionを継承しているクラスがthrowされるとロールバックされるように設定します.
	 * 呼び出し元つまりコントローラ のメソッドでtry-catchする ここで例外処理をしてはいけない コントローラには @Transactional
	 * をつけないこと つけると、UnexpectedRollbackException発生する トランザクションをコミットしようとした結果、予期しないロールバックが発生した場合にスローされます
	 * 
	 * @Transactional(readOnly=false, rollbackFor=Exception.class) をつけること throws
	 *                                PersistenceException が必要
	  * @param id
	  * @return true:成功<br /> false:失敗
	  * @throws PersistenceException
	  */
	 @Transactional(readOnly = false, rollbackFor = Exception.class) // ここに@Transactionalをつけて、コントローラにはつけないでください
	 public boolean delete(Integer id) throws PersistenceException{// throwsして、呼び出しもとで
		 Query query = entityManager.createNativeQuery("delete from members where id = ? ");
		 
		 query.setParameter(1, id);
		 
		 int result = query.executeUpdate();
		 if(result != 1) {
			 return false;  // 失敗
		 }
		 return true;  // 成功
	 }
	 
	 /**
		 * 会員検索
		 * @param departmentId
		 * @param employeeId
		 * @param word
		 * @return List<Object[]>
		 */
		@SuppressWarnings("unchecked")
		public List<Object[]> find(String departmentId, String employeeId, String word) {
			// 注意 引数のdepartmentId は 空文字とnullの可能ある   employeeId と word は ""空文字の可能性ある
	
			String sql = "select * from employee";
			String where = ""; // where句
			int depIdIndex = 0; // プレースホルダーの位置を指定する 0だと、プレースホルダーは使用しないことになる
			int empIdIndex = 0;
			int wordIndex = 0;
	
			if (departmentId == null) {
				departmentId = "";
			}
			if (departmentId.equals("")) {
				// 未指定の時 何もしない depIdIndex 0 のまま変更無し
			} else {
				where = " where departmentid = ?"; // 代入する 注意カラム名を全て小文字にすること departmentid また、前後半角空白入れてつなぐので注意
				depIdIndex = 1; // 変更あり
			}
	
			if (employeeId.equals("")) {
				// 未指定の時 何もしない 
			} else {
				if (where.equals("")) { 
					where = " where employeeid = ?"; // 代入する カラム名を全て小文字 employeeid
					empIdIndex = 1;
				} else {
					where += " and employeeid = ?"; // where句はすでにあるので 二項演算子の加算代入演算子を使って連結 												
					empIdIndex = depIdIndex + 1;
				}
			}
	
			if (word.equals("")) {
				// 未指定の時何もしない
			} else {
				if (where.equals("")) { 
					where = " where name like ?"; // 代入  
					 wordIndex = 1;
				} else if (where.equals(" where departmentid = ?")) {
					where += " and name like ?"; // 二項演算子の加算代入演算子を使って連結 
					 wordIndex = depIdIndex + 1;
				} else if (where.equals(" where employeeid = ?")) {
					where += " and name like ?"; // 二項演算子の加算代入演算子を使って連結 
					 wordIndex = empIdIndex + 1;
				} else if (where.equals(" where departmentid = ? and employeeid = ?")) {
					where += " and name like ?"; // 二項演算子の加算代入演算子を使って連結 
					 wordIndex = depIdIndex + empIdIndex + 1;
				}
			}
	
			Query query = entityManager.createNativeQuery(sql + where);
			if (depIdIndex > 0) {
				query.setParameter(depIdIndex, departmentId);
			}
			if (empIdIndex > 0) {
				query.setParameter(empIdIndex, employeeId);
			}
			if (wordIndex > 0) {
				query.setParameter(wordIndex, "%" + word + "%");
			}
			// query.getResultList()で取得したデータは List<Object[]>になってます
			 //  Iterable にキャストもできる (List<Book>)にキャストもできる
			return query.getResultList(); // 結果リスト 型のないリストを返す 
		}

		  // idがnullじゃない時に、idで検索した本が、図書館システムに存在してるかどうかを調べる
	    public boolean exist(int id) {
	    	Query query = entityManager.createNativeQuery("select * from members where id = ? ");
	    	query.setParameter(1, id);
	    	List<Object[]> list = query.getResultList();
	    	if(list.size() > 0) {
	    		return true;
	    	}
	    	return false;
	    }
	    
	    
	    // idは完全一致検索 nameは曖昧検索 AND検索
	    
	    public List<Object[]> searchMemberAND(Integer id, String name) {
		    
	    	StringBuilder sql = new StringBuilder();
	    	
	    	// 注意！！　JPQL文ですので、Bookはエンティティです なので大文字から始める
	    	//JPQL には エイリアスが必要
	    	//sql.append("SELECT m From Member m WHERE ");  
	    	sql.append("SELECT m From Member as m WHERE ");  // JPQLの文なので Member はエンティティを示す
	    	sql.append(" ");  // 一応半角空白を明示的に入れておくと ミスが防げる 
	    	boolean idFlg = false;
	    	 boolean nameFlg= false;
	    	 	    	    	 
	    	 boolean andFlg= false;
	    	 
	    	 if(id != null) {  // id は　Integerです フォームに何も入力しないと nullできます
	    	 sql.append("m.id = :id");  // m.id とエイリアス付きに書かないといけない  完全一致検索なので イコールで
	    	 idFlg = true;
	    	 andFlg= true;
	    	 }
	    
	    	if( !"".equals(name)) {  // nameは Stringなので フォームに何も入力しないと ""空文字できます
	    	  if (andFlg) sql.append(" AND ");  // 前後に半角空白が必要です
	    	  sql.append("m.name LIKE :name");  // m.エンティティのプロパティ名 mのエイリアスは省略できません 曖昧検索なので LIKEです
	    	  nameFlg = true;
	    	  // andFlg = true;
	    	 }
	    	// JPQL だから createQueryメソッドを使う
	    	 Query query = entityManager.createQuery(sql.toString());
				if (idFlg) query.setParameter("id",  id );  // 完全一致検索なので
				 if (nameFlg) query.setParameter("name", "%" + name + "%");  // 曖昧検索なので
				
				return query.getResultList();
				// query.getResultList()で取得したデータは List<Object[]>になってます
	    }
}
