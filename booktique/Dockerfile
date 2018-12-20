FROM maven:3.6-jdk-11
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn compile
CMD ["mvn", "spring-boot:run"]
