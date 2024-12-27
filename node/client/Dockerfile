# Use an official OpenJDK image as the base
FROM openjdk:23-jdk

# Set the working directory
WORKDIR /app

# Copy the Hazelcast JAR files and application
COPY target/dependency/hazelcast-5.5.0.jar /app/
COPY target/client-1.0-SNAPSHOT.jar /app/

# Expose Hazelcast's default port
EXPOSE 5701

# Set the default command
CMD ["java", "-cp", "hazelcast-5.5.0.jar:client-1.0-SNAPSHOT.jar", "org.example.MatrixMultiplicationClient"]
