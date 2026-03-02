pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        jdk 'JDK-17'
    }

    stages {
        
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Diagnóstico') {
            steps {
                sh '''
                  echo "Directorio actual:"; pwd
                  echo "Contenido raíz:"; ls -la
                  echo "Buscando pom.xml:"
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
                sh 'mvn -B clean verify'
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
                        sh 'mvn sonar:sonar -Dsonar.projectKey=LecturaSana'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 15, unit: 'MINUTES') {
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
            echo '🚀 Iniciando despliegue automático...'

            // 1️⃣ Buscar proceso anterior de la app
            sh '''
            echo "🔎 Buscando proceso anterior..."
            PID=$(pgrep -f LecturaSana-0.0.1-SNAPSHOT.jar || true)

            if [ ! -z "$PID" ]; then
              echo "🛑 Matando proceso anterior con PID $PID"
              kill -9 $PID
            else
              echo "✅ No había proceso previo"
            fi
            '''

            // 2️⃣ Levantar nueva versión con perfil PROD
            withEnv(['JENKINS_NODE_COOKIE=dontKillMe']) {
                sh '''
                nohup java -jar target/LecturaSana-0.0.1-SNAPSHOT.jar \
                --server.port=8081 \
                --spring.profiles.active=prod \
                > deploy.log 2>&1 &
                '''
            }

            // 3️⃣ Esperar que levante
            echo "⏳ Esperando que la aplicación inicie..."
            sh 'sleep 10'

            // 4️⃣ Validar con Actuator
            sh '''
            echo "🔍 Verificando estado de la aplicación..."
            curl -f http://localhost:8081/actuator/health
            '''

            echo '✅ Aplicación desplegada y validada correctamente.'
            echo '🌍 http://3.140.188.231:8081'
        }
    }
}
    } // <--- ESTA ES LA LLAVE QUE FALTABA PARA CERRAR "STAGES"

    post { 
        failure {
            echo '❌ El pipeline falló. Notificando a Discord...'
            sh '''
                curl -H "Content-Type: application/json" \
                     -d "{\\"content\\": \\"🚨 **¡Alerta Equipo!** El build o despliegue de *Lectura Sana* acaba de fallar. ❌\\nRevisen los logs en Jenkins para ver qué pasó.\\"}" \
                     https://discord.com/api/webhooks/1475567824637394974/8IcAQSusCm8vz0J-aIWF12stQxi0NKQCS2--CVCXOARhVM3xXU5esa98whb5l6aZddlk
            '''
        }
        success {
            echo '✅ Pipeline y Deploy exitosos. Notificando a Discord...'
            sh '''
                curl -H "Content-Type: application/json" \
                     -d "{\\"content\\": \\"🚀 **¡Despliegue Exitoso!**\\nEl proyecto *Lectura Sana* ya está actualizado y corriendo en:\\nhttp://3.140.188.231:8081\\n\\n✅ Pruebas y SonarQube aprobados.\\"}" \
                     https://discord.com/api/webhooks/1475567824637394974/8IcAQSusCm8vz0J-aIWF12stQxi0NKQCS2--CVCXOARhVM3xXU5esa98whb5l6aZddlk
            '''
        }
    }
}