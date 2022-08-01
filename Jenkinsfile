#!groovy
pipeline {
    agent none
    stages {     
        stage('Maven Install') {
            agent {         
                docker { image 'maven:3.8.1-adoptopenjdk-11' }
                args '--env $JAVA_HOME=/opt/java/openjdk'
            }       
            steps {
                sh 'mvn --version'
           }
        }
    }
}