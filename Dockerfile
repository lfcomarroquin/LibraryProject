FROM ubuntu:latest
LABEL authors="Luis Marroquin"

ENTRYPOINT ["top", "-b"]
# Utiliza una imagen base con OpenJDK 17 y Gradle 7.4.0
FROM gradle:7.4.0-jdk17 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de tu proyecto al directorio de trabajo
COPY . .

# Establece permisos de Gradle
RUN chmod +x gradlew

# Construye tu aplicación con Gradle
RUN ./gradlew build --no-daemon

# Cambia a una imagen más ligera de OpenJDK 17 para la ejecución
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR de tu aplicación al directorio de trabajo
COPY --from=build /app/build/libs/LibraryProject-0.0.1.jar .
COPY --from=build /app/src/main/resources/application.properties .

# Exponer el puerto que utilizará la aplicación
EXPOSE 8080

# Define el comando de inicio de la aplicación
CMD ["java", "-jar", "LibraryProject-0.0.1.jar"]