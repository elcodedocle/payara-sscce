#!/usr/bin/env bash

# Bash script for git bisect to determine whether a particular Payara revision build fails a (SSCCE) test or not
#
# Returns 125 on failed Payara builds to tell git bisect to skip the revision
#
# Requirements:
#  - (git), java, maven, docker, payara project, payara-sscce project
#  - Disable maven dependency plugin payara.zip override in hello-payara-world-test/pom.xml
#  - hello.ear (and hello.jar SAM module) must be built already (It saves time)
#  - Tune the env vars below to match your environment
#
# Operation:
#  1. Builds the checked out Payara revision
#  2. Builds the test environment, embedding the built Payara revision
#  3. Deploys the test environment
#  4. Runs the test(s), returning 0 on success or non-zero on fail

PAYARA_FOLDER=${PAYARA_FOLDER:-"$HOME/workspace/Payara"}
SKIP_ON_PAYARA_BUILD_FAILURE=${SKIP_ON_PAYARA_BUILD_FAILURE:-true}
PAYARA_SSCCE_FOLDER=${PAYARA_SSCCE_FOLDER:-"$HOME/workspace/payara-sscce"}

# Builds payara and generates payara.zip distribution
build_and_package_payara(){
  pushd "$PAYARA_FOLDER" && \
  mvn clean package -DskipTests=true && \
  popd
  return $?
}

build_deploy_and_run_payara_sscce_tests() {
  cp "$PAYARA_FOLDER/appserver/distributions/payara/target/payara.zip" "$PAYARA_SSCCE_FOLDER/hello-payara-world-test/target/test-classes/dockerfiles" && \
  pushd "$PAYARA_SSCCE_FOLDER/hello-payara-world-test" && \
  mvn verify && \
  popd
  return $?
}

run_script() {
  build_and_package_payara 1>&2
  build_result=$?
  if [ "$build_result" == "0" ]; then
    build_deploy_and_run_payara_sscce_tests
    result=$?
  else
    if [ "$SKIP_ON_PAYARA_BUILD_FAILURE" == "true" ]; then
      # we let bisect continue on build errors
      result=125
    else
      result=$build_result
    fi
  fi
  return $result
}

run_script
result=$?
pushd -0 && dirs -c
exit $result
