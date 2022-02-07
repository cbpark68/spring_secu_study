package com.spring.secu03.user;

import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
	@Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserDao userDao;
    
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@RequestMapping(value = "/user/userForm.do", method = RequestMethod.GET)
	public String userForm(Locale locale, Model model) {
		return "user/userForm";
	}

	@RequestMapping(value = "/user/addUser.do", method = RequestMethod.POST)
	public String addUser(@ModelAttribute UserVO userVO ) {
		String encPassword = passwordEncoder.encode(userVO.getPassword());
        userVO.setPassword(encPassword);
		userDao.insertUser(userVO);
		return "home";
	}
}
