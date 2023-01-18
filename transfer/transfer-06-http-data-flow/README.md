# Implement a simple Http Data Flow

This sample demonstrates how to implement a simple Http Data Flow.

# How to build a connector

Consumer and Provider are both connectors and before to start them, we need to
build the connector module as well with the following command

```bash
./gradlew transfer:transfer-06-http-data-flow:connector:build
```

After the build end you should verify that the connector jar is created in the directory
[connector.jar](connector/build/libs/)

# How to run a connector

It's important to note that only the properties file differs between the consumer and the provider

### Run a provider

To run a provider you should run the following command

```bash
java -Dedc.vault=transfer/transfer-06-http-data-flow/provider/provider-vault.properties -Dedc.keystore=transfer/transfer-06-http-data-flow/certs/cert.pfx -Dedc.keystore.password=123456 -Dedc.fs.config=transfer/transfer-06-http-data-flow/provider/provider-configuration.properties -jar transfer/transfer-06-http-data-flow/connector/build/libs/connector.jar
```

### Run a consumer

To run a consumer you should run the following command

```bash
java -Dedc.vault=transfer/transfer-06-http-data-flow/consumer/consumer-vault.properties -Dedc.keystore=transfer/transfer-06-http-data-flow/certs/cert.pfx -Dedc.keystore.password=123456 -Dedc.fs.config=transfer/transfer-06-http-data-flow/consumer/consumer-configuration.properties -jar transfer/transfer-06-http-data-flow/connector/build/libs/connector.jar

```

### Register data plane instance for provider

```bash
curl -H 'Content-Type: application/json' \
     -d '{
   "edctype": "dataspaceconnector:dataplaneinstance",
   "id": "provider-dataplane",
   "url": "http://localhost:19292/control/transfer",
   "allowedSourceTypes": [ "HttpData" ],
   "allowedDestTypes": [ "HttpProxy", "HttpData" ],
   "properties": {
     "publicApiUrl": "http://localhost:19291/public/"
   }
 }' \
     -X POST "http://localhost:19195/dataplane/instances"
```

### Register data plane instance for consumer

```bash
curl -H 'Content-Type: application/json' \
     -d '{
   "edctype": "dataspaceconnector:dataplaneinstance",
   "id": "consumer-dataplane",
   "url": "http://localhost:29292/control/transfer",
   "allowedSourceTypes": [ "HttpData" ],
   "allowedDestTypes": [ "HttpProxy", "HttpData" ],
   "properties": {
     "publicApiUrl": "http://localhost:29291/public/"
   }
 }' \
     -X POST "http://localhost:29195/dataplane/instances"
```

### Create an Asset on the provider side

```bash
curl -d '{
           "asset": {
             "properties": {
               "asset:prop:id": "assetId",
               "asset:prop:name": "product description",
               "asset:prop:contenttype": "application/json"
             }
           },
           "dataAddress": {
             "properties": {
               "name": "Test asset",
               "baseUrl": "https://jsonplaceholder.typicode.com/users",
               "type": "HttpData"
             }
           }
         }' -H 'content-type: application/json' http://localhost:19193/api/v1/data/assets
```

### Create a Policy on the provider

```bash
curl -d '{
           "id": "aPolicy",
           "policy": {
             "uid": "231802-bb34-11ec-8422-0242ac120002",
             "permissions": [
               {
                 "target": "assetId",
                 "action": {
                   "type": "USE"
                 },
                 "edctype": "dataspaceconnector:permission"
               }
             ],
             "@type": {
               "@policytype": "set"
             }
           }
         }' -H 'content-type: application/json' http://localhost:19193/api/v1/data/policydefinitions
```

### Create a contract definition on Provider

```bash
curl -d '{
           "id": "1",
           "accessPolicyId": "aPolicy",
           "contractPolicyId": "aPolicy",
           "criteria": []
         }' -H 'content-type: application/json' http://localhost:19193/api/v1/data/contractdefinitions
```

# How to fetch catalog on consumer side

```bash
curl http://localhost:29193/api/v1/data/catalog\?providerUrl\=http://localhost:19194/api/v1/ids/data
```

# Negotiate a contract

Please in case you have some issues with the jq option, not that it's not mandatory and you can drop
it from the command.

```bash

```bash
curl -d '{
           "connectorId": "provider",
           "connectorAddress": "http://localhost:19194/api/v1/ids/data",
           "protocol": "ids-multipart",
           "offer": {
             "offerId": "1:50f75a7a-5f81-4764-b2f9-ac258c3628e2",
             "assetId": "assetId",
             "policy": {
               "uid": "231802-bb34-11ec-8422-0242ac120002",
               "permissions": [
                 {
                   "target": "assetId",
                   "action": {
                     "type": "USE"
                   },
                   "edctype": "dataspaceconnector:permission"
                 }
               ],
               "@type": {
                 "@policytype": "set"
               }
             }
           }
         }' -X POST -H 'content-type: application/json' http://localhost:29193/api/v1/data/contractnegotiations \
         -s | jq
```

# Getting the contract agreement id

Please in case you have some issues with the jq option, not that it's not mandatory and you can drop
it from the command.

```bash
curl -X GET "http://localhost:29193/api/v1/data/contractnegotiations/<contract negotiation id, returned by the negotiation call>" \
    --header 'Content-Type: application/json' \
    -s | jq
```

# Start the transfer

Please in case you have some issues with the jq option, not that it's not mandatory and you can drop
it from the command.

As a pre-requisite, you need to have a server that run on port 4000

```bash
./gradlew transfer:transfer-06-http-data-flow:http-server:build
java -jar transfer/transfer-06-http-data-flow/http-server/build/libs/http-server.jar

```

```bash
curl -X POST "http://localhost:29193/api/v1/data/transferprocess" \
    --header "Content-Type: application/json" \
    --data '{
                "connectorId": "provider",
                "connectorAddress": "http://localhost:19194/api/v1/ids/data",
                "contractId": "<contract agreement id>",
                "assetId": "assetId",
                "managedResources": "false",
                "dataDestination": { "type": "HttpProxy" }
            }' \
    -s | jq
```

This call will retrieve back the transfer process id, which you can use to check the status of the
transfer. An example of the response could be

> {"createdAt":1674078357807,"id":"591bb609-1edb-4a6b-babe-50f1eca3e1e9"}⏎

where id is the transfer process id.

# Check the transfer status

```bash
curl http://localhost:19193/api/v1/data/transferprocess/<transfer process id>
```
