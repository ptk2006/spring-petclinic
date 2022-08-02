#!groovy
pipeline {
    agent none
    stages {     
        stage('Maven Install') {
            agent {         
                docker { image 'maven:3.8.6' }
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