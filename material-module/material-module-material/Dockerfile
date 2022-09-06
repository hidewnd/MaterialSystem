FROM openjdk:8-jre-alpine
EXPOSE 7060
MAINTAINER lyne
ADD material-module-material.jar material-module-material.jar
CMD ["-Ddruid.mysql.usePingMethod=false"]
ENTRYPOINT ["java","-jar","material-module-material.jar"]
