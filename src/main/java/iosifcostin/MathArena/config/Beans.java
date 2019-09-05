package iosifcostin.MathArena.config;
import iosifcostin.MathArena.mathMl.MathMlToPng;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Beans {

    @Bean
    public MathMlToPng mathMlToPng() {
        return new MathMlToPng();
    }



}
