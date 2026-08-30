package com.heim.api.move.application.service;

import com.heim.api.drivers.application.dto.DriverLocation;
import com.heim.api.drivers.application.dto.TimeAndDistanceDestinationResponse;
import com.heim.api.drivers.application.dto.TimeAndDistanceOriginResponse;
import com.heim.api.drivers.application.service.DistanceCalculatorService;
import com.heim.api.drivers.domain.entity.Driver;
import com.heim.api.drivers.infraestructure.repository.DriverRepository;
import com.heim.api.exceptions.BusinessException;
import com.heim.api.exceptions.NotFoundException;
import com.heim.api.notification.application.service.EmailNotificationService;
import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.hazelcast.application.dto.GeoLocation;
import com.heim.api.hazelcast.service.HazelcastGeoService;
import com.heim.api.move.application.dto.*;
import com.heim.api.move.application.mapper.MoveMapper;
import com.heim.api.move.application.mapper.MoveSummaryMapper;
import com.heim.api.move.application.mapper.MovingHistoryMapper;
import com.heim.api.payment.application.dto.CreatePaymentDTO;
import com.heim.api.payment.application.dto.PaymentRequest;
import com.heim.api.payment.application.dto.PaymentResponse;
import com.heim.api.payment.application.service.EarningService;
import com.heim.api.payment.application.service.PaymentService;
import com.heim.api.payment.domain.Earning;
import com.heim.api.payment.domain.enums.PaymentMethod;
import com.heim.api.payment.domain.enums.PaymentStatus;
import com.heim.api.payment.infraestructure.repository.DriverPaymentAccountRepository;
import com.heim.api.payment.infraestructure.repository.EarningRepository;
import com.heim.api.payment.infraestructure.repository.PaymentRepository;
import com.heim.api.users.application.dto.UserPaymentRequest;
import com.heim.api.users.domain.entity.User;
import com.heim.api.webSocket.domain.entity.event.MoveAssignedEvent;
import com.heim.api.notification.application.service.NotificationService;
import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.domain.enums.MoveStatus;
import com.heim.api.move.infraestructure.repository.MoveRepository;
import com.heim.api.price.service.MovePricingService;
import com.heim.api.users.infraestructure.repository.UserRepository;
import com.heim.api.webSocket.application.dto.MoveNotificationDTO;
import com.heim.api.webSocket.domain.entity.event.MoveAssignedUserEvent;
import com.heim.api.webSocket.domain.entity.event.MoveFinishedEvent;
import com.heim.api.webSocket.infraestructure.listener.MoveNotificationUserFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Slf4j
@Service
public class MoveService {
    private static final Logger logger = LoggerFactory.getLogger(MoveService.class);
    private final MoveRepository moveRepository;
    private final NotificationService notificationService;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final HazelcastGeoService hazelcastGeoService;
    private final MoveCacheService tripCacheService;
    private final DistanceCalculatorService distanceCalculatorService;
    private final MoveMapper moveMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MoveNotificationUserFactory moveNotificationUserFactory;
    private final PaymentService paymentService;
    private final MovingHistoryMapper movingHistoryMapper;
    private final EarningRepository earningRepository;
    private final EarningService earningService;
    private final PaymentRepository paymentRepository;
    private final DriverPaymentAccountRepository driverPaymentAccountRepository;
    private final EmailNotificationService emailNotificationService;
    private final ScheduledMoveRegistry scheduledMoveRegistry;
    private final MovePricingService movePricingService;




    @Autowired
    public MoveService(MoveRepository moveRepository,
                       NotificationService notificationService,
                       DriverRepository driverRepository,
                       UserRepository userRepository,
                       HazelcastGeoService hazelcastGeoService,
                       MoveCacheService tripCacheService,
                       DistanceCalculatorService distanceCalculatorService,
                       MoveMapper moveMapper,
                        ApplicationEventPublisher applicationEventPublisher,
                       MoveNotificationUserFactory moveNotificationUserFactory,
                       PaymentService paymentService,
                       MovingHistoryMapper movingHistoryMapper,
                       EarningRepository earningRepository,
                       EarningService earningService,
                       PaymentRepository paymentRepository,
                       DriverPaymentAccountRepository driverPaymentAccountRepository,
                       EmailNotificationService emailNotificationService,
                       ScheduledMoveRegistry scheduledMoveRegistry,
                       MovePricingService movePricingService
                       ) {

        this.moveRepository = moveRepository;
        this.notificationService = notificationService;
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.hazelcastGeoService = hazelcastGeoService;
        this.tripCacheService = tripCacheService;
        this.distanceCalculatorService = distanceCalculatorService;
        this.moveMapper = moveMapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.moveNotificationUserFactory = moveNotificationUserFactory;
        this.paymentService = paymentService;
        this.movingHistoryMapper = movingHistoryMapper;
        this.earningRepository =  earningRepository;
        this.earningService = earningService;
        this.paymentRepository = paymentRepository;
        this.driverPaymentAccountRepository = driverPaymentAccountRepository;
        this.emailNotificationService = emailNotificationService;
        this.scheduledMoveRegistry = scheduledMoveRegistry;
        this.movePricingService = movePricingService;
    }

    private static final String[] NOTIFICATION_MESSAGES = {
            "¡Nuevo viaje disponible cerca de ti!",
            "¡No pierdas la oportunidad, viaje disponible cerca!",
            "Último aviso: viaje disponible para aceptar.",
    };


