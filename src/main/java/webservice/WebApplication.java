package webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import qa.QASystem;
import qa.QASystemImpl;

@SpringBootApplication
public class WebApplication {
	
	@Bean
	public QASystem createSystem() {
		/*
		 * This is an Example QA System providing a static response. 
		 * Implement your System as a QASystem and create it here
		 * 
		 * CREATE YOUR SYSTEM HERE 
		 */
		return new QASystemImpl();
	}
	
	public static void main(final String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}
}
