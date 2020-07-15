package cst438.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cst438.domain.*;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Service
public class CityService {
	
	@Autowired
    private RabbitTemplate rabbitTemplate;
	
    @Autowired
    private FanoutExchange fanout;

	
	@Autowired
	private CityRepository cityRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private WeatherService weatherService;
	
	CityService(CityRepository cityRepository, WeatherService weatherService, CountryRepository countryRepository)
	{
		this.cityRepository = cityRepository;
		this.weatherService = weatherService;
		this.countryRepository = countryRepository;
	}
	
	public CityInfo getCityInfo(String cityName) {
		List<City> cities = cityRepository.findByName(cityName);
		if(cities.isEmpty())
			return null;
		City cityC = cities.get(0);
		Country country = countryRepository.findByCode(cityC.getCountryCode());
		TempAndTime tempTime = weatherService.getTempAndTime(cityName); 
		double fahrenheit = (tempTime.temp - 273.15) * 9.0/5.0 + 32.0;
		BigDecimal bd = new BigDecimal(fahrenheit).setScale(2, RoundingMode.HALF_UP);
		fahrenheit = bd.doubleValue();
		long timeZone = tempTime.timezone * 1000;
		long now = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
		TimeZone tz = TimeZone.getTimeZone("UTC");
		tz.setRawOffset((int) timeZone);
		sdf.setTimeZone(tz);
		Date date = new Date(now);
		String  formattedDate  =  sdf.format(date);
		CityInfo cityInfo = new CityInfo(cityC, country.getName(), fahrenheit, formattedDate);
		/**cityInfo.setName(city.getName());
		cityInfo.setCountryCode(city.getCountryCode());
		cityInfo.setCountryName(country.getName());
		cityInfo.setDistrict(city.getDistrict());
		cityInfo.setPopulation(city.getPopulation());**/
		cityInfo.setTemp(fahrenheit);
		cityInfo.setTime(formattedDate);
		
		return cityInfo;
	}

	public void requestReservation(String cityName, String level, String email) {
		String msg  = "{\"cityName\": \"" + cityName + 
		        "\" \"level\": \"" + level +
		        "\" \"email\": \"" + email + "\"}";
		System.out.println("Sending message:" + msg);
		rabbitTemplate.convertSendAndReceive(fanout.getName(), "",   // routing key none.
					msg);
		
	}
	
}
