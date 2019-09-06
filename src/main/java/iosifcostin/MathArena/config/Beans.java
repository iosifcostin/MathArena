package iosifcostin.MathArena.config;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import iosifcostin.MathArena.mathMl.MathMlToPng;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Beans {

    @Bean
    public MathMlToPng mathMlToPng() {
        return new MathMlToPng();
    }

    @Bean
    public AmazonS3 amazonS3 (){
        return new AmazonS3Client();
    }



}
