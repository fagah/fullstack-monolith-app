package mobidoc.ci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MobiDocApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobiDocApplication.class, args);
    }
}