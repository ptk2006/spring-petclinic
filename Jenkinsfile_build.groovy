#!groovy
pipeline {
    environment {
        imagename = "ptk2006/spring-petclinic"
    }
    agent { label 'main' }
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
            steps {
                sh 'docker build -t $imagename:$BUILD_NUMBER -t $imagename:latest .'
            }
        }
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dokcer_hub', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
                sh 'docker login -u $dockerHubUser -p $dockerHubPassword'
                sh 'docker push $imagename --all-tags'
                sh 'docker rmi -f $(docker images -aq) || true'
                echo 'Docker image bild is $BUILD_NUMBER'
                }
            }
        }
    }
}
