FROM maven:3.6-jdk-11
WORKDIR /app
COPY . .
RUN mvn compile
CMD ["mvn", "spring-boot:run"]
