FROM maven:3.6.0-jdk-8-alpine
#
ARG MAVEN_VERSION=3.8.6
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries
ARG USER_HOME_DIR="/applitools"
#

RUN apk add --no-cache curl tar bash procps


WORKDIR "/eyes.sdk.java3"
COPY ./eyes-universal-core/ /applitools/
COPY entrypoint.sh /applitools

EXPOSE 8080

WORKDIR "/applitools"
RUN chmod a+x entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]
# ENTRYPOINT ["mvn", "test", "-pl", "eyes-universal-core", "-e", "-x"]
