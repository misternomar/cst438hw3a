package cst438.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cst438.domain.*;
import cst438.service.CityService;
import cst438.service.WeatherService;

@Controller
public class CityController {
	
	@Autowired
	private CityService cityService;
	
	@Autowired
	private WeatherService weatherService;
	
	@GetMapping("/cities/{city}")
	public String getWeather(@PathVariable("city") String cityName, Model model) {
		CityInfo cityS = cityService.getCityInfo(cityName);
		model.addAttribute("city", cityS);
		return "index";
	}
	
	@PostMapping("/cities/reservation")
	public String createReservation(
			@RequestParam("city") String cityName, 
			@RequestParam("level") String level, 
			@RequestParam("email") String email, 
			Model model) {
		
		model.addAttribute("city", cityName);
		model.addAttribute("level", level);
		model.addAttribute("email", email);
		cityService.requestReservation(cityName, level, email);
		return "request_reservation";
	}

	
}