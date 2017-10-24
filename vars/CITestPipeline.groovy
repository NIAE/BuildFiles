#!/usr/bin/env groovy

//CITestPipeline(utfPaths, lvVersion)

def call(utfPaths,lvVersion){

switch(lvVersion){  //This is to abstract out the different Jenkinsfile conventions of setting version to 14.0 instead of 2014.
  case "14.0":
    lvVersion="2014"
    break
  case "15.0":
    lvVersion="2015"
    break
  case "16.0":
    lvVersion="2016"
    break
  case "17.0":
    lvVersion="2017"
    break
}

def continueBuild
  
  node(lvVersion){
        echo 'Starting build...'
		
		
      stage ('Pre-Clean'){
        preClean()
      }
	  
	  
      stage ('SCM_Checkout'){
        echo 'Attempting to get source from repo...'
        timeout(time: 5, unit: 'MINUTES'){
          checkout scm
        }
      }
      
	  
	  // If this change is a pull request and the DIFFING_PIC_REPO variable is set on the jenkins master, diff vis.
      if (env.CHANGE_ID && env.DIFFING_PIC_REPO) {
        stage ('Diff VIs'){
          lvDiff(lvVersion)
        }
      }
	  
	  
      stage ('Check Preconditions for Build'){
        continueBuild=checkCommits()
      }
	  
	  
    if(continueBuild){
        stage ('Temp Directories'){
          bat 'mkdir build_temp'
        }
		
        stage ('UTF'){
          utfPaths.each{utfPath->
            echo 'UTF path: '+utfPath
            timeout(time: 30, unit: 'MINUTES'){
              utfTest(utfPath, lvVersion)  //Run tests on all projects    
            }
          }
        }
     }
  }
       
	   stage ('Post-Clean'){
          postClean()
        }    