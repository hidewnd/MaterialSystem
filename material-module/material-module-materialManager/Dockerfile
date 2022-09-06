FROM openjdk:8-jre-alpine
EXPOSE 7070
MAINTAINER lyne
ADD material-module-materialManager.jar material-module-materialManager.jar
CMD ["-Ddruid.mysql.usePingMethod=false"]
ENTRYPOINT ["java","-jar","material-module-materialManager.jar"]
