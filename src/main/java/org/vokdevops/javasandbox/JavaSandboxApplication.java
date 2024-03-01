package org.vokdevops.javasandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaSandboxApplication implements CommandLineRunner {

	private static Logger logger = LoggerFactory
			.getLogger(JavaSandboxApplication.class);

	public static void main(String[] args) {
		logger.info("Staring: sandbox java app");
		SpringApplication.run(JavaSandboxApplication.class, args);
		logger.info("Completed: sandbox java app");
	}

	/**
	 * Overridden to enable Spring boot app to run experimental Java code
	 * @param args
	 * @throws Exception
	 */
	@Override
	public void run(String... args) throws Exception {

		// Experimental Java code goes here

	}
}
