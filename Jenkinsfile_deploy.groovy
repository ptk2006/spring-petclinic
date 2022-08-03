#!groovy
pipeline {
    environment {
        imagename = "ptk2006/spring-petclinic"
        containername = "petclinic"
        dev_instance = "tf-target-01.westeurope.cloudapp.azure.com"
        qa_instance = "tf-target-02.westeurope.cloudapp.azure.com"
        curl_command_template = 'curl -o /dev/null -s -w %{http_code}\n http://'        
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
                        sh "ssh -oStrictHostKeyChecking=no $SSH_USER@$INSTANCE -i $SSH_KEY docker rm -f $containername || true"
                        sh "ssh -oStrictHostKeyChecking=no $SSH_USER@$INSTANCE -i $SSH_KEY docker run --name $containername -d -p 80:8080 $imagename:$BUILD_VERSION"
                    }
                }
            }
        }
        stage("Check deploying") {
            steps {
                script {
                    // curl_command = '$curl_command_template$INSTANCE'
                    // echo "$curl_command"
                    curl_answer = sh "curl -Is http://$INSTANCE | head -n 1 | grep -o -e 200 -e 301 "
                    echo "&curl_answer"
                    if (env.curl_answer == '200') {
                        echo 'Site is working'
                    } else {
                        echo 'I execute elsewhere'
                    }
                }
            }
        }
    }    
    
}