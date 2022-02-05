package ac.ou.tm470.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "ac.ou.tm470.user.config",
        "ac.ou.tm470.user.service",
        "ac.ou.tm470.user.logic",
        "ac.ou.tm470.user.rest",
        "ac.ou.tm470.user.utils"
})
@EnableJpaRepositories("ac.ou.tm470.user.persistence")
@EnableEurekaClient
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
