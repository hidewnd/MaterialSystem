FROM openjdk:8-jre-alpine
EXPOSE 7050
MAINTAINER lyne
ADD material-module-system.jar material-module-system.jar
CMD ["-Ddruid.mysql.usePingMethod=false"]
ENTRYPOINT ["java","-jar","material-module-system.jar"]
