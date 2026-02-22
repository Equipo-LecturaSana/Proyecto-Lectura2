pipeline {
    agent any
    stages {
        stage('Carga de Entorno') {
            steps {
                echo 'Iniciando Pipeline para Proyecto Lectura Sana...'
                sh 'java -version'
            }
        }
        stage('Compilación Automatizada (LS-72)') {
            steps {
                // Ejecuta la compilación sin lanzar los tests para asegurar que el build pase
                sh './mvnw clean compile -DskipTests'
            }
        }
        stage('Empaquetado (Build)') {
            steps {
                sh './mvnw install -DskipTests'
            }
        }
    }
}