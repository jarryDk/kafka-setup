#!/bin/bash

set -euo pipefail

cd ${0%/*}
cd ../ssl

echo "Creating CA Authority"
./create_ca_authority.sh -name Jarry

if [ "X" == "X$KAFKA_JARRY_DK_STOREPASS" ]; then
    echo "KAFKA_JARRY_DK_STOREPASS need to be set via export"
    echo "export KAFKA_JARRY_DK_STOREPASS=password1234"
    exit 0
fi

if [ "X" == "X$KAFKA_JARRY_DK_KEYPASS" ]; then
    echo "KAFKA_JARRY_DK_KEYPASS need to be set via export"
    echo "export KAFKA_JARRY_DK_KEYPASS=password1234"
    exit 0
fi

mkdir -p /opt/apache/kafka/jarry_dk

echo "Creating server certificates for broker100.jarry.dk"
./create_server_certificates.sh -name broker100.jarry.dk -spass $KAFKA_JARRY_DK_STOREPASS -kpass $KAFKA_JARRY_DK_KEYPASS
cp -v server_certs/broker100.jarry.dk.keystore.jks /opt/apache/kafka/jarry_dk/broker100.jarry.dk.keystore.jks

echo "Creating server certificates for broker200.jarry.dk"
./create_server_certificates.sh -name broker200.jarry.dk -spass $KAFKA_JARRY_DK_STOREPASS -kpass $KAFKA_JARRY_DK_KEYPASS
cp -v server_certs/broker200.jarry.dk.keystore.jks /opt/apache/kafka/jarry_dk/broker200.jarry.dk.keystore.jks

echo "Creating server certificates for broker300.jarry.dk"
./create_server_certificates.sh -name broker300.jarry.dk -spass $KAFKA_JARRY_DK_STOREPASS -kpass $KAFKA_JARRY_DK_KEYPASS
cp -v server_certs/broker300.jarry.dk.keystore.jks /opt/apache/kafka/jarry_dk/broker300.jarry.dk.keystore.jks

echo "Creating server certificates for broker400.jarry.dk"
./create_server_certificates.sh -name broker400.jarry.dk -spass $KAFKA_JARRY_DK_STOREPASS -kpass $KAFKA_JARRY_DK_KEYPASS
cp -v server_certs/broker400.jarry.dk.keystore.jks /opt/apache/kafka/jarry_dk/broker400.jarry.dk.keystore.jks

echo "Move kafka.server.truststore.jks"
cp -v server_certs/kafka.server.truststore.jks /opt/apache/kafka/jarry_dk/kafka.server.truststore.jks

echo "Creating client certificates for jarry.dk"
./create_client_certificates.sh
cp -v client_certs/kafka.client.truststore.jks /opt/apache/kafka/jarry_dk/kafka.client.truststore.jks
