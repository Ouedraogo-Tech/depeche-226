# Image du backend pour l'hébergeur (Render)
# Étape 1 : compilation avec Maven et Java 21 (tests ignorés : ils tournent en local)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

# Étape 2 : exécution (Java 21 seul, sans Maven)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
# Offre gratuite : 512 Mo de mémoire. Java en prend au plus 70 %, avec un ramasse-miettes léger
# et une compilation plus rapide au démarrage (le serveur redémarre à chaque réveil)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k -XX:TieredStopAtLevel=1"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
