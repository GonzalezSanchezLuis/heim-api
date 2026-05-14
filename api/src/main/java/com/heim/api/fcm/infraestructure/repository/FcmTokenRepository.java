package com.heim.api.fcm.infraestructure.repository;

import com.heim.api.fcm.domain.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByToken(String token);
    Optional<FcmToken> findByOwnerIdAndOwnerType(Long ownerId, FcmToken.OwnerType ownerType);
   // List<FcmToken> findByOwnerTypeAndOwnerId(FcmToken.OwnerType ownerType, Long ownerId);
   List<FcmToken> findAllByOwnerIdAndOwnerType(Long ownerId, FcmToken.OwnerType ownerType);
    Optional<FcmToken> findByOwnerId(Long ownerId);

}
