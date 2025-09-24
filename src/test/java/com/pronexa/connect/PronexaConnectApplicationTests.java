package com.pronexa.connect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pronexa.connect.services.EmailService;

@SpringBootTest
class PronexaConnectApplicationTests {

	@Test
	void contextLoads() {
	}


	@Autowired
	private EmailService service;


	@Test
	void sendEmailTest(){
		service.sendEmail("vidhanvvispute786@gmail.com",
		 "hie vidhan mi test krtoh ahe hie sejal",
		  "hie vidhan mi test krtoh ahe");

	}

}
