package ru.ls.pjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SuppressWarnings("PMD.UseUtilityClass")
@ConfigurationPropertiesScan
@EnableScheduling
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class PjwtApplication {

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public static void main(final String[] args) {
    SpringApplication.run(PjwtApplication.class, args);
  }
}
