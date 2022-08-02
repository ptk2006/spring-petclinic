#!groovy
pipeline {
    agent none
    stages {     
        stage('Maven Install') {
            // agent {label 'Linux'}
            agent {         
                docker { image 'maven:3.8.1-adoptopenjdk-11' 
                args '--env $JAVA_HOME=/opt/java/openjdk' }
            }       
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Docker Build') {
            agent {label 'Linux'}
            steps {
                sh 'docker build -t ptk2006/spring-petclinic:latest .'
            }
        }
    }
}