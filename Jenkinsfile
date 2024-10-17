pipeline {
    agent any
    environment {
        DB_HOST = '192.168.1.88'
    }
    tools {
        maven 'maven 3.9.9'
    }
    stages {
        stage('Build maven') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/r9956/mueblesStgo-Backend']])
                bat 'mvn clean package'
            }
        }

        stage('Unit Tests') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Build docker image') {
            steps {
                script {
                    bat 'docker build -t r9956/payroll-backend:latest .'
                }
            }
        }

        stage('Push image to Docker Hub') {
            steps {
                    bat 'docker login -u r9956 -p admin'
                    bat 'docker push r9956/payroll-backend:latest'
                }
            }
        }
    }
}