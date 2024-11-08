package com.gft.location_query_microservice.infraestructure.service;

import com.gft.location_query_microservice.infraestructure.model.aggregates.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class VehicleService {

    private final WebClient webClient;
    private String baseUrl;
    private String vehicleUrl;

    @Autowired
    public VehicleService(WebClient webClient,
                        @Value("${vehicles.api.base-url}") String baseUrl,
                        @Value("${vehicles.api.get-by-id}") String vehicleUrl) {
        this.webClient = webClient;
        this.vehicleUrl=vehicleUrl;
        this.baseUrl=baseUrl;
    }

    public Mono<Vehicle> getVehicleById(String idString) {
        return webClient.get()
                .uri(baseUrl + vehicleUrl, idString)
                .retrieve()
                .bodyToMono(Vehicle.class)
                .onErrorResume(error -> {
                    System.err.println("Error al llamar al servicio de vehicles: " + error.getMessage());
                    return Mono.empty();
                });
    }
}
