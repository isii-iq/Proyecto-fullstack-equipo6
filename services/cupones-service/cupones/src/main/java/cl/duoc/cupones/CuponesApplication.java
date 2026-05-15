package cl.duoc.cupones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CuponesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CuponesApplication.class, args);
	}

}
