package dev.camp.MessageApp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@EnableConfigurationProperties(DataSourceProperties.class)
public class MessageAppApplication implements CommandLineRunner {
	private final DatabaseConfiguration configuration;
	public MessageAppApplication(DatabaseConfiguration configuration)
	{
		this.configuration = configuration;
	}

	public static void main(String[] args) {
		SpringApplication.run(MessageAppApplication.class, args);
	}

	@Override
	public void run(String... args){
		Logger logger = Logger.getLogger(MessageAppApplication.class.getName());
		logger.log(Level.INFO, "------------------------------------");
		logger.log(Level.INFO, "Config Properties:");
		logger.log(Level.INFO, "   username is {0}", configuration.getUsername());
		logger.log(Level.INFO, "   password is {0}", configuration.getPassword());
		logger.log(Level.INFO, "------------------------------------");
	}
}