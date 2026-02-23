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

        // (Opcional pero útil) Mantengamos Diagnóstico una vez más
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
                // Ejecutar en la RAÍZ (aquí está el pom.xml)
                sh 'mvn -B -DskipTests clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    // Reportes Surefire en la RAÍZ
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B -DskipTests package'
            }
            post {
                success {
                    // Artefactos generados en la RAÍZ
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
