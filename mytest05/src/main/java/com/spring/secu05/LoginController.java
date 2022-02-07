package com.spring.secu05;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping(value = "/login/loginForm.do", method = RequestMethod.GET)
	public String loginFor(Locale locale, Model model) {
		logger.info("로그인창입니다.");

		return "login/loginForm";
	}

	@RequestMapping(value = "/login/accessDenied.do", method = RequestMethod.GET)
	public String accessDenied(Locale locale, Model model) {
		logger.info("접근이 거부되었습니다.");

		return "login/accessDenied";
	}
	
/*	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public String logout(Locale locale, Model model) {
		logger.info("로그아웃");

		return "home";
	}*/
}

