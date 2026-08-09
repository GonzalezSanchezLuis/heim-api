package com.heim.api.move.application.service;

import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.domain.enums.MoveStatus;
import com.heim.api.move.infraestructure.repository.MoveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledMoveActivatorService {

    private final MoveRepository moveRepository;
    private final MoveService moveService;

    @Scheduled(fixedRate = 60000)
    public void activateScheduledMoves() {
        LocalDateTime now = LocalDateTime.now();
        List<Move> readyMoves = moveRepository.findScheduledMovesReady(
                List.of(MoveStatus.SCHEDULED, MoveStatus.REQUESTED), now.plusMinutes(30), 6, now.minusSeconds(50)
        );

        if (readyMoves.isEmpty()) return;

        log.info("⏰ {} viaje(s) programado(s) listo(s) para activar", readyMoves.size());
        for (Move move : readyMoves) {
            try {
                moveService.activateScheduledMove(move);
            } catch (Exception e) {
                log.error("❌ Error al activar viaje programado {}: {}", move.getMoveId(), e.getMessage());
            }
        }
    }
}
