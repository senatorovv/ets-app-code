FROM 159743215/gateway-apis:jdkbaseimage03-1
LABEL maintainer="Kumoco"
VOLUME /tmp
ARG JAR_FILE=build/libs/productorder*.jar
ADD ${JAR_FILE} app.jar
COPY run.sh /run.sh
WORKDIR /opt/productorder
ENTRYPOINT ["/run.sh"]
