package com.heim.api.drivers.application.service;

import com.heim.api.drivers.application.dto.DriverLocation;
import com.heim.api.drivers.application.dto.TimeAndDistanceDestinationResponse;
import com.heim.api.drivers.application.dto.TimeAndDistanceOriginResponse;
import com.heim.api.hazelcast.service.HazelcastGeoService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DistanceCalculatorService {
    private static final Logger logger = LoggerFactory.getLogger(DistanceCalculatorService.class);
    private final RestTemplate restTemplate;


    @Value("${google.maps.api-key}")
    private String apikey;

    @Autowired
    public DistanceCalculatorService(
            HazelcastGeoService hazelcastGeoService,
            RestTemplate restTemplate){
        this.restTemplate = restTemplate;

    }

    public Map<Long, TimeAndDistanceOriginResponse> calculateDistancesToUserForMultipleDrivers(
            List<DriverLocation> drivers, double userLat, double userLng) {

        Map<Long, TimeAndDistanceOriginResponse> driverDistanceMap = new HashMap<>();

        if (drivers.isEmpty()) {
            return driverDistanceMap;
        }

        // Construir la lista de ubicaciones de origen para todos los conductores
        String origins = drivers.stream()
                .map(driver -> driver.getLatitude() + "," + driver.getLongitude())
                .collect(Collectors.joining("|"));

        String destination = userLat + "," + userLng;

        try {
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json" +
                    "?origins=" + origins +
                    "&destinations=" + destination +
                    "&departure_time=now" +
                    "&key=" + apikey;

            String response = restTemplate.getForObject(url, String.class);
            JSONObject jsonObject = new JSONObject(response);
            JSONArray rows = jsonObject.getJSONArray("rows");

            for (int i = 0; i < rows.length(); i++) {
                JSONObject element = rows.getJSONObject(i)
                        .getJSONArray("elements")
                        .getJSONObject(0);

                String distance = element.getJSONObject("distance").getString("text");
                String timeMin;

                if (element.has("duration_in_traffic")) {
                    timeMin = element.getJSONObject("duration_in_traffic").getString("text");
                } else {
                    timeMin = element.getJSONObject("duration").getString("text");
                }

                TimeAndDistanceOriginResponse distanceResponse = new TimeAndDistanceOriginResponse(distance, timeMin);
               Long driverId = drivers.get(i).getDriverId();
                driverDistanceMap.put(driverId, distanceResponse);
            }

        } catch (Exception e) {
            logger.error("Error al calcular distancias y tiempos para múltiples conductores: {}", e.getMessage());
            throw new RuntimeException("Error al calcular distancias y tiempos", e);
        }

        return driverDistanceMap;
    }



    public Map<Long, TimeAndDistanceDestinationResponse> calculateDistancesToDestinationForMultipleDrivers(
            List<DriverLocation> drivers, double destinationLat, double destinationLng) {

        Map<Long, TimeAndDistanceDestinationResponse> driverDistanceMap = new HashMap<>();

        if (drivers.isEmpty()) {
            return driverDistanceMap;
        }

        // Origen: ubicación de los conductores
        String origins = drivers.stream()
                .map(driver -> driver.getLatitude() + "," + driver.getLongitude())
                .collect(Collectors.joining("|"));

        String destination = destinationLat + "," + destinationLng;

        try {
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json" +
                    "?origins=" + origins +
                    "&destinations=" + destination +
                    "&departure_time=now" +
                    "&key=" + apikey;

            String response = restTemplate.getForObject(url, String.class);
            JSONObject jsonObject = new JSONObject(response);
            JSONArray rows = jsonObject.getJSONArray("rows");

            for (int i = 0; i < rows.length(); i++) {
                JSONObject element = rows.getJSONObject(i)
                        .getJSONArray("elements")
                        .getJSONObject(0);

                String distance = element.getJSONObject("distance").getString("text");
                String timeMin;

                if (element.has("duration_in_traffic")) {
                    timeMin = element.getJSONObject("duration_in_traffic").getString("text");
                } else {
                    timeMin = element.getJSONObject("duration").getString("text");
                }

                TimeAndDistanceDestinationResponse distanceResponse = new TimeAndDistanceDestinationResponse(distance, timeMin);
                Long driverId = drivers.get(i).getDriverId();
                driverDistanceMap.put(driverId, distanceResponse);
            }

        } catch (Exception e) {
            logger.error("Error al calcular distancias y tiempos hacia el destino: {}", e.getMessage());
            throw new RuntimeException("Error al calcular distancias y tiempos hacia destino", e);
        }

        return driverDistanceMap;
    }



}
