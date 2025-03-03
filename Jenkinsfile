pipeline {
    agent any

    environment {
        SONAR_SCANNER = "${tool 'SonarQube Scanner'}/bin/SonarQube Scanner" // Ensure correct tool name
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/mohay22/task6.git' // Replace with your repository
            }
        }

        stage('Setup JDK & Gradle') {
            steps {
                script {
                    def javaHome = tool name: 'JDK17', type: 'jdk'
                    env.JAVA_HOME = javaHome
                    if (isUnix()) {
                        env.PATH = "${javaHome}/bin:${env.PATH}"
                    } else {
                        env.PATH = "${javaHome}\\bin;${env.PATH}"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew clean assembleDebug'
                    } else {
                        bat 'gradlew.bat clean assembleDebug'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew testDebugUnitTest'
                    } else {
                        bat 'gradlew.bat testDebugUnitTest'
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'sqp_0afe9972009c6d94c269fdc3780d46127b7c008c', variable: 'SONAR_LOGIN')]) {
                        script {
                            // Temporarily echo the SonarQube token for debugging (Remove this in production)
                            echo "The SonarQube token is: ${SONAR_LOGIN}"

                            // SonarQube analysis for Unix systems
                            if (isUnix()) {
                                sh "./gradlew sonarqube -Dsonar.login=$SONAR_LOGIN"
                            } else {
                                // SonarQube analysis for Windows systems
                                bat "gradlew.bat sonarqube -Dsonar.login=%SONAR_LOGIN%"
                            }
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploy step - You can add Firebase or other distribution steps here'
            }
        }
    }

    post {
        always {
            // Updated to reflect correct test report path
            junit 'app/build/reports/androidTests/connected/debug/TEST-*.xml' // Updated report path
            archiveArtifacts artifacts: 'app/build/outputs/**/*.apk', fingerprint: true
        }
        success {
            echo '✅ Build & Tests Passed Successfully!'
        }
        failure {
            echo '❌ Build or Tests Failed. Check logs for details.'
        }
    }
}
