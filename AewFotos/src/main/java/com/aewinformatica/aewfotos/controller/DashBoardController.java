package com.aewinformatica.aewfotos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class DashBoardController {

	@GetMapping
	public String home() {
		return "Dashboard";
	}

}
