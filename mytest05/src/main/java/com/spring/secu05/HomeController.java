package com.spring.secu05;

import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class HomeController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@RequestMapping(value = "/home.do", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("홈페이지");

		return "home";
	}

	@RequestMapping(value = "/intro/intro.do", method = RequestMethod.GET)
	public String introduction(Locale locale, Model model) {
		logger.info("소개 페이지");

		// 시큐리티 컨텍스트 객체를 얻습니다.
		SecurityContext context = SecurityContextHolder.getContext();

		// 인증 객체를 얻습니다.
		Authentication authentication = context.getAuthentication();
		
		// 로그인한 사용자정보를 가진 객체를 얻습니다.
		UserDetails principal = (UserDetails)authentication.getPrincipal();
		String username = principal.getUsername();
		String password = principal.getPassword();
		System.out.println("username: " + username + ", password: " + password);
		
		// 사용자가 가진 모든 롤 정보를 얻습니다.
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iter = authorities.iterator();
		while (iter.hasNext()) {
			GrantedAuthority auth = iter.next();
			System.out.println("권한: " + auth.getAuthority());
		}

		return "intro/intro";
	}
}
