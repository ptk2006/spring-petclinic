FROM openjdk:17-alpine  
COPY /target/spring-petclinic-*-SNAPSHOT.jar /home/spring-petclinic.jar
CMD ["java","-jar","/home/spring-petclinic.jar"]