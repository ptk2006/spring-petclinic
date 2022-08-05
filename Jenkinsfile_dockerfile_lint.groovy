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
        // stage("TestDockerfile") {
        //     steps {
        //         script {
        //             def lintResult = sh returnStdout: true, script: 'docker run --rm -i hadolint/hadolint < Dockerfile'
        //             try {
        //                 lintResult
        //                 println 'Lint finished with no errors'
        //             }catch (Exception e) {
        //                 println 'Error found in Lint'
        //                 println "${lintResult}"
        //                 currentBuild.result = 'UNSTABLE'
        //             }
        //         }
        //     }
        // }

    }
}
