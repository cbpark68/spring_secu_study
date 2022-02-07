package com.spring.secu03.user;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository("userDao")
public class UserDao {
	@Autowired
	private SqlSession sqlSession;
	
	public void insertUser(UserVO userVO) throws DataAccessException{
		sqlSession.insert("mapper.user.insertUser", userVO);
	}

}


