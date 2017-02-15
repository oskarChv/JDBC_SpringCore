package com.blog.JDBC_example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application implements CommandLineRunner {
	 private static final Logger log = LoggerFactory.getLogger(Application.class);

	    public static void main(String args[]) {
	        SpringApplication.run(Application.class, args);
	    }
	    @Autowired
	    JdbcTemplate jdbc;

		@Override
		public void run(String... arg0) throws Exception {
			log.info("<- Creating tables ->\n");
			jdbc.execute("DROP TABLE customers IF EXISTS;");
			jdbc.execute(
					"CREATE TABLE customers("+
							"id SERIAL, firstName VARCHAR(255), lastName VARCHAR(255)"
							+")"
						 );
			
			//Split up the array of whole names into an array of first/last names
			List <Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "longa Bloch", "Nisa Long").stream()
	                .map(name -> name.split(" "))
	                .collect(Collectors.toList());
			
			// Use a Java 8 stream to print out each tuple of the list
	        splitUpNames.forEach(name -> log.info(String.format("-> Inserting customer record for %s, %s", name[0], name[1])));
	        
	        jdbc.batchUpdate("INSERT INTO customers (firstName, lastName) "
	        				+"VALUES (?,?)",splitUpNames);
	        

	        log.info("Querying for customer records where firstName = 'Nisa':");
	        jdbc.query("SELECT id, firstName, lastName "
	        				+"FROM customers "
	        				+"WHERE firstName = ?", new Object[]{"Nisa"},
	        				(rs,rowNum)-> new Customer(rs.getLong("id"), rs.getString("firstName"), rs.getString("lastName"))
	        				).forEach(customer -> log.info(customer.toString()));					
		}
}