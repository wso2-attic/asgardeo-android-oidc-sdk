def repository_url = "https://github.com/asgardio/asgardio-android-oidc-sdk.git"
def repository_branch = "master"
def repository_name = "asgardio/asgardio-android-oidc-sdk"

def incrementMinorVersion(currentVersion) {
    def versionComponents = currentVersion.tokenize(".")
    return "${versionComponents[0]}.${versionComponents[1]}.${versionComponents[2].toInteger() + 1}"
}

node('PRODUCT_BIONIC_ECS') {
    stage('Preparation') {
        // Clone the GitHub repository
        checkout([$class           : 'GitSCM',
                  branches         : [[name: repository_branch]],
                  extensions       : [[
                                              $class       : 'UserExclusion',
                                              excludedUsers: 'wso2-jenkins-bot'
                                      ]],
                  userRemoteConfigs: [[url: repository_url]]])
    }
    stage('Build') {
        withCredentials([
                usernamePassword(credentialsId: '4ff4a55b-1313-45da-8cbf-b2e100b1accd',
                        usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')
        ]) {
            withEnv(["BINTRAY_USER=gayashanbc", "BINTRAY_API_KEY=d67d509a4d403e0b7e0a6712954790a499ee09c8"]) {
                def latestVersion = sh(returnStdout: true, script: 'git describe --tags | cut -d \'-\' -f 1 | cut -d \'v\' -f 2').trim()
                def releaseVersion = RELEASE_VERSION ?: incrementMinorVersion(latestVersion)
                def developmentVersion = "${incrementMinorVersion(releaseVersion)}-SNAPSHOT"

                println "Latest Release Version: ${latestVersion}"
                println "Next Release Version: ${releaseVersion}"
                println "Next Development Release Version: ${developmentVersion}"

                sh """
                    git config  user.email "jenkins-bot@wso2.com"
                    ./gradlew increment -PnewVersion=${releaseVersion}
                    ./gradlew clean assembleRelease
                    ./gradlew bintrayUpload
                    sed -ie "s/${latestVersion}/${releaseVersion}/g" README.md
                    git status
                    git add io.asgardio.android.oidc.sdk/build.gradle io.asgardio.android.oidc.sdk.sample/build.gradle README.md
                    git commit -m "[WSO2 Release][Release ${releaseVersion}]Prepare release v${releaseVersion}"
                    git push -f https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/asgardio/asgardio-android-oidc-sdk.git HEAD:master
                 """

                sh returnStdout: true,
                        script: "curl --retry 5 -s -u ${GIT_USERNAME}:${GIT_PASSWORD} " +
                                "-d '{\"tag_name\": \"v${releaseVersion}\", \"target_commitish\": \"${repository_branch}\", \"name\":\"Asgardio - Android OIDC SDK Release v${releaseVersion}\",\"body\":\"Asgardio - Android OIDC SDK version v${releaseVersion} released!\"}' " +
                                "https://api.github.com/repos/${repository_name}/releases"

                sh """
                ./gradlew increment -PnewVersion=${developmentVersion}
                ./gradlew clean assembleRelease
                git status
                git add io.asgardio.android.oidc.sdk/build.gradle io.asgardio.android.oidc.sdk.sample/build.gradle
                git commit -m "[WSO2 Release][Release ${releaseVersion}]Prepare for next development iteration"
                git push -f https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/asgardio/asgardio-android-oidc-sdk.git HEAD:master
                """
            }
        }
    }
}
