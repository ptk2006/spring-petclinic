#!groovy
pipeline {
    environment {
        imagename = "ptk2006/spring-petclinic"
        containername = "petclinic"
        dev_instance = "tf-target-01.westeurope.cloudapp.azure.com"
        qa_instance = "tf-target-02.westeurope.cloudapp.azure.com"
        curl_command_template = "curl -o /dev/null -s -w '%{http_code}\n' http://"        
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

                    withCredentials([sshUserPrivateKey(credentialsId: 'tls_private_key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                        // sh "ssh -oStrictHostKeyChecking=no $SSH_USER@$INSTANCE -i $SSH_KEY docker rm -f $petclinic || true"
                        sh "ssh -oStrictHostKeyChecking=no $SSH_USER@$INSTANCE -i $SSH_KEY docker run -- name $petclinic -d -p 80:8080 $imagename:$BUILD_VERSION"
                    }
                }
            }
        }
        stage("Check deploying") {
            steps {
                script {
                    curl_command = "$curl_command_template$INSTANCE"
                    echo "$curl_command"
                    if (env.curl_command == '200') {
                        echo 'I only execute on the master branch'
                    } else {
                        echo 'I execute elsewhere'
                    }
                }
            }
        }
    }    
    
}