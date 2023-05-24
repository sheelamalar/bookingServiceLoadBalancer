package com.reservation.loadbalnce.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.reservation.loadbalnce.model.Booking;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
	Logger logger = LoggerFactory.getLogger(BookingController.class);
	
	@Autowired
	LoadBalancerClientFactory clientFactory;

	@Autowired
	RestTemplate restTemplate;
	
	/*
	 * CRUD - Create
	 * URL: http://localhost:8111/api/booking/book
	 */
	@PostMapping("/book")
	public Booking createBooking(@RequestBody Booking booking) {
		logger.debug("createBooking: "+booking.getBookingNumber());
		String url = getUrl("/api/booking/book");
		
		Booking bookedDetails = restTemplate.getForObject(url, Booking.class);
		return bookedDetails;
	}
	
	/*
	 * CRUD - Receive
	 * URL: http://localhost:8116/api/booking/bookings
	 */
	@GetMapping("/bookings")
	public List<Booking> getAllBookings() {
		logger.debug("getAllBookings ");
		String url = getUrl("/api/booking/bookings");
		
		List<Booking> bookings = restTemplate.getForObject(url, List.class);
		return bookings;
	}
	
	/*
	 * CRUD - Receive
	 * URL: http://localhost:8116/api/booking/booking/78
	 */
	@GetMapping("/booking/{bookingNumber}")
	public Booking getBookingById(@PathVariable(value = "bookingNumber") int bookingNumber) throws Exception {
		logger.debug("getBookingById: "+bookingNumber);
		
		String path = "/api/booking/booking/"+bookingNumber;
		String url = getUrl(path);
		
		Booking booking = restTemplate.getForObject(url, Booking.class);
		return booking;
	}
	
	/*
	 * CRUD - Update
	 * URL: http://localhost:8111/api/booking/booking/78
	 */
	@PutMapping("/booking/{bookingNumber}")
	public Booking updateBooking(@PathVariable(value = "bookingNumber") int bookingNumber, @RequestBody Booking booking) throws Exception {
		logger.debug("updateBooking: "+bookingNumber);
		String path = "/api/booking/booking/"+bookingNumber;
		String url = getUrl(path);
		
		Booking bookedDetails = restTemplate.getForObject(url, Booking.class);
		return bookedDetails;
	}
	
	public String getUrl(String path) {
		RoundRobinLoadBalancer lb = clientFactory.getInstance("BOOKINGSERVICE",RoundRobinLoadBalancer.class);		
		ServiceInstance instance = lb.choose().block().getServer();

		String host = instance.getHost();
		int port = instance.getPort();	
		
		String url = "http://"+host+":"+port+path;
		logger.debug(url);
		
		return url;
	}
}
