pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v $HOME/.m2:/root/.m2'
        }
    }
    
    
    stages {
        stage('Build') {
            steps {
                configFileProvider([configFile(fileId: '1875c4af-1841-47bf-b289-951a565458d1', variable: 'MAVEN_SETTINGS')]) {
                    sh 'mvn -X -s $MAVEN_SETTINGS clean deploy'
                }
                //sh 'mvn -B verify'
            }
        }
    }
}
