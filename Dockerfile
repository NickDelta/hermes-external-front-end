FROM openjdk:11-jre-slim
COPY target/hermes-external-front-end.jar app.jar
RUN groupadd hermes && useradd -g users -G hermes hermes
USER hermes
EXPOSE 8083
CMD java -jar app.jar