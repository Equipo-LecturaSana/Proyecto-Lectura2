pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        // Si registraste JDK en Tools:
        // jdk 'JDK-17'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Equipo-LecturaSana/Proyecto-Lectura2.git'
            }
        }

        // (Opcional) Diagnóstico para confirmar dónde está el pom.xml
        stage('Diagnóstico') {
            steps {
                sh '''
                  echo "Directorio actual:"; pwd
                  echo "Buscando pom.xml (hasta 3 niveles):"
                  find . -maxdepth 3 -name "pom.xml" -print
                  echo "Listado de la carpeta con espacio:"
                  ls -la "Lectura Sana xd" || true
                '''
            }
        }

        stage('Compile') {
            steps {
                // IMPORTANTE: entrar a la carpeta con espacio
                dir('Lectura Sana xd') {
                    sh 'mvn -B -DskipTests clean compile'
                }
            }
        }

        stage('Test') {
            steps {
                dir('Lectura Sana xd') {
                    sh 'mvn -B test'
                }
            }
            post {
                always {
                    // Publicar resultados de JUnit desde la subcarpeta
                    junit allowEmptyResults: true, testResults: 'Lectura Sana xd/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                dir('Lectura Sana xd') {
                    sh 'mvn -B -DskipTests package'
                }
            }
            post {
                success {
                    // Archivar el .jar generado en la subcarpeta
                    archiveArtifacts artifacts: 'Lectura Sana xd/target/*.jar', fingerprint: true
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
