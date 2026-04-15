FROM gradle:9.3.0-jdk21-alpine AS build
WORKDIR /project
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon
COPY src ./src
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /project/build/libs/*.jar app.jar
RUN addgroup -S developers && adduser -S runner -G developers && chown runner:developers .
USER runner:developers 
ENV JAVA_ARGS='-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:MaxMetaspaceSize=256m -Djava.security.egd=file:/dev/./urandom'
VOLUME /tmp
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_ARGS} -jar app.jar"]