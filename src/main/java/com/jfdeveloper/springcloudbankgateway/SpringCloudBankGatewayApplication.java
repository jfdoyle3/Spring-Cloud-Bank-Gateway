package com.jfdeveloper.springcloudbankgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudBankGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudBankGatewayApplication.class, args);
	}

	// Simple Route
//	@Bean
//	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route(p -> p
//						.path("/get")
//						.filters(f -> f.addRequestHeader("Hello", "World"))
//						.uri("http://httpbin.org:80"))
//				.build();
//	}

    @Component
    public class AddHeaderFilter implements GatewayFilter {

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("header-name", "header-value")
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(request).build();
            return chain.filter(mutatedExchange);
        }
    }
	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()

				.route(p -> p
						.path("/get")
						.filters(f -> f.addRequestHeader("Hello", "World"))
						.uri("http://httpbin.org:80"))
				.route(p -> p
						.host("*.circuitbreaker.com")
						.filters(f -> f.circuitBreaker(config -> config.setName("mycmd")))
						.uri("http://httpbin.org:80"))
                .route("create", r -> r.path("/api/**")
                        .filters(f -> f.filter(new AddHeaderFilter()))
                        .uri("http://localhost:8080"))
				.build();
	}



}
