package com.heim.api.hazelcast.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.heim.api.drivers.domain.entity.Driver;
import com.heim.api.drivers.infraestructure.repository.DriverRepository;
import com.heim.api.hazelcast.application.dto.GeoLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HazelcastGeoService {
    private static final double EARTH_RADIUS_KM = 6371;
    private final IMap<Long, GeoLocation> driverLocations;
    public DriverRepository driverRepository;


    public HazelcastGeoService(HazelcastInstance hazelcastInstance,
                               DriverRepository driverRepository){
        this.driverLocations = hazelcastInstance.getMap("driver-locations");
        this.driverRepository = driverRepository;
    }

    public void updateDriverLocation(Long driverId, double latitude, double longitude) {
        try {
            GeoLocation currentLocation = driverLocations.get(driverId);

            if (currentLocation != null && currentLocation.getLatitude() == latitude
                    && currentLocation.getLongitude() == longitude) {
                HazelcastGeoService.log.info("🚀 Ubicación sin cambios para el conductor {}", driverId);
                return; // Evita actualizaciones innecesarias
            }

            driverLocations.put(driverId, new GeoLocation(latitude, longitude));
            HazelcastGeoService.log.info("✅ Ubicación del conductor {} actualizada a ({}, {})", driverId, latitude, longitude);
        } catch (Exception e) {
            HazelcastGeoService.log.error("⚠️ Error al actualizar ubicación del conductor {}: {}", driverId, e.getMessage());
        }
    }

    public void removeDriverLocation(Long driverId) {
        driverLocations.remove(driverId);
        HazelcastGeoService.log.info("❌ Conductor {} eliminado del mapa", driverId);
    }

    public List<Long> findNearbyDriversDynamically(double latitude, double longitude) {
        log.info("📌 Conductores registrados en Hazelcast: {}", driverLocations.size());
        double radiusKm = 0.01;
        log.info("📡 Buscando conductores desde {} km", radiusKm);
        int minDrivers = 3;
        double maxRadius = 8.0; // Radio máximo permitido
        List<Long> drivers = findNearbyDrivers(latitude, longitude, radiusKm);

        // Aumentar el radio hasta encontrar suficientes conductores o alcanzar el máximo permitido
        while (drivers.size() < minDrivers && radiusKm < maxRadius) {
            radiusKm += 5; // Incremento en pasos de 5 km
            drivers = findNearbyDrivers(latitude, longitude, radiusKm);
        }
        return drivers;
    }


    public List<Long> findNearbyDrivers(double latitude, double longitude, double radiusKm) {
        // Primero obtener todos los conductores conectados en una sola consulta
        Set<Long> connectedDrivers = new HashSet<>(driverRepository.findConnectedDriverIds());


        List<Long> drivers = driverLocations.entrySet().stream()
                .filter(entry -> connectedDrivers.contains(entry.getKey()))
                .filter(entry -> {
                    GeoLocation loc = entry.getValue();
                    double dist = distance(loc, latitude, longitude);
                    log.debug("Driver {} distance: {} km", entry.getKey(), dist);
                    return dist <= radiusKm;
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        log.info("🚗 Found {} drivers within {} km radius", drivers.size(), radiusKm);
        return drivers;
    }


    private boolean isDriverConnected(Long driverId) {
        Driver driver = driverRepository.findById(driverId).orElse(null);
        log.info("🔍 Verificando estado del conductor {}: {}", driverId, driver != null ? driver.getStatus() : "No encontrado");
        return false;
    }



    private boolean isWithinBoundingBox(GeoLocation location, double latitude, double longitude, double radiusKm) {
        double latDiff = Math.abs(location.getLatitude() - latitude);
        double lonDiff = Math.abs(location.getLongitude() - longitude);
        boolean within = latDiff <= radiusKm / 111 && lonDiff <= radiusKm / 111;
        log.info("🗺️ Conductor en ({}, {}) -> Bounding Box: {}, Dentro: {}",
                location.getLatitude(), location.getLongitude(), radiusKm, within);
        return within;
    }


    private double distance(GeoLocation location, double latitude, double longitude) {
        double dLat = Math.toRadians(latitude - location.getLatitude());
        double dLon = Math.toRadians(longitude - location.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(location.getLatitude())) * Math.cos(Math.toRadians(latitude)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double distanceKm =  EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        log.info("📏 Distancia entre ({}, {}) y ({}, {}): {} km",
                latitude, longitude, location.getLatitude(), location.getLongitude(), distanceKm);
        return distanceKm;
    }

    public  GeoLocation getDriverLocation(Long driverId){
        GeoLocation location = driverLocations.get(driverId);

        if (location != null){
            log.info("📍 Ubicación encontrada para el conductor {}: ({}, {})", driverId, location.getLatitude(), location.getLongitude());
        }else {
            log.warn("⚠️ No se encontró ubicación para el conductor {}", driverId);
        }
        return  location;
    }
}
