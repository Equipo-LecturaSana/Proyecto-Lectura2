pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        // Si registraste un JDK en Tools, puedes habilitarlo:
        // jdk 'JDK-17'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Equipo-LecturaSana/Proyecto-Lectura2.git'
            }
        }

        stage('Diagnóstico') {
            steps {
                sh '''
                  echo "Directorio actual:"; pwd
                  echo "Contenido raíz:"; ls -la
                  echo "Buscando pom.xml (hasta 3 niveles):"
                  find . -maxdepth 3 -name pom.xml -print
                '''
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn -B -DskipTests clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        //  Nueva etapa para SonarQube
        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('LecturaSana-Sonar') {
                        sh 'sonar-scanner'
                    }
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B -DskipTests package'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completado.'
        }
    }
}
