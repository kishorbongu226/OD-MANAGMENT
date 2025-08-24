package org.studyeasy.SpringRestdemo.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;


@Configuration
@OpenAPIDefinition(
  info =@Info(
    title = "Demo API",
    version = "Verions 1.0",
    contact = @Contact(
      name = "Od Management", email = "bongukishor320@gmail.com", url = "https://od-management.org"
    ),
    license = @License(
      name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
    ),
    termsOfService = "https://od-management.org/",
    description = "Spring Boot RestFul API Demo by Kishor"
  )
)
public class SwaggerConfig {
}
