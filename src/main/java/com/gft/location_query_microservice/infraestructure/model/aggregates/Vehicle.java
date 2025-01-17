package com.gft.location_query_microservice.infraestructure.model.aggregates;


import com.gft.location_query_microservice.infraestructure.model.entities.Driver;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.VehicleCoordinates;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.MaintenanceDetails;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.enums.VehicleStatus;
import com.gft.location_query_microservice.infraestructure.model.valueobjects.enums.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Vehicle")
public class Vehicle {

    @Id
    private ObjectId vehicleId;

    @NotBlank(message = "License plate cannot be empty")
    private String licensePlate;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Current status is required")
    private VehicleStatus currentStatus;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @NotNull(message = "Driver is required")
    private Driver driver;

    @NotNull(message = "Maintenance details are required")
    private MaintenanceDetails maintenanceDetails;

    @NotNull(message = "Current location is required")
    private VehicleCoordinates currentLocation;

    private LocalDate lastMaintenance;

    @NotNull(message = "Route ID is required")
    private ObjectId routeId;
}
