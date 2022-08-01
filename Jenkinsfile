#!groovy
pipeline {
    agent none
    stages {     
        stage('Maven Install') {
            agent {         
                docker { image 'maven:latest' }
            }       
            steps {
                sh 'mvn clean install'
           }
        }
    }
}