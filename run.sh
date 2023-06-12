#!/bin/sh

if [[ -z "${OCNAMESPACE}" ]]; then
  OCNAMESPACE=$(cat /run/secrets/kubernetes.io/serviceaccount/namespace)
fi

if [[ -z "${OCDEPLOYNAME}" ]]; then
  OCDEPLOYNAME=$(hostname | rev | cut -c19- | rev)
fi

if [[ -z "${OCNODENAME}" ]]; then
  OCNODENAME="OpenShiftNode"
fi

if [ "${OCNAMESPACE}" == "kumoco" ]; then
  OCCLUSTERNAME="Prod"
else
  OCCLUSTERNAME="SIT"
fi


syslog-ng

/usr/bin/filebeat -c /etc/filebeat/filebeat.yml --path.data /tmp/filebeatdata --path.logs /tmp/filebeatlog > /tmp/filebeat.log &

java -jar /app.jar 2>&1 | tee /tmp/javaapp.log 
