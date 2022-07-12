pipeline{
  agent any
  stages{
    stage('Build'){
      steps{
        withGradle(){
          sh './gradlew assemble'
        }
      }  
    }
    stage('Test'){
      steps{
        withGradle(){
          sh './gradlew test'
    }
      }
    }
     stage('Docker build'){
      steps{
     
          sh 'docker-compose -f docker-compose-prod.yml build && docker-compose -f docker-compose-prod.yml up -d'
    
      }
    }
  }
  post { 
        always { 
            sh 'docker-compose -f docker-compose-prod.yml down'
        }
    }
}
