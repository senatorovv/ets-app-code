FROM gradle:6.4-jdk8 AS build
COPY . /build_app
WORKDIR /build_app
RUN gradle compileJava
RUN gradle -i --parallel build
RUN mv build/libs/productorder*.jar build/libs/app.jar
RUN cp run.sh build/libs/run.sh
RUN tar -czvf build/libs/app.tar.gz build/libs/app.jar build/libs/run.sh

FROM registry.hub.docker.com/hybrid2k3/private:jdcbaseimage03-1
USER root
COPY --from=build /build_app /build_app
WORKDIR /build_app
RUN ls -la && ls -la build && ls -la build/libs/
RUN cp build/libs/app.tar.gz /app.tar.gz
RUN tar -xzvf /app.tar.gz && mv -f build/libs/* / && rm -rf build/ && rm -rf apps.tar.gz
USER kumoco
CMD /run.sh
