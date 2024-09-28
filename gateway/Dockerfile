ARG JAVA_RUNTIME_IMAGE=openjdk:21


FROM ${JAVA_RUNTIME_IMAGE} AS builder

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

RUN java -Djarmode=layertools -jar app.jar extract


FROM openjdk:21

COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
