def call(){
  //This is a test to determine if the mailer works correctly by forcing an unstable build.
  echo 'Sending emails out if build failed'
  echo 'Here is the reported changeset of the build:'
  echo currentBuild.rawBuild.changeSets
  currentBuild.result = 'FAILURE'
  step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: emailextrecipients([[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']])])
}
