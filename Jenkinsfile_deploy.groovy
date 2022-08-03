#!groovy
pipeline {
    environment {
        imagename = "ptk2006/spring-petclinic"
        dev_instance = "tf-target-01.westeurope.cloudapp.azure.com"
        qa_instance = "tf-target-02.westeurope.cloudapp.azure.com"        
    }
    agent any
    stages {     
        stage("Deploy artifacts") {
            steps {
                script {
                    if (env.TARGET_ENVIRONMENT == "DEV") {
                        echo "Deploying to DEV environment"
                        INSTANCE = env.dev_instance
                    } else if (env.TARGET_ENVIRONMENT == "QA") {
                        echo "Deploying to QA environment"
                        INSTANCE = env.qa_instance
                    }

                    echo "Target instance IP: $INSTANCE"

                    withCredentials([sshUserPrivateKey(credentialsId: 'tls_private_key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                        sh "ssh $SSH_USER@$INSTANCE -i $SSH_KEY docker run -d -p 80:8080 $imagename:$BUILD_VERSION"
                    }
                }
            }
        }
    }
}