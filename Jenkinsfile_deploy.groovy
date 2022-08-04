#!groovy
pipeline {
    environment {
        imagename = "ptk2006/spring-petclinic"
        containername = "petclinic"
        dev_instance = "tf-target-01.westeurope.cloudapp.azure.com"
        qa_instance = "tf-target-02.westeurope.cloudapp.azure.com"
    }
    agent any
    stages {     
        stage("Deploy image") {
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
                    echo "Build version: $BUILD_VERSION"

                    withCredentials([sshUserPrivateKey(credentialsId: 'tls_private_key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                        sh "ssh -oStrictHostKeyChecking=no $SSH_USER@$INSTANCE -i $SSH_KEY docker rm -f $containername"
                        sh "ssh -oStrictHostKeyChecking=no $SSH_USER@$INSTANCE -i $SSH_KEY docker pull $imagename:$BUILD_VERSION || true"
                        sleep(5)
                        sh "ssh -oStrictHostKeyChecking=no $SSH_USER@$INSTANCE -i $SSH_KEY docker run --name $containername -d -p 80:8080 $imagename:$BUILD_VERSION"
                    }
                }
            }
        }
        stage('Check Availability') {
            steps {
                script {             
                    sleep(10)
                    try {         
                        sh "curl -s --head  --request GET  $INSTANCE | grep -e '200' -e '301'"
                    } catch (Exception e) {
                        error "Site isn't available"
                    }
                }
            }
        }
    }
}
