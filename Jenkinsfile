pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        jdk 'JDK-17'
    }

    stages {
       
       stage('Checkout') {
            steps {
                // Esto descargará automáticamente la rama correcta (sea main, pruebas, etc.)
                checkout scm
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

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('LecturaSana-Sonar') {
                        sh '''
                          sonar-scanner \
                            -Dsonar.projectKey=LecturaSana \
                            -Dsonar.projectName=LecturaSana \
                            -Dsonar.sources=.
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
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
        failure {
            echo '❌ El pipeline falló. Notificando a Discord...'
            
            // Reemplaza TU_URL_DEL_WEBHOOK con el enlace que copiaste en el Paso 1
            sh """
                curl -H "Content-Type: application/json" \\
                     -d '{"content": "🚨 **¡Alerta Equipo!** El build de *Lectura Sana* acaba de fallar. ❌\\nRevisen el código para arreglarlo."}' \\
                     https://discord.com/api/webhooks/1475567824637394974/8IcAQSusCm8vz0J-aIWF12stQxi0NKQCS2--CVCXOARhVM3xXU5esa98whb5l6aZddlk
            """
        }
        success {
            echo '✅ Pipeline exitoso. Notificando a Discord...'
            
            // Opcional: Un mensaje de que todo salió bien
            sh """
                curl -H "Content-Type: application/json" \\
                     -d '{"content": "✅ **¡Éxito!** El nuevo código compiló y pasó las pruebas perfectamente."}' \\
                     https://discord.com/api/webhooks/1475567824637394974/8IcAQSusCm8vz0J-aIWF12stQxi0NKQCS2--CVCXOARhVM3xXU5esa98whb5l6aZddlk
            """
        }
    }
}
