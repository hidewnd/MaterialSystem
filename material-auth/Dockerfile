FROM openjdk:8-jre-alpine
EXPOSE 7010
MAINTAINER lyne
ADD material-auth.jar material-auth.jar
CMD ["-Ddruid.mysql.usePingMethod=false"]
ENTRYPOINT ["java","-jar","material-auth.jar"]