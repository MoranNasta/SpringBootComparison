package com.example.demo.grpc.client;

import com.example.demo.grpc.client.model.User;
import grpc.greetings.Greetings;
import grpc.greetings.SayWelcomeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		int iterations=500000;

		ManagedChannel managedChannel =
				ManagedChannelBuilder.forAddress("localhost", 9092).usePlaintext().build();

		SayWelcomeServiceGrpc.SayWelcomeServiceBlockingStub greeterBlockingStub = SayWelcomeServiceGrpc.newBlockingStub(managedChannel);


		User user = new User("moran", "nasta");
		Greetings.User request = Greetings.User.newBuilder()
				.setFirstName(user.getFirstName())
				.setLastName(user.getLastName())
				.build();

		Long timeBeforeGrpc = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {
			Greetings.WelcomeMessage reply = greeterBlockingStub.sayHelloToUser(request);
			System.out.println(reply);
		}

		Long timeAfterGrpc = System.currentTimeMillis();

		final String uri = "http://localhost:9095/hello/moran/nasta";

		RestTemplate restTemplate = new RestTemplate();

		Long timeBeforeHttp = System.currentTimeMillis();

		for (int i = 0; i < iterations; i++) {
			String result = restTemplate.getForObject(uri, String.class);

			System.out.println(result);
		}

		Long timeAfterHttp = System.currentTimeMillis();

		System.out.println("time for grpc: "+(timeAfterGrpc-timeBeforeGrpc));
		System.out.println("time for http: "+(timeAfterHttp-timeBeforeHttp));
	}


}
