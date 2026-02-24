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
                    // Aquí se usa el nombre configurado en Manage Jenkins → Configure System → SonarQube servers
                    withSonarQubeEnv('LecturaSana-Sonar') {
                        sh 'mvn sonar:sonar -Dsonar.projectKey=LecturaSana'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    // Esta línea consulta el resultado del análisis en SonarQube
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

        stage('Deploy') {
            steps {
                script {
                    echo '🚀 Desplegando aplicación...'
                    // 1. Buscamos si ya hay un proceso corriendo en el puerto 8080 y lo detenemos
                    sh 'sudo fuser -k 8080/tcp || true'
                    
                    // 2. Ejecutamos el nuevo JAR en segundo plano (nohup)
                    // Asegúrate de que el nombre del JAR coincida con el que genera tu pom.xml
                    sh 'nohup java -jar target/LecturaSana-0.0.1-SNAPSHOT.jar > deploy.log 2>&1 &'
                    
                    echo '✅ Aplicación desplegada en http://3.140.188.231:8081'
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
       post {
        failure {
            echo '❌ El pipeline falló. Notificando a Discord...'
            sh """
                curl -H "Content-Type: application/json" \
                     -d '{"content": "🚨 **¡Alerta Equipo!** El build o despliegue de *Lectura Sana* acaba de fallar. ❌\\nRevisen los logs en Jenkins para ver qué pasó."}' \
                     https://discord.com/api/webhooks/1475567824637394974/8IcAQSusCm8vz0J-aIWF12stQxi0NKQCS2--CVCXOARhVM3xXU5esa98whb5l6aZddlk
            """
        }
        success {
            echo '✅ Pipeline y Deploy exitosos. Notificando a Discord...'
            sh """
                curl -H "Content-Type: application/json" \
                     -d '{"content": "🚀 **¡Despliegue Exitoso!**\\nEl proyecto *Lectura Sana* ya está actualizado y corriendo en:\\nhttp://3.140.188.231:8080\\n\\n✅ Pruebas y SonarQube aprobados."}' \
                     https://discord.com/api/webhooks/1475567824637394974/8IcAQSusCm8vz0J-aIWF12stQxi0NKQCS2--CVCXOARhVM3xXU5esa98whb5l6aZddlk
            """
        }
    }
    }
}
