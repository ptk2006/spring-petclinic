#!groovy
pipeline {
    environment {
        imagename = "ptk2006/spring-petclinic"
    }
    agent none
    stages {     
        stage('build && SonarQube analysis') {
            agent { label 'main' }
            steps {
                withSonarQubeEnv('local-sq') {
                    // Optionally use a Maven environment you've configured already
                    withMaven(maven:'Maven 3.8.6') {
                        sh 'mvn clean package sonar:sonar'
                    }
                }
            }
        }
        stage("Quality Gate") {
            agent { label 'main' }
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    // Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
                    // true = set pipeline to UNSTABLE, false = don't
                    waitForQualityGate abortPipeline: true
                }
            }
        }         
        stage('Maven Install') {
            agent {         
                docker { image 'maven:3.8.6' }
            }       
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Docker Build') {
            agent { label 'main' }
            steps {
                sh 'docker build -t $imagename:$BUILD_NUMBER -t $imagename:latest .'
            }
        }
        stage('Docker Push') {
           agent { label 'main' }
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
