FROM openjdk:21-jdk-slim AS builder

COPY . /

RUN ./gradlew clean bootJar

FROM openjdk:21-jdk-slim

LABEL mainers=eliasmflilico@gmail.com

# Required by container health check
RUN apt-get update \
    && apt-get install -y curl \
    && apt-get clean

ARG PORT

COPY --from=builder  app/build/libs/app-1.0.0.jar /app.jar

ENV PORT $PORT

ENV TZ America/Sao_Paulo

ENV SPRING_PROFILES_ACTIVE $PROFILE

EXPOSE $PORT

CMD java -jar /app.jar
#CMD tail -f /dev/null
