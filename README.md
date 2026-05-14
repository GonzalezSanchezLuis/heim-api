# Heim API - Sistema de Logística de Mudanzas 🚚

Heim es una plataforma robusta diseñada para la gestión de servicios de transporte y logística de mudanzas. El backend está construido siguiendo principios de **Arquitectura Limpia** y **Spring Modulith**, permitiendo un sistema escalable y fácil de mantener para la asignación de conductores y seguimiento de pedidos en tiempo real.
## 📱 Vista de la Aplicación (UI)

|         Login & Registro          |                              Mapa                               |              Calculo de precio y trazado de ruta               | Vista del conductor                                            | 
|:---------------------------------:|:---------------------------------------------------------------:|:--------------------------------------------------------------:|----------------------------------------------------------------|
| ![Login](screenshot/welcome.jpeg) | <p><img src="screenshot/4.png" alt="Heim App" width="1000"></p> | <p><img src="screenshot/3.png" alt="Heim App" width="900"></p> | <p><img src="screenshot/5.png" alt="Heim App" width="500"></p> |
## 🚀 Tecnologías Clave

* **Backend:** Java 17+, Spring Boot 3, Spring Modulith.
* **Tiempo Real:** WebSockets (STOMP) para tracking de conductores.
* **Caché y Rendimiento:** Hazelcast (Caché distribuida en memoria).
* **Base de Datos:** MySQL / PostgreSQL.
* **Infraestructura:** Docker & Docker Compose.
* **Notificaciones:** Integration con GCM/FCM (Firebase Cloud Messaging).
* **APIs Externas:** Google Maps Platform (Directions, Distance Matrix).

## 🏗️ Arquitectura: Spring Modulith

A diferencia de los monolitos tradicionales, este proyecto utiliza **Spring Modulith** para garantizar un bajo acoplamiento. Cada módulo (Usuarios, Viajes, Notificaciones, Pagos) tiene sus límites definidos, facilitando una futura migración a microservicios si el negocio lo requiere.



## ✨ Características Principales

1.  **Seguimiento en Tiempo Real:** Comunicación bidireccional mediante WebSockets para que el cliente vea la posición del conductor sin refrescar la app.
2.  **Cálculo de Rutas Dinámico:** Integración con Google Maps para estimación de costos y tiempos basada en tráfico real.
3.  **Gestión de Tokens GCM:** Flujo automatizado para envío de notificaciones push críticas.
4.  **Alta Disponibilidad:** Uso de Hazelcast para reducir la carga en la base de datos y acelerar las consultas de sesiones activas.

## 🛠️ Instalación y Configuración (Local)

### Requisitos
* Docker y Docker Compose.
* JDK 17+.

### Pasos
1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/GonzalezSanchezLuis/Heim-API.git](https://github.com/GonzalezSanchezLuis/Heim-API.git)
    ```
2.  **Configurar variables de entorno:**
    Crea un archivo `application-local.yml` basado en el `application.yml.example` e incluye tus API Keys de Google.
3.  **Levantar servicios con Docker:**
    ```bash
    docker-compose up -d
    ```

Heim API está completamente dockerizada, lo que garantiza una ejecución inmediata y consistente en cualquier entorno (Windows, macOS o Linux) mediante un solo comando."

## 📄 Licencia
License: All rights reserved. This project is part of a private venture (Heim). Code is public for portfolio and technical review purposes only