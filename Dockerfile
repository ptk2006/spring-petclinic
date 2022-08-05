# FROM openjdk:17-alpine  
# COPY /target/spring-petclinic-*-SNAPSHOT.jar /home/spring-petclinic.jar
# CMD ["java","-jar","/home/spring-petclinic.jar"]

FROM php:apache-bullseye

ARG dbpass
ARG apache_dir
ARG git_url
ARG db_connect_script
ARG apache_conf
ARG apache_ports
ARG apache_port

RUN apt-get update -y && apt-get install -y git

RUN docker-php-ext-install mysqli && docker-php-ext-enable mysqli

RUN git -C ${apache_dir} clone ${git_url} . && \
    sed -i 's/localhost/db/g' ${db_connect_script} && \
    sed -i "s/password123/${dbpass}/g" ${db_connect_script}

RUN echo "ServerName $(hostname -i)" | tee -a ${apache_conf}

RUN sed -i "s/Listen 80/Listen ${apache_port}/g" ${apache_ports}
