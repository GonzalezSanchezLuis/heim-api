package com.heim.api.drivers.infraestructure.repository;

import com.heim.api.drivers.domain.entity.Driver;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends CrudRepository<Driver, Long> {
    @Query("SELECT d FROM Driver d WHERE d.user.id = :userId")
    Optional<Driver> findByUserId(@Param("userId") Long userId);

    @Query("SELECT d.id FROM Driver d WHERE d.status = 'CONNECTED'")
    List<Long> findConnectedDriverIds();

    @Query("SELECT COUNT(d) > 0 FROM Driver d WHERE d.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}
