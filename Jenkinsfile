def getProfileName(pEnvName) {
    switch(pEnvName) {

      case "api-gateway-sit":
        env.PROFILE_NAME = "sit"
        break

      case "e2e-non-production":
        env.PROFILE_NAME = "e2e"
        break

      case "uat-non-production":
        env.PROFILE_NAME = "uat"
        break

      case "dev-non-prod":
        env.PROFILE_NAME = "dev-sit"
        break

      default:
        env.PROFILE_NAME = "dev-sit"
        break
    }
}

pipeline {

    agent any

    options { disableConcurrentBuilds() }

    stages {

            stage("Select Environment") {
                           options {
                             timeout(time: 20, unit: 'SECONDS')
               }
               steps {
                  script {
                     try {
                         env.RELEASE_SCOPE = input message: 'Please Provide Parameters', ok: 'Next',
                                     parameters: [choice(name: 'RELEASE_SCOPE', choices: ['api-gateway-sit','e2e-non-production', 'uat-non-production', 'dev-non-prod'].join('\n'), description: 'Please select the Environment')]
                            echo "Selected Environment: ${env.RELEASE_SCOPE}"
                         } catch (Throwable e) {
                            echo "TIMEOUT.... Waited 40 secs..."
                            env.RELEASE_SCOPE = 'dev-non-prod'
                     }

                  getProfileName( "${env.RELEASE_SCOPE}" )

                  echo "------------------------------------------------------"
                  echo "Selected Profile: ${env.PROFILE_NAME}"
                  echo "Selected Environment: ${env.RELEASE_SCOPE}"
                  echo "------------------------------------------------------"

                  }
               }
            }
  }
}
try {
    node ("kcgradle"){            

            stage('Clean Up') {
                echo 'Cleaning without ws ...'
                cleanWs()
            }

            stage('Clone Repository') {
                  echo 'Checking out the latest code from Github'
                  checkout scm
                  stash name:"gitsourcecode"
            }

            stage('Compile') {
                    echo 'Compiling ...'
                    sh './gradlew compileJava'
            }

            stage('Unit Tests') {
                   echo 'Unit testing. ..'
                   sh './gradlew test'
           }

            stage('Build') {
                    echo 'Building ...'
                    sh './gradlew -i --parallel build'
                    sh 'sed -i "s/active=sit/active=${PROFILE_NAME}/g" run.sh'
                    sh 'mv build/libs/productorder*.jar build/libs/app.jar'
                    sh 'cp run.sh build/libs/run.sh'
                    sh 'tar -czvf build/libs/app.tar.gz build/libs/app.jar build/libs/run.sh'
                    stash name:"jarfile", includes:"build/libs/app.tar.gz"
            }

            stage('Static Code Analysis/Test Coverage') {
                    echo 'Code Analysis ...'
                    withSonarQubeEnv(credentialsId: 'kcjen-sonar-token', installationName: 'sonarqube') { 
                        sh './gradlew sonarqube'
                }
            }

  node {
            stage('Build Config') {

                sh 'hostname'
                unstash name:"gitsourcecode"
                withCredentials([usernamePassword(credentialsId: 'ocadmin', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh 'oc login "$OPENSHIFTSERVER" -u "$USERNAME" -p "$PASSWORD" --insecure-skip-tls-verify'
                try {
                    sh 'oc delete -f buildconfig.yaml -n vfom-jenkins-non-production'
                } catch (err) {
                    echo "Build Config Doesnt exist"
                }
                sh 'sed -i "s/productorder:latest/productorder:${PROFILE_NAME}/g" buildconfig.yaml'
                sh 'oc apply -f buildconfig.yaml -n vfom-jenkins-non-production'
                echo "New BuildConfig created"
            }
        }

      }
    }

    node {
            stage("Build Image") {
                  unstash name:"jarfile"
                  def status = sh(returnStdout: true, script: "oc start-build productorder-dockerbuild --from-file=build/libs/app.tar.gz -n vfom-jenkins-non-production ")

                  def result = status.split("\n").find{ it.matches("^build.*started") }

                  if (!result) {
                      echo "ERROR: No started build found for ${appName}"
                      currentBuild.result = 'FAILURE'
                      return
                  }

                  def startedBuild = result.replaceAll("build [^0-9a-zA-Z]*", "").replaceAll("[^0-9a-zA-Z]* started", "").replaceFirst("^.*/", "")
                  echo "Build ${startedBuild} has started. Now watching it ..."

                  timeout(time: 20, unit: 'MINUTES') {
                     openshift.withCluster() {
                         openshift.withProject() {
                            def build = openshift.selector('builds', "${startedBuild}")
                            build.untilEach {
                               def object = it.object()
                               if(object.status.phase == "Failed") {
                                  error("Build ${startedBuild} failed")
                               }
                               return object.status.phase == "Complete"
                            }
                         }  
                     }
                  }
            }
    }

node ("kcgradle"){
             stage('Non-Prod Deploy') {
                   echo 'Deploying...'
                   unstash name:"gitsourcecode"
                   withCredentials([usernamePassword(credentialsId: 'ocadmin', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh '''
                        oc login "$OPENSHIFTSERVER" -u "$USERNAME" -p "$PASSWORD" --insecure-skip-tls-verify
                        oc project "${RELEASE_SCOPE}" -q
                        oc apply -f "${PROFILE_NAME}-deploy.yaml"
                        oc logout
                        '''
   }

                  echo "$env.RELEASE_SCOPE"

                  if ( "$env.RELEASE_SCOPE" == "e2e-non-production" ) {
                      withCredentials([usernamePassword(credentialsId: 'ocadmin', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                         sh 'oc login "$OPENSHIFTSERVER" -u "$USERNAME" -p "$PASSWORD" --insecure-skip-tls-verify'
                         try {
                             sh 'oc delete -f buildconfig.yaml -n vfom-jenkins-non-production'
                         } catch (err) {
                             echo "Build Config Doesnt exist"
                         }
                         sh 'sed -i "s/productorder:${PROFILE_NAME}/productorder:latest/g" buildconfig.yaml'
                         sh 'oc apply -f buildconfig-prod.yaml -n vfom-jenkins-non-production'
                         sh 'oc logout'
                        }
                     }

             }
    }

    node {
        stage("Build Latest Tag") {
            echo "Latest tag creating...."
            if ( "$env.RELEASE_SCOPE" == "e2e-non-production") {
                  unstash name:"jarfile"
                  def status = sh(returnStdout: true, script: "oc start-build productorder-dockerbuild --from-file=build/libs/app.tar.gz -n vfom-jenkins-non-production ")

                  def result = status.split("\n").find{ it.matches("^build.*started") }

                  if (!result) {
                      echo "ERROR: No started build found for ${appName}"
                      currentBuild.result = 'FAILURE'
                      return
                  }

                  def startedBuild = result.replaceAll("build [^0-9a-zA-Z]*", "").replaceAll("[^0-9a-zA-Z]* started", "").replaceFirst("^.*/", "")
                  echo "Build ${startedBuild} has started. Now watching it ..."

                  timeout(time: 20, unit: 'MINUTES') {
                     openshift.withCluster() {
                         openshift.withProject() {
                            def build = openshift.selector('builds', "${startedBuild}")
                            build.untilEach {
                               def object = it.object()
                               if(object.status.phase == "Failed") {
                                  error("Build ${startedBuild} failed")
                               }
                               return object.status.phase == "Complete"
                            }
                         }  
                     }
                  }
            }
        }
    }


} catch (err) {
   echo "in catch block"
   echo "Caught: ${err}"
   currentBuild.result = 'FAILURE'
   throw err
}
