FROM public.ecr.aws/lambda/java:11 as build-image

ARG SCRATCH_DIR=/var/task/build

COPY src/ src/
COPY gradle/ gradle/
COPY build.gradle gradlew ./

RUN mkdir build
COPY gradle/lambda-build-init.gradle ./build

RUN ./gradlew --project-cache-dir $SCRATCH_DIR/gradle-cache -Dsoftware.amazon.aws.lambdabuilders.scratch-dir=$SCRATCH_DIR --init-script $SCRATCH_DIR/lambda-build-init.gradle build
RUN rm -r $SCRATCH_DIR/gradle-cache
RUN rm -r $SCRATCH_DIR/lambda-build-init.gradle
RUN cp -r $SCRATCH_DIR/*/build/distributions/lambda-build/* .

FROM public.ecr.aws/lambda/java:11

RUN yum install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm -y
RUN yum install curl cabextract xorg-x11-font-utils fontconfig -y
RUN rpm -i https://downloads.sourceforge.net/project/mscorefonts2/rpms/msttcore-fonts-installer-2.6-1.noarch.rpm

COPY --from=build-image /var/task/META-INF ./
COPY --from=build-image /var/task/com ./com
COPY --from=build-image /var/task/lib/ ./lib
# Command can be overwritten by providing a different command in the template directly.
CMD ["com.woowahan.market.forecast.App::handleRequest"]
