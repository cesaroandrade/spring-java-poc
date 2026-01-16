FROM eclipse-temurin:21-jre

RUN groupadd -r appgroup && useradd -r -g appgroup appuser && mkdir -p /opt/java && chown -R appuser:appgroup /opt/java

COPY target/oraclaservice-0.0.1-SNAPSHOT.jar /opt/java

RUN chown appuser:appgroup /opt/java/oraclaservice-0.0.1-SNAPSHOT.jar

USER appuser

EXPOSE 8080

CMD ["java","-jar","/opt/java/oraclaservice-0.0.1-SNAPSHOT.jar"]