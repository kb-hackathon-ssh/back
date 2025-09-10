package kb.hackathon.ssh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SshApplication {

	public static void main(String[] args) {
		SpringApplication.run(SshApplication.class, args);
	}

}
