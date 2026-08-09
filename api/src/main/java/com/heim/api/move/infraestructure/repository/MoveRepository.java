package com.heim.api.move.infraestructure.repository;

import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.domain.enums.MoveStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoveRepository extends CrudRepository<Move, Long> {
    Optional<Move> findByMoveIdAndDriver_Id(@Param("moveId") Long moveId, @Param("driverId") Long driverId);
    List<Move> findByDriverIdAndStatus(Long driverId, MoveStatus status);
    List<Move> findByUser_UserIdAndStatus(Long userId, MoveStatus status);
    List<Move> findByUser_UserIdAndStatusIn(Long userId, List<MoveStatus> statuses);
    @Query("SELECT m FROM Move m WHERE m.user.userId = :userId AND m.origin = :origin AND m.destination = :destination AND m.status = :status ORDER BY m.requestTime DESC LIMIT 1")
    Optional<Move> findByUser_UserIdAndOriginAndDestinationAndStatus(
            @Param("userId") Long userId,
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("status") MoveStatus status
    );
    Optional<Move> findById(Long moveId);




    @Query("""
    SELECT m FROM Move m
    WHERE m.id = :moveId
      AND m.driver.id = :driverId
      AND m.status IN (:statuses)
""")
    Optional<Move> findActiveMoveByDriverIdAndMoveId(
            @Param("moveId") Long moveId,
            @Param("driverId") Long driverId,
            @Param("statuses") List<MoveStatus> statuses
    );

    @Query("SELECT m FROM Move m WHERE m.scheduledTime IS NOT NULL AND m.status IN (:statuses) AND m.scheduledTime <= :now AND m.retryCount < :maxRetries AND (m.lastActivatedAt IS NULL OR m.lastActivatedAt <= :cooldown)")
    List<Move> findScheduledMovesReady(
            @Param("statuses") List<MoveStatus> statuses,
            @Param("now") java.time.LocalDateTime now,
            @Param("maxRetries") int maxRetries,
            @Param("cooldown") java.time.LocalDateTime cooldown
    );

    @Query("SELECT m FROM Move m WHERE m.driver.id = :driverId AND m.status IN :statuses ORDER BY m.requestTime DESC LIMIT 1")
    Optional<Move> findActiveByDriverId(
            @Param("driverId") Long driverId,
            @Param("statuses") List<MoveStatus> statuses
    );


}

