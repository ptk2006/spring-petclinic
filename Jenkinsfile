#!groovy
pipeline {
    environment {
        imagename = "ptk2006/spring-petclinic"
    }
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
                sh 'docker build -t $imagename:$BUILD_NUMBER -t $imagename:latest .'
            }
        }
        stage('Docker Push') {
            agent any
            steps {
                withCredentials([usernamePassword(credentialsId: 'dokcer_hub', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
                sh 'docker login -u ${env.dockerHubUser} -p ${env.dockerHubPassword}'
                // sh 'docker tag $imagename:$BUILD_NUMBER $imagename:latest'
                sh 'docker push $imagename --all-tags'
                sh 'docker rmi $(docker images -aq)'
                }
            }
        }
    }
}