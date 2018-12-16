FROM maven:3.6-jdk-11
WORKDIR /app
COPY . .
RUN mvn compile
ENTRYPOINT ["mvn", "spring-boot:run"]
