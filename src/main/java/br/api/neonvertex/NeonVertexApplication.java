package br.api.neonvertex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NeonVertexApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeonVertexApplication.class, args);
    }
}
