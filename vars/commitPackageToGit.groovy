def call(vipbPath){
  echo 'Commit only the VIPB such that the build number is correctly updated between builds, even if the build machine is lost'
  
 withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'DCAF-Build', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD']]) {
    bat 'git config user.name'
    bat 'git config user.email'
   //These lines are commented out because they were only needed once to set up the build environment.  They needed to be called from script because script runs as a different user, and I couldn't be arsed to find the right spot for it globally for all users.
   // bat 'git config --global user.email "yourname@yourcompany.com"'
   // bat 'git config --global user.name "your github username"'
    bat 'git commit -m "Auto-update files from build, ignore this commit" '+'"'+vipbPath+'"'
    def git_remote_url=bat returnStdout: true, script: '@git remote get-url origin'
   echo "Remote_URL raw: "+git_remote_url
   git_remote_url=git_remote_url.trim()     //get rid of any trailing end of line characters
   git_remote_url=git_remote_url.drop(8)    //Drop first 8 characters, which in our case are the https:// chars.  This might break if we change access modes.
   bat "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${git_remote_url} HEAD:${env.JOB_NAME.replaceFirst('.+/', '')}"
 }
}
