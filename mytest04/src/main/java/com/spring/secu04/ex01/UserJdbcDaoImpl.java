package com.spring.secu04.ex01;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

public class UserJdbcDaoImpl extends JdbcDaoImpl {

	@Override
	protected List<UserDetails> loadUsersByUsername(String username) {
		return getJdbcTemplate().query(getUsersByUsernameQuery(), new String[] {username}, new RowMapper<UserDetails>() {
            public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            	String username = rs.getString(1);
                String password = rs.getString(2);
                int r_enabled = rs.getInt(3);
                
                boolean enabled= false; 
                if(r_enabled ==1) enabled= true;
                
                System.out.println("username: " + username+", password: " + password + "enabled: " + enabled);
                return new User(username, password, enabled, true, true, true,AuthorityUtils.NO_AUTHORITIES);            
             }
        });
	}

	@Override
	protected List<GrantedAuthority> loadUserAuthorities(String username) {
		return getJdbcTemplate().query(getAuthoritiesByUsernameQuery(), new String[] {username},
			new RowMapper<GrantedAuthority>() {

				@Override
				public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
					String roleName = getRolePrefix() + rs.getString(2);
					System.out.println("roleName: " + roleName);
					
					return new SimpleGrantedAuthority(roleName);
				}
				
			});
	}
	
	

}
