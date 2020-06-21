FROM openjdk:8-jdk-alpine

COPY metric-monitor-1.0.jar /
RUN echo "Asia/Shanghai" > /etc/timezone
ENTRYPOINT ["java","-jar","/metric-monitor-1.0.jar"]
