#!groovy
pipeline {
    environment {
        imagename = "ptk2006/spring-petclinic"
    }
    agent any
    stages {     
        stage("Clone feature") {
            steps {
                git branch: 'feature', credentialsId: 'Gitlab_key', url: 'git@github.com:ptk2006/spring-petclinic.git'
            }
        }
        stage("TestDockerfile") {
            steps {
                script {
                    try {
                        sh 'docker run --rm -i hadolint/hadolint < Dockerfile'
                        println 'Lint finished with no errors'
                    }catch (Exception e) {
                        println 'Error found in Lint'
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

    }
}
