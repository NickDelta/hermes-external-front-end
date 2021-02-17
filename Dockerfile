FROM adoptopenjdk/openjdk11:alpine
COPY target/hermes-external-front-end.jar app.jar
RUN addgroup -S hermesgroup && adduser -S hermes -G hermesgroup
USER hermes
EXPOSE 8083
CMD java -jar app.jar