# --- Etapa de Compilación ---
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copiamos los archivos de configuración de Gradle primero para aprovechar el caché
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Damos permisos de ejecución al wrapper de Gradle
RUN chmod +x gradlew

# Copiamos el código fuente
COPY src src

# Compilamos el proyecto generando el archivo JAR
RUN ./gradlew clean build -x test

# --- Etapa de Ejecución ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiamos el JAR generado desde la etapa de compilación
# Usamos un comodín (*) para evitar errores si el nombre cambia ligeramente
COPY --from=build /app/build/libs/*.jar app.jar

# Exponemos el puerto que usa Spring Boot por defecto
EXPOSE 8080

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]