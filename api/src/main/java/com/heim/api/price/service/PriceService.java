package com.heim.api.price.service;

import com.heim.api.price.application.dto.PriceRequest;
import com.heim.api.price.application.dto.PriceResponse;
import com.heim.api.price.domain.MoveType;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PriceService {

    @Value("${google.maps.api-key}")
    private String apikey;

    @Value("${google.maps.api-key-directions}")
    private String apikeyDirections;

    private final RestTemplate restTemplate;

    @Autowired
    public PriceService(@Lazy RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PriceResponse calculatePrice(PriceRequest priceRequest) {
        try {
            String encodedOrigin = priceRequest.getOriginLat() + "," + priceRequest.getOriginLng();
            String encodedDestination = priceRequest.getDestinationLat() + "," + priceRequest.getDestinationLng();

            log.info("OBJETO DE PRICE REQUEST{}", priceRequest);

            String url = "https://maps.googleapis.com/maps/api/distancematrix/json" +
                    "?origins=" + encodedOrigin +
                    "&destinations=" + encodedDestination +
                    "&key=" + apikey;

            //   System.out.println("URL ENVIADA A GOOGLE MAPS API: " + url);

            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response);
            System.out.println("Respuesta de Google Maps API: " + response);

            if (!json.has("rows") || json.getJSONArray("rows").isEmpty()) {
                throw new RuntimeException("Respuesta de Google Maps no contiene 'rows'");
            }

            JSONObject element = json.getJSONArray("rows")
                    .getJSONObject(0)
                    .getJSONArray("elements")
                    .getJSONObject(0);

            if (!element.has("distance") || !element.has("duration")) {
                throw new RuntimeException("Google Maps API no devolvió distancia o duración");
            }

            double distanceKm = element.getJSONObject("distance").getDouble("value") / 1000.0;
            double timeMin = element.getJSONObject("duration").getDouble("value") / 60.0;


            MoveType moveType = priceRequest.getTypeOfMove();
            String accessType = priceRequest.getAccessType();
            double calculatedPrice = calculatePriceByMoveType(moveType, distanceKm, timeMin, accessType);
            BigDecimal rawPrice = BigDecimal.valueOf(calculatedPrice);
            BigDecimal roundedPrice = rawPrice.divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

            String directionsUrl = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=" + encodedOrigin +
                    "&destination=" + encodedDestination +
                    "&key=" + apikeyDirections;

            //   System.out.println("URL ENVIADA A GOOGLE DIRECTIONS API: " + directionsUrl);

            String directionsResponse = restTemplate.getForObject(directionsUrl, String.class);
            JSONObject directionsJson = new JSONObject(directionsResponse);
            //  System.out.println("Respuesta de Google Directions API: " + directionsResponse);

            List<Map<String, Double>> route = new ArrayList<>();

            if (directionsJson.has("routes") && !directionsJson.getJSONArray("routes").isEmpty()) {
                JSONObject firstRoute = directionsJson.getJSONArray("routes").getJSONObject(0);

                if (firstRoute.has("legs") && !firstRoute.getJSONArray("legs").isEmpty()) {
                    JSONObject firstLeg = firstRoute.getJSONArray("legs").getJSONObject(0);

                    if (firstLeg.has("steps") && !firstLeg.getJSONArray("steps").isEmpty()) {
                        JSONArray steps = firstLeg.getJSONArray("steps");

                        for (int i = 0; i < steps.length(); i++) {
                            JSONObject step = steps.getJSONObject(i).getJSONObject("start_location");
                            Map<String, Double> point = new HashMap<>();
                            point.put("lat", step.getDouble("lat"));
                            point.put("lng", step.getDouble("lng"));
                            route.add(point);
                        }
                    } else {
                        System.out.println("⚠️ No se encontraron 'steps' en la respuesta de Google Directions.");
                    }
                } else {
                    System.out.println("⚠️ No se encontraron 'legs' en la respuesta de Google Directions.");
                }
            } else {
                System.out.println("⚠️ No se encontraron 'routes' en la respuesta de Google Directions.");
            }

            System.out.println("PRECIO DE LA MUDANZA" + roundedPrice);
            return new PriceResponse(roundedPrice, distanceKm, timeMin, route, accessType);

        } catch (Exception e) {
            System.out.println("Error al calcular el precio: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al calcular precio", e);
        }
    }

    private double calculatePriceByMoveType(MoveType moveType, double distanceKm, double timeMin, String accessType) {
        double rateBase;
        double costByKm;
        double costByMin;

        switch (moveType) {
            case PEQUENA:
                rateBase = 120000;
                costByKm = 1000;
                costByMin = 500;
                break;
            case MEDIANA:
                rateBase = 200000;
                costByKm = 1500;
                costByMin = 1000;
                break;
            default:
                throw new IllegalArgumentException("Tipo de mudanza no válido: " + moveType);
        }

        double subtotal = rateBase + (costByKm * distanceKm) + (costByMin * timeMin);

        if ("ESCALERAS".equalsIgnoreCase(accessType)) {
            subtotal += 30000;
        } else if ("ASCENSOR".equalsIgnoreCase(accessType)) {
            subtotal += 5000;
        } else if ("CALLE".equalsIgnoreCase(accessType)) {
            log.info("NUEVOS DATOS DE LA MUDANZA {}", subtotal);
            subtotal +=0;
        }

        return subtotal;
    }

}
