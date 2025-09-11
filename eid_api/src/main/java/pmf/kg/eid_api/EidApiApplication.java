package pmf.kg.eid_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class EidApiApplication {

    public static void main(String[] args) {

         SpringApplication.run(EidApiApplication.class, args);
    }

}