    private MoveConfirmationResponse scheduleMove(MoveRequest moveRequest) {
        User user = userRepository.findById(moveRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BigDecimal price = moveRequest.getPrice().setScale(2, RoundingMode.HALF_UP);

        Move move = new Move();
        move.setUser(user);
        move.setOrigin(moveRequest.getOrigin());
        move.setDestination(moveRequest.getDestination());
        move.setOriginLat(moveRequest.getOriginLat());
        move.setOriginLng(moveRequest.getOriginLng());
        move.setDestinationLat(moveRequest.getDestinationLat());
        move.setDestinationLng(moveRequest.getDestinationLng());
        move.setTypeOfMove(moveRequest.getTypeOfMove());
        move.setPrice(price);
        move.setPaymentMethod(moveRequest.getPaymentMethod());
        move.setDistanceKm(moveRequest.getDistanceKm());
        move.setDurationMin(moveRequest.getEstimatedTime());
        move.setAccessType(moveRequest.getAccessType());
        move.setScheduledTime(moveRequest.getScheduledTime());
        move.setAddressee(moveRequest.getAddressee());
        move.setRecipientPhoneNumber(moveRequest.getRecipientPhoneNumber());
        move.setRequestTime(LocalDateTime.now());
        move.setStatus(MoveStatus.SCHEDULED);
        moveRepository.save(move);
        scheduledMoveRegistry.register(move.getMoveId());
        boolean firstTrip = movePricingService.isFirstTrip(user.getUserId());
        movePricingService.savePricing(move, price, firstTrip);
        log.info("✅ Viaje programado guardado para: {}", moveRequest.getScheduledTime());
        emailNotificationService.sendScheduledMoveEmail(
                user.getEmail(),
                user.getFullName(),
                moveRequest.getOrigin(),
                moveRequest.getDestination(),
                moveRequest.getScheduledTime()
        );
        return new MoveConfirmationResponse("Viaje programado para " + moveRequest.getScheduledTime());
    }

    private static final int MAX_RETRIES = 6;

    public void activateScheduledMove(Move move) {
        double latitude = move.getOriginLat();
        double longitude = move.getOriginLng();

        List<Long> nearbyDrivers = Optional.ofNullable(
                hazelcastGeoService.findNearbyDriversDynamically(latitude, longitude)
        ).orElse(Collections.emptyList());

        if (!nearbyDrivers.isEmpty()) {
            move.setStatus(MoveStatus.REQUESTED);
            move.setLastActivatedAt(LocalDateTime.now());
            moveRepository.save(move);
            Map<String, String> data = buildLocationData(move);
            notificationService.notify(FcmToken.OwnerType.USER, move.getUser().getUserId(),
                    "🚚 Tu viaje está por comenzar",
                    "Estamos buscando el conductor más cercano. Tu servicio programado inicia en 30 minutos.",
                    data, null);
            notifyDriversLimited(move, nearbyDrivers);
            log.info("🚀 Viaje programado {} notificando {} conductores", move.getMoveId(), nearbyDrivers.size());
        } else {
            int retries = move.getRetryCount() + 1;
            move.setRetryCount(retries);
            move.setLastActivatedAt(LocalDateTime.now());

            if (retries >= MAX_RETRIES) {
                move.setStatus(MoveStatus.CANCELLED);
                moveRepository.save(move);
                scheduledMoveRegistry.unregister(move.getMoveId());
                log.warn("❌ Viaje {} cancelado automáticamente: sin conductores tras {} intentos", move.getMoveId(), retries);
                Map<String, String> data = buildLocationData(move);
                notificationService.notify(FcmToken.OwnerType.USER, move.getUser().getUserId(),
                        " Hoy no pudimos encontrar un conductor para tu viaje.",
                        "Te agradecemos tu paciencia mientras crecemos.",
                        data, null);
                emailNotificationService.sendCancelledMoveEmail(
                        move.getUser().getEmail(),
                        move.getUser().getFullName(),
                        move.getOrigin(),
                        move.getDestination(),
                        move.getScheduledTime()
                );
            } else {
                moveRepository.save(move);
                log.warn("⚠️ Viaje {} sin conductores. Intento {}/{}", move.getMoveId(), retries, MAX_RETRIES);
            }
        }
    }

    public MoveConfirmationResponse confirmMove(MoveRequest moveRequest) {
        try {
            log.info("📥 confirmMove recibido - userId: {}, scheduledTime: {}", moveRequest.getUserId(), moveRequest.getScheduledTime());
            log.info("📥 addressee: {}, recipientPhoneNumber: {}", moveRequest.getAddressee(), moveRequest.getRecipientPhoneNumber());
            if (moveRequest.getScheduledTime() != null) {
                return scheduleMove(moveRequest);
            }

            double latitude = moveRequest.getOriginLat();
            double longitude = moveRequest.getOriginLng();

            List<Long> nearbyDrivers = Optional.ofNullable(
                    hazelcastGeoService.findNearbyDriversDynamically(latitude, longitude)
            ).orElse(Collections.emptyList());


            if (!nearbyDrivers.isEmpty()){
               // tripCacheService.storeTrip(tripRequest.getUserId(), tripRequest);
                Optional<Move> existingMove = moveRepository.findByUser_UserIdAndOriginAndDestinationAndStatus(
                        moveRequest.getUserId(),
                        moveRequest.getOrigin(),
                        moveRequest.getDestination(),
                        MoveStatus.REQUESTED
                );
                Move move;
                if (existingMove.isPresent()) {
                    move = existingMove.get();
                    BigDecimal newPrice = moveRequest.getPrice().setScale(2, RoundingMode.HALF_UP);
                    move.setPrice(newPrice);

                    move.setTypeOfMove(moveRequest.getTypeOfMove());
                    move.setAccessType(moveRequest.getAccessType());
                    move.setDistanceKm(moveRequest.getDistanceKm());
                    move.setDurationMin(moveRequest.getEstimatedTime());

                    move = moveRepository.save(move);
                    log.info("Actualizado viaje existente ID: {} con nuevo precio: {}", move.getMoveId(), move.getPrice());
                } else {
                    move = createMove(moveRequest);
                }

                notifyDriversLimited(move, nearbyDrivers);

                return new MoveConfirmationResponse("Enviando solicitud a conductores cercanos...");
            }else {
                return new MoveConfirmationResponse("No hay conductores disponibles cerca por ahora.");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Move createMove(MoveRequest moveRequest) {
        com.heim.api.users.domain.entity.User user = userRepository.findById(moveRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + moveRequest.getUserId()));

        Optional<Move> existingMove = moveRepository.findByUser_UserIdAndOriginAndDestinationAndStatus(
                user.getUserId(),
                moveRequest.getOrigin(),
                moveRequest.getDestination(),
                MoveStatus.REQUESTED
        );

        if (existingMove.isPresent()) {
            log.info("Ya existe un viaje similar para este usuario.");
            return existingMove.get();
        }

        BigDecimal price = moveRequest.getPrice();
        price = price.setScale(2, RoundingMode.HALF_UP);

        Move move = new Move();
        move.setUser(user);
        move.setOrigin(moveRequest.getOrigin());
        move.setDestination(moveRequest.getDestination());
        move.setOriginLat(moveRequest.getOriginLat());
        move.setOriginLng(moveRequest.getOriginLng());
        move.setDestinationLat(moveRequest.getDestinationLat());
        move.setDestinationLng(moveRequest.getDestinationLng());
        move.setTypeOfMove(moveRequest.getTypeOfMove());
        move.setPrice(price);
        move.setPaymentMethod(moveRequest.getPaymentMethod());
        move.setDistanceKm(moveRequest.getDistanceKm());
        move.setDurationMin(moveRequest.getEstimatedTime());
        move.setRequestTime(LocalDateTime.now());
        move.setStatus(MoveStatus.REQUESTED);
        move.setAccessType(moveRequest.getAccessType());
        move.setAddressee(moveRequest.getAddressee());
        move.setRecipientPhoneNumber(moveRequest.getRecipientPhoneNumber());
        log.info("MUDANZA QUE SE CONFIRMA DEL CLIENTE {}", move);
        move = moveRepository.save(move);
        boolean firstTrip = movePricingService.isFirstTrip(user.getUserId());
        movePricingService.savePricing(move, price, firstTrip);
        return move;
    }


    @Transactional
    public MoveDTO assignDriverToMove(AcceptMoveRequest acceptMoveRequest) {
        Optional<Move> moveOptional = moveRepository.findById(acceptMoveRequest.getMoveId());
        System.out.println("DATO QUE VIENE DESDE EL CLIENTE " +acceptMoveRequest);

        if (moveOptional.isPresent()) {
            Move move = moveOptional.get();

            Driver driver = driverRepository.findByUserId(acceptMoveRequest.getDriverId()).orElseThrow(() -> new IllegalArgumentException("Driver not found"));

            if (driver.getPromotionalMovesLeft() == null){
                driver.setPromotionalMovesLeft(0);
                driverRepository.save(driver);
            }
            move.setDriver(driver);
            move.setStatus(MoveStatus.ASSIGNED);
            move.setStartTime(LocalDateTime.now());

          Move savedMove =   moveRepository.save(move);
            logger.info("Viaje actualizado exitosamente: {}", savedMove);

            try {
                sendNotificationToUser(move);
                logger.info("Notificando al usuario");
            }catch (Exception e){
                logger.error("Error al enviar notificación", e);

            }
            return moveMapper.toDTO(savedMove);
        }

        return null;
    }

    @Transactional
    public void markDriverArrived(MovingStatusesDTO movingStatusesDTO) {
        Move move = moveRepository.findById(movingStatusesDTO.getMoveId())
                .orElseThrow(() -> new EntityNotFoundException("Mudanza no encontrada"));

        move.setStatus(MoveStatus.DRIVER_ARRIVED);
        notificationService.notifyUser(FcmToken.OwnerType.USER, move.getUser().getUserId(), "\uD83D\uDE9B ¡Tu conductor ha llegado!",
                "Ya estamos aquí, justo a tiempo para ayudarte a dar este gran paso.");
        moveRepository.save(move);
    }


    @Transactional
    public void startMove(MovingStatusesDTO movingStatusesDTO){
        Move move = moveRepository.findById(movingStatusesDTO.getMoveId()).orElseThrow(()-> new EntityNotFoundException("Mudanza no encontrada"));
        move.setStatus(MoveStatus.MOVING_STARTED);

        move.setStartTime(movingStatusesDTO.getTimestamp() != null ? movingStatusesDTO.getTimestamp() : LocalDateTime.now());

        notificationService.notifyUser(FcmToken.OwnerType.USER, move.getUser().getUserId(), "\uD83D\uDE9A Heim: Despacho en camino",
                "Tu carga ya fue recogida. Sigue el camión en vivo hasta el punto de entrega.");
        moveRepository.save(move);

    }


   @Transactional
    public Move completeMove(MovingStatusesDTO movingStatusesDTO) {
        Optional<Move> moveOptional = moveRepository.findById(movingStatusesDTO.getMoveId());

        if (moveOptional.isPresent()) {
            Move move = moveOptional.get();
            com.heim.api.users.domain.entity.User user = move.getUser();
            move.setStatus(MoveStatus.MOVE_COMPLETE);
            move.setEndTime(LocalDateTime.now());

            earningService.createPendingEarning(move);

          //  PaymentRequest paymentRequest = getPaymentRequest(user, move);
            CreatePaymentDTO paymentDTO = new CreatePaymentDTO();
            paymentDTO.setMoveId(move.getMoveId());
            paymentDTO.setUserId(user.getUserId());
            paymentDTO.setAmount(move.getPrice());

            String wavaPaymentUrl;
            try {
                wavaPaymentUrl = paymentService.createPayment(paymentDTO);
            } catch (Exception e) {
                logger.error("Error al generar el link de pago de Wava", e);
                wavaPaymentUrl = "heim://pay";
            }

            PaymentResponse paymentResponse = getPaymentResponse(move, wavaPaymentUrl);
            moveRepository.save(move);

            notificationService.notifyUser(FcmToken.OwnerType.USER, move.getUser().getUserId(), "\uD83D\uDCE6 ¡Entrega completada con éxito!",
                    "Tu mercancía ha sido entregada. Gracias por confiar tu logística en Heim. ¡Hasta el próximo despacho! \uD83D\uDE9A");

            MoveFinishedEvent event = new MoveFinishedEvent(move.getMoveId(), paymentResponse);
            applicationEventPublisher.publishEvent(event);
         //   logger.info("ENVIANDO DATOS DE PAGO MEDIANTE WEBSOCKET {}", event);

            return move;
        }

        return null;
    }


    private PaymentResponse getPaymentResponse(Move move, String wavaPaymentUrl) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentURL(wavaPaymentUrl);
        paymentResponse.setPaymentMethod(move.getPaymentMethod());
        paymentResponse.setAmount(move.getPrice());
        paymentResponse.setCurrency("COP");
        paymentResponse.setMoveId(move.getMoveId());
        paymentResponse.setOrigin(move.getOrigin());
        paymentResponse.setDestination(move.getDestination());
        paymentResponse.setDurationMin(move.getDurationMin());
        paymentResponse.setDistanceKm(move.getDistanceKm());
        return paymentResponse;
    }


    public MoveStatus getMoveStatus(Long moveId) {
        Move move = moveRepository.findById(moveId)
                .orElseThrow(() -> new EntityNotFoundException("Mudanza no encontrada"));
        return move.getStatus();
    }


    private void sendNotificationToUser(Move move) {
        String message = "";
        GeoLocation driverLocation = hazelcastGeoService.getDriverLocation(move.getDriver().getId());
        Map<String, String> data = buildMoveDataForUser(move, driverLocation);
        logger.info("DATOS DE LA MUDANZA PARA EL USUARIO {}",data);


        com.heim.api.users.domain.entity.User driverUser = move.getDriver().getUser();

       notificationService.notify(
               FcmToken.OwnerType.USER,
               move.getUser().getUserId(),
               "\uD83D\uDE9A ¡Conductor asignado a tu despacho!", "El vehículo está en camino al punto de recogida. Ten tu carga lista para agilizar el proceso. \uD83D\uDCE6",
               data,
               message
               );


        MoveNotificationUserResponse moveNotification = moveNotificationUserFactory.build(move);
        if (driverLocation != null){
            moveNotification.setDriverLat(driverLocation.getLatitude());
            moveNotification.setDriverLng(driverLocation.getLongitude());
        }
        applicationEventPublisher.publishEvent(new MoveAssignedUserEvent(moveNotification, move.getUser().getUserId()));
        logger.info("Enviando datos al usuario por WEBSOCKET: {}",moveNotification);
    }

    private void notifyDriversLimited(Move move, List<Long> nearbyDrivers){
        log.info("Notificando a conductores, total conductores: {}", nearbyDrivers.size());

        List<DriverLocation> driverLocations = new ArrayList<>();
        for (Long driverId: nearbyDrivers){
            GeoLocation geoLocation =  hazelcastGeoService.getDriverLocation(driverId);
            driverLocations.add( new DriverLocation(driverId, geoLocation.getLatitude(), geoLocation.getLongitude()));
        }

        Map<Long, DriverLocation> driverLocationMap = driverLocations.stream().collect(Collectors.toMap(DriverLocation::getDriverId, dl -> dl));

        Map<Long, TimeAndDistanceOriginResponse> driverDistances = distanceCalculatorService
                .calculateDistancesToUserForMultipleDrivers(driverLocations, move.getOriginLat(), move.getOriginLng());


        Map<Long, TimeAndDistanceDestinationResponse> distancesToDestination =
                distanceCalculatorService.calculateDistancesToDestinationForMultipleDrivers(driverLocations,move.getDestinationLat(), move.getDestinationLng());

        for (Map.Entry<Long, TimeAndDistanceOriginResponse> entry : driverDistances.entrySet()) {
            Long driverId = entry.getKey();
            TimeAndDistanceOriginResponse response = entry.getValue();

            if (response != null) {
                log.info("Conductor ID {}: distancia = {}, tiempo = {}",
                        driverId, response.getDistance(), response.getEstimatedTimeOfArrival());
            } else {
                log.warn("No se obtuvo distancia/tiempo para el conductor ID {}", driverId);
            }
        }

        for (Long driverId : nearbyDrivers){
            Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new NoSuchElementException("Driver no encontrado"));
            Long userId = driver.getUser().getUserId();

            int attempts = tripCacheService.getNotificationCount(move.getMoveId(), driverId);
           // if (attempts < MAX_NOTIFICATION_ATTEMPTS){
                String message = NOTIFICATION_MESSAGES[attempts % NOTIFICATION_MESSAGES.length];
                log.info("Notificando conductor con ID: {}, USERID: {}", driverId, userId);

                Map<String, String> moveData = buildMovingInformationDriver(
                        move, driverDistances.get(driverId),
                        distancesToDestination.get(driverId),
                        driverLocationMap.get(driverId));


                notificationService.notify(
                        FcmToken.OwnerType.DRIVER,
                        userId,
                        "\uD83D\uDEA8 ¡Nueva oferta de carga disponible!",
                        "Hay un servicio cerca de tu ubicación. Revisa los detalles y acepta el flete ahora. \uD83D\uDE9A",
                        moveData,
                        message);
                DriverLocation driverLocation = driverLocationMap.get(driverId);

                MoveDTO moveDTO =  moveMapper.toDTO(move);
                String avatar = Optional.ofNullable(move.getUser())
                        .map(User::getUrlAvatarProfile)
                        .orElse("");


                TimeAndDistanceDestinationResponse destinationData = distancesToDestination.get(driverId);
                if (destinationData != null) {
                   moveDTO.setDistanceToDestination(destinationData.getTimeToDestination());
                   moveDTO.setTimeToDestination(destinationData.getDistanceToDestination());
                 }
                if(move.getUser() != null){
                    moveDTO.setFullName(move.getUser().getFullName());
                    moveDTO.setUserId(move.getUser().getUserId());
                }

                if (driverLocation != null) {
                    moveDTO.setDriverLat(driverLocation.getLatitude());
                    moveDTO.setDriverLng(driverLocation.getLongitude());
                }


                TimeAndDistanceOriginResponse originData = driverDistances.get(driverId);
                if (originData != null) {
                    moveDTO.setEstimatedTimeOfArrival(originData.getEstimatedTimeOfArrival());
                    moveDTO.setDistance(originData.getDistance());
                }

                MoveNotificationDTO notification = new MoveNotificationDTO(moveDTO);
                applicationEventPublisher.publishEvent(new MoveAssignedEvent(notification,userId));
                tripCacheService.incrementNotificationCount(move.getMoveId(), driverId);
                logger.info("ENVIANDO DATOS DE LA MUDANZA MEDIANTE WEBSOCKET PARA EL CONDUCTOR {}", moveDTO);

           // }else {
               // log.info("No se notificará más al conductor {} para el viaje {} para evitar spam.", driverId, move.getMoveId());
          //  }
        }
    }

    private Map<String, String> buildLocationData(Move move) {
        Map<String, String> data = new HashMap<>();
        data.put("moveId", String.valueOf(move.getMoveId()));
        data.put("originLat", String.valueOf(move.getOriginLat()));
        data.put("originLng", String.valueOf(move.getOriginLng()));
        data.put("destinationLat", String.valueOf(move.getDestinationLat()));
        data.put("destinationLng", String.valueOf(move.getDestinationLng()));
        data.put("origin", move.getOrigin());
        data.put("destination", move.getDestination());
        return data;
    }

    private Map<String, String> buildMovingInformationDriver(
            Move move,
            TimeAndDistanceOriginResponse distanceResponse,
            TimeAndDistanceDestinationResponse destinationResponse,
            DriverLocation driverLocation) {
        Map<String, String> moveData = new HashMap<>();

        moveData.put("moveId", String.valueOf(move.getMoveId()));
        moveData.put("origin", move.getOrigin());
        moveData.put("destination", move.getDestination());
        moveData.put("originLat", String.valueOf(move.getOriginLat()));
        moveData.put("originLng", String.valueOf(move.getOriginLng()));
        moveData.put("destinationLat", String.valueOf(move.getDestinationLat()));
        moveData.put("destinationLng", String.valueOf(move.getDestinationLng()));
        moveData.put("typeOfMove", String.valueOf(move.getTypeOfMove().name()));
        moveData.put("price", move.getPrice().toPlainString());
        moveData.put("paymentMethod", String.valueOf(move.getPaymentMethod()));
        moveData.put("addressee", move.getAddressee() != null ? move.getAddressee() : "");
        moveData.put("recipientPhoneNumber", move.getRecipientPhoneNumber() != null ? move.getRecipientPhoneNumber() : "");

        log.info("DATOS DE LA DISTANCIA {}" , distanceResponse);

        moveData.put("distance", distanceResponse.getDistance());
        moveData.put("estimatedTimeOfArrival", distanceResponse.getEstimatedTimeOfArrival());
        moveData.put("distanceToDestination", destinationResponse.getDistanceToDestination());
        moveData.put("timeToDestination", destinationResponse.getTimeToDestination());

        // Coordenadas del conductor
        log.info("VALIDACIÓN: Driver Lat/Lng recibidos son VÁLIDOS: {} / {}", driverLocation.getLatitude(), driverLocation.getLongitude());

        moveData.put("driverLat", String.valueOf(driverLocation.getLatitude()));
        moveData.put("driverLng", String.valueOf(driverLocation.getLongitude()));
        moveData.put("originLat", String.valueOf(move.getOriginLat()));
        moveData.put("originLng", String.valueOf(move.getOriginLng()));
        moveData.put("destinationLat", String.valueOf(move.getDestinationLat()));
        moveData.put("destinationLng", String.valueOf(move.getDestinationLng()));
        moveData.put("role", "DRIVER");

        //DATOS DEL USUARIO
        if (move.getUser() != null) {
            moveData.put("userName", move.getUser().getFullName());
            moveData.put("avatarProfile", move.getUser().getUrlAvatarProfile() != null ? move.getUser().getUrlAvatarProfile() : "");
          //  moveData.put("userPhone", move.getUser().getPhone());
        }
        return moveData;
    }

    private Map<String, String> buildMoveDataForUser(Move move, GeoLocation driverLocation) {
        Map<String, String> data = new HashMap<>();
        data.put("moveId", move.getMoveId().toString());
        data.put("status", move.getStatus().toString());
        data.put("amount", move.getPrice().toString());
        data.put("originLat", String.valueOf(move.getOriginLat()));
        data.put("originLng", String.valueOf(move.getOriginLng()));
        data.put("destinationLat", String.valueOf(move.getDestinationLat()));
        data.put("destinationLng", String.valueOf(move.getDestinationLng()));

        if (move.getDriver() != null) {
            Driver driver = move.getDriver();
            com.heim.api.users.domain.entity.User user = driver.getUser();

            data.put("driverLat", String.valueOf(driverLocation.getLatitude()));
            data.put("driverLng", String.valueOf(driverLocation.getLongitude()));
            data.put("driverName", user.getFullName());
            data.put("driverPhone", user.getPhone() != null ? user.getPhone() : "");
            data.put("driverImageUrl", user.getUrlAvatarProfile() != null ? user.getUrlAvatarProfile() : "");



            driverPaymentAccountRepository.findByDriverId(driver.getId()).ifPresent(account ->
                    data.put("accountNumber", account.getAccountNumber())
            );
        }

        return data;
    }

    public List<MovingHistoryDTO> getMovingHistoryByDriverId(Long driverId){
        List<Move> moves = moveRepository.findByDriverIdAndStatus(driverId,MoveStatus.MOVE_COMPLETE);
        logger.info("MUDANZAS {}", moves);
            return movingHistoryMapper.toDtoList(moves);
    }

    public List<MovingHistoryDTO> getMovingHistoryByUserId(Long userId){
        List<Move> moves = moveRepository.findByUser_UserIdAndStatusIn(
                userId, List.of(MoveStatus.MOVE_COMPLETE, MoveStatus.SCHEDULED)
        );
        return movingHistoryMapper.toDtoList(moves);
    }


  public MoveSummaryDTO movingSummary(Long moveId){
        Optional<Move> moveOptional = moveRepository.findById(moveId);
        if (moveOptional.isPresent()){
            Move move = moveOptional.get();
            logger.info("DATA MOVING {}",moveOptional);
            return  MoveSummaryMapper.toSummaryDTO(move);
        }else {
            throw new NotFoundException("Mudanza con ID " + moveId + " no encontrada.");
        }
  }

    public MoveDetailsDTO  findMoveDetails(Long moveId){
        Move move = moveRepository.findById(moveId)
                .orElseThrow(() -> new NotFoundException("Mudanza no encontrada"));

        // 2. Crear el DTO y llenarlo con datos de la mudanza
        MoveDetailsDTO dto = new MoveDetailsDTO();
        dto.setMoveId(move.getMoveId());
        dto.setOrigin(move.getOrigin());
        dto.setDestination(move.getDestination());
        dto.setAmount(move.getPrice());
        dto.setPaymentMethod(move.getPaymentMethod());
        dto.setTypeOfMove(move.getTypeOfMove());
        dto.setMovingDate(move.getEndTime());


        paymentRepository
                .findByMoveIdAndStatus(moveId, PaymentStatus.PAID)
                .ifPresent(payment -> {
                    dto.setTransactionalNumber(payment.getProviderOrderId());
                    dto.setPaymentStatus(payment.getStatus());
                });



        driverRepository.findById(move.getDriver().getId()).ifPresent(driver -> {
            dto.setDriverName(driver.getUser().getFullName());
            dto.setTypeOfVehicle(driver.getVehicleType());

        });

        return dto;
    }



    public Optional<Move> getTripForDriver(Long moveId, Long driverId) {
        return moveRepository.findByMoveIdAndDriver_Id(moveId, driverId);
    }

    @Transactional
    public void cancelMove(Long moveId, Long userId) {
        Move move = moveRepository.findById(moveId)
                .orElseThrow(() -> new NotFoundException("Mudanza no encontrada"));

        if (!move.getUser().getUserId().equals(userId)) {
            throw new BusinessException("No tienes permiso para cancelar este viaje", "FORBIDDEN");
        }

        if (move.getStatus() != MoveStatus.SCHEDULED && move.getStatus() != MoveStatus.REQUESTED) {
            throw new BusinessException("Solo se pueden cancelar viajes programados", "INVALID_STATUS");
        }

        if (move.getScheduledTime() == null) {
            throw new BusinessException("Solo se pueden cancelar viajes programados", "INVALID_STATUS");
        }

        move.setStatus(MoveStatus.CANCELLED);
        moveRepository.save(move);
        scheduledMoveRegistry.unregister(moveId);
        log.info("❌ Viaje {} cancelado por el usuario {}", moveId, userId);
        emailNotificationService.sendCancelledMoveEmail(
                move.getUser().getEmail(),
                move.getUser().getFullName(),
                move.getOrigin(),
                move.getDestination(),
                move.getScheduledTime()
        );
    }

}
