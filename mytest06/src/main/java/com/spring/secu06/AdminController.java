package com.spring.secu06;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController {
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@RequestMapping(value = "/admin/adminMain.do", method = RequestMethod.GET)
	public String adminMain(Locale locale, Model model) {
		logger.info("메인 관리자창입니다.");

		return "admin/adminMain";
	}
}


