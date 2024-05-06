package com.onlinereservationapplication.admin;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("api/v1")
public class AppController
{
    private final BusRouteRepository busRouteRepository;
    private final BusInventoryRepository busInventoryRepository;
    WebClient.Builder webClientBuilder;


    AppController(BusRouteRepository busRouteRepository,
                  WebClient.Builder webClientBuilder,
                  BusInventoryRepository busInventoryRepository)
    {
        this.busRouteRepository = busRouteRepository;
        this.webClientBuilder = webClientBuilder;
        this.busInventoryRepository = busInventoryRepository;
    }

    @GetMapping("fetch/all/busroutes")
    public ResponseEntity<List<Busroute>> fetchAllBookings()
    {
        return ResponseEntity.ok(busRouteRepository.findAll());
    }

    @PostMapping("add/busRoute")
    public ResponseEntity<String> addBusRoute(@RequestBody BusRouteWithSeats busRouteWithSeats)
    {
        // While adding busRoute it is to must add busInventory (i.e.,
        // available seats - as this is the first the time a master record gets created
        // while master record is being created, the available seats also should be updated

        Busroute busRoute = busRouteWithSeats.getBusRoute();
        busRoute.setId(String.valueOf(UUID.randomUUID()));
        busRoute.setBusNumber(busRoute.getBusNumber());
        busRoute.setSource(busRoute.getSource());
        busRoute.setDestination(busRoute.getDestination());
        busRoute.setPrice(busRoute.getPrice());
        busRouteRepository.save(busRoute);

        // forward request to bus inventory for creating a record
        // add/busInventory/{busNumber}/{availableSeats}
        int availableSeats = busRouteWithSeats.getAvailableSeats();
        String URL = "http://localhost:8088/api/v1/add/busInventory";
        // Get the current date and format it as a string
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Make POST request with JSON body
        String requestBody = "{\"busNumber\": \"" + busRoute.getBusNumber() + "\", \"availableSeats\": " + availableSeats + ", \"lastUpdatedDate\": \"" + formattedDate + "\"}";

        String resInventoryService = webClientBuilder.build()
                .post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block(); //SYNCHRONOUS REQUEST

        return ResponseEntity.ok("bus Route added successfully" + resInventoryService );
    }

    @PostMapping("edit/busRoute")
    public ResponseEntity<String> editBusRoute(@RequestBody BusRouteWithSeats busRouteWithSeats)
    {
        // While editing busRoute it is to must edit busInventory (i.e.,
        // available seats - as this might impact the master record and available seats


        Busroute busRoute = busRouteWithSeats.getBusRoute();
        Busroute fetchedBusRoute = busRouteRepository.findByBusNumber(busRoute.getBusNumber());

        fetchedBusRoute.setSource(busRoute.getSource());
        fetchedBusRoute.setDestination(busRoute.getDestination());
        fetchedBusRoute.setPrice(busRoute.getPrice());
        busRouteRepository.save(fetchedBusRoute);

        // forward request to bus inventory for creating a record
        // add/busInventory/{busNumber}/{availableSeats}
        int availableSeats = busRouteWithSeats.getAvailableSeats();
        String URL = "http://localhost:8088/api/v1/edit/busInventory/"
                + busRoute.getBusNumber()
                + "/" + availableSeats;
        String resInventoryService = webClientBuilder.build()
                .post()
                .uri(URL)
                .retrieve()
                .bodyToMono(String.class)
                .block(); //SYNCHRONOUS REQUEST

        return ResponseEntity.ok("bus Route updated successfully" + resInventoryService );
    }

    @PostMapping("delete/busRoute/{busNumber}")
    public ResponseEntity<String> deleteBusRoute(@PathVariable String busNumber)
    {
        // While deleting the master Record of busRoute it is mandatory to
        // delete the record on busInventory table

        // first delete the busInventory table record

        String URL = "http://localhost:8088/api/v1/delete/busInventory/" + busNumber;
        String resInventoryService = webClientBuilder.build()
                .post()
                .uri(URL)
                .retrieve()
                .bodyToMono(String.class)
                .block(); //SYNCHRONOUS REQUEST

        // after deleting the busInventory record;
        // delete the busRoute record

        Busroute busRoute = busRouteRepository.findByBusNumber(busNumber);
        // Check if busRoute exists
        if (busRoute != null) {
            // Delete the BusInventory
            busRouteRepository.delete(busRoute);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("BusRoute with bus number \"" + busNumber + "\" deleted successfully");
        }
        else
        {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("BusRoute with bus number \"" + busNumber + "\" Not found");
        }
    }
}
