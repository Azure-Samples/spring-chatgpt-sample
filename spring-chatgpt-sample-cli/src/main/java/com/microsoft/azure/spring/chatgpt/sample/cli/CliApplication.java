package com.microsoft.azure.spring.chatgpt.sample.cli;

import com.microsoft.azure.spring.chatgpt.sample.common.DocumentIndexPlanner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
@RequiredArgsConstructor
public class CliApplication implements ApplicationRunner {

	private final DocumentIndexPlanner indexPlanner;

	public static void main(String[] args) {
		SpringApplication.run(CliApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws IOException {
		var from = args.getOptionValues("from");
		if (from == null || from.size() != 1) {
			System.err.println("argument --from is required.");
			System.exit(-1);
		}
		indexPlanner.buildFromFolder(from.get(0));
	}
}
