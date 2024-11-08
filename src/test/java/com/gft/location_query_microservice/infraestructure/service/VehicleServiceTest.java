package com.gft.location_query_microservice.infraestructure.service;

import com.gft.location_query_microservice.infraestructure.model.aggregates.Vehicle;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class VehicleServiceTest {

    private MockWebServer mockWebServer;
    private VehicleService vehicleService;
    private WebClient webClient;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.create(mockWebServer.url("/").toString());

        vehicleService = new VehicleService(webClient,
                mockWebServer.url("/").toString(),
                "/vehicles/{id}");
    }

    @Test
    @DisplayName("When fetching a route by ID, then the correct route is returned")
    void testGetRouteById() {
        String routeJson = """
          {
                    "_id": "672caeb97e634e3fe18ada37",
                    "licensePlate": "AAA",
                    "capacity": 40,
                    "currentStatus": "IN_SERVICE",
                    "type": "BUS",
                    "driver": {
                      "driverId": "64f10c5a89d45e1a2b63e2ac",
                      "name": "John Doe",
                      "contact": {
                        "email": "johndoe@example.com",
                        "phone": "+1234567890"
                      }
                    },
                    "maintenanceDetails": {
                      "scheduledBy": "Jane Smith",
                      "scheduledDate": "2024-12-01",
                      "details": "Routine checkup"
                    },
                    "currentLocation": {
                      "latitude": 37.7749,
                      "longitude": -122.4194
                    },
                    "lastMaintenance": "2024-10-15",
                    "routeId": "64f10c9e89d45e1a2b63e2bd"
                  }
        """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(routeJson)
                .addHeader("Content-Type", "application/json"));

        Mono<Vehicle> vehicleMono = vehicleService.getVehicleById("1");
        Vehicle vehicle = vehicleMono.block();

        assertNotNull(vehicle);
        assertEquals("AAA", vehicle.getLicensePlate());
    }

    @Test
    @DisplayName("When fetching a non-existent route by ID, then an empty result is returned")
    void testGetRouteByIdNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
                .addHeader("Content-Type", "application/json"));

        Mono<Vehicle> routeMono = vehicleService.getVehicleById("999");
        Vehicle vehicle = routeMono.block();

        assertNull(vehicle);
    }

    @Test
    @DisplayName("When fetching a route by ID and a server error occurs, then an empty result is returned")
    void testGetRouteByIdServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "application/json"));

        Mono<Vehicle> routeMono = vehicleService.getVehicleById("1");
        Vehicle vehicle = routeMono.block();

        assertNull(vehicle);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }
}