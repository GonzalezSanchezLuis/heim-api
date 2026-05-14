package com.heim.api.payment.application.service;

import com.heim.api.drivers.domain.entity.Driver;
import com.heim.api.drivers.infraestructure.repository.DriverRepository;
import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.infraestructure.repository.MoveRepository;
import com.heim.api.payment.application.dto.EarningsDTO;
import com.heim.api.payment.domain.DriverBalance;
import com.heim.api.payment.domain.Earning;
import com.heim.api.payment.infraestructure.repository.DriverBalanceRepository;
import com.heim.api.payment.infraestructure.repository.EarningRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;


@Service
public class EarningService {
    private static final Logger logger = LoggerFactory.getLogger(EarningService.class);
    private static final int CUTOFF_HOUR = 16;
    private final EarningRepository earningRepository;
    private final DriverBalanceRepository driverBalanceRepository;
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.5");
    private final DriverRepository driverRepository;



    @Autowired
    EarningService(EarningRepository earningRepository,
                   DriverBalanceRepository driverBalanceRepository,
                   DriverRepository driverRepository
                   ){
        this.earningRepository = earningRepository;
        this.driverBalanceRepository = driverBalanceRepository;
        this.driverRepository = driverRepository;
    }

    public EarningsDTO getDriverEarnings(Long userId){
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado para el usuario: " + userId));
        Integer remainingMoves = driver.getPromotionalMovesLeft();
        Long actualDriverId = driver.getId();

        BigDecimal pendingBalance = earningRepository.sumPendingBalanceByDriverId(userId);

        DriverBalance driverBalance = driverBalanceRepository.findByDriverId(userId)
                .orElseGet(() -> {
                    DriverBalance newBalance = new DriverBalance();
                    newBalance.setDriverId(userId);
                    return driverBalanceRepository.save(newBalance);
                });

        BigDecimal availableBalance = driverBalance.getAvailableBalance();
        LocalDate nextPaymentDate = calculateNextPaymentDate();

        LocalDateTime lastSettlementDate = earningRepository.findLastSettlementDateByDriverId(userId);
        LocalDate lastPaymentDate = lastSettlementDate != null ? lastSettlementDate.toLocalDate() : null;


        EarningsDTO earningsDTO = new EarningsDTO(
                userId,
                availableBalance,
                pendingBalance,
                lastPaymentDate,
                nextPaymentDate,
                remainingMoves
        );

        logger.info("DATOS DE LA BILLETERA: Disponible={}, Pendiente={}, Próx. Pago={}",
                availableBalance, pendingBalance, nextPaymentDate);

        return  earningsDTO;
    }

    @Transactional
    public Earning createPendingEarning(Move move){
        var driver = move.getDriver();
        Integer promos = driver.getPromotionalMovesLeft();
        BigDecimal appliedRate;

        if (promos != null && promos > 0){
            driver.setPromotionalMovesLeft(driver.getPromotionalMovesLeft() - 1);
            driverRepository.save(driver);

            logger.info("PROMO: Viaje gratuito aplicado. Le quedan: {}", driver.getPromotionalMovesLeft());
        }else {
            appliedRate = COMMISSION_RATE;
        }


        BigDecimal driverShare = BigDecimal.ONE.subtract(COMMISSION_RATE);
        BigDecimal netAmount = move.getPrice().multiply(driverShare);

        Earning earning = new Earning();
        earning.setMove(move);
        earning.setDriver(move.getDriver());
        earning.setNetAmount(netAmount);
        earning.setSettled(false);
        earning.setCreationDate(LocalDateTime.now());

        return earningRepository.save(earning);

    }

    private LocalDate calculateNextPaymentDate(){
        LocalDateTime now = LocalDateTime.now();
        LocalDate nextFriday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).toLocalDate();

        if (now.getDayOfWeek() == DayOfWeek.FRIDAY && now.getHour() >= CUTOFF_HOUR){
            nextFriday = nextFriday.plusWeeks(1);
        }else  if (now.getDayOfWeek().getValue() > DayOfWeek.FRIDAY.getValue()){
            nextFriday = now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).toLocalDate();
        }
        return  nextFriday;
    }
}
