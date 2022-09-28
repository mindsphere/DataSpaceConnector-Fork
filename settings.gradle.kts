/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *       Fraunhofer Institute for Software and Systems Engineering - refactoring
 *       ZF Friedrichshafen AG - add dependency & reorder entries
 *       Fraunhofer Institute for Software and Systems Engineering - refactoring
 *
 */

rootProject.name = "dataspaceconnector"

// EDC core modules --------------------------------------------------------------------------------
include(":core:common:base")
include(":core:common:boot")
include(":core:common:jwt-core")
include(":core:common:policy-engine")
include(":core:common:policy-evaluator")
include(":core:common:state-machine")
include(":core:common:util")

include(":core:control-plane:contract")
include(":core:control-plane:control-plane-core")
include(":core:control-plane:transfer")

include(":core:data-plane:data-plane-core")
include(":core:data-plane:data-plane-framework")

include(":core:data-plane-selector:data-plane-selector-core")

include(":core:federated-catalog:federated-catalog-core")

// modules that provide implementations for data ingress/egress ------------------------------------
include(":data-protocols:ids:ids-api-configuration")
include(":data-protocols:ids:ids-api-multipart-endpoint-v1")
include(":data-protocols:ids:ids-api-multipart-dispatcher-v1")
include(":data-protocols:ids:ids-core")
include(":data-protocols:ids:ids-jsonld-serdes")
include(":data-protocols:ids:ids-spi")
include(":data-protocols:ids:ids-token-validation")
include(":data-protocols:ids:ids-transform-v1")

// modules for technology- or cloud-provider extensions --------------------------------------------
include(":extensions:common:api:api-core")
include(":extensions:common:api:observability")
include(":extensions:common:auth:auth-basic")
include(":extensions:common:auth:auth-tokenbased")
include(":extensions:common:aws:aws-test")
include(":extensions:common:aws:s3-core")
include(":extensions:common:azure:azure-eventgrid")
include(":extensions:common:azure:azure-resource-manager")
include(":extensions:common:azure:azure-test")
include(":extensions:common:azure:blob-core")
include(":extensions:common:azure:cosmos-common")
include(":extensions:common:configuration:filesystem-configuration")
include(":extensions:common:events:cloudevents-http")
include(":extensions:common:http")
include(":extensions:common:http:jersey")
include(":extensions:common:http:jersey-micrometer")
include(":extensions:common:http:jetty")
include(":extensions:common:http:jetty-micrometer")
include(":extensions:common:iam:decentralized-identity")
include(":extensions:common:iam:decentralized-identity:dummy-credentials-verifier")
include(":extensions:common:iam:decentralized-identity:identity-common-test")
include(":extensions:common:iam:decentralized-identity:identity-did-core")
include(":extensions:common:iam:decentralized-identity:identity-did-crypto")
include(":extensions:common:iam:decentralized-identity:identity-did-service")
include(":extensions:common:iam:decentralized-identity:identity-did-web")
include(":extensions:common:iam:iam-mock")
include(":extensions:common:iam:oauth2:daps")
include(":extensions:common:iam:oauth2:oauth2-core")
include(":extensions:common:junit")
include(":extensions:common:micrometer")
include(":extensions:common:monitor:jdk-logger-monitor")
include(":extensions:common:sql:common-sql")
include(":extensions:common:sql:lease-sql")
include(":extensions:common:sql:pool:apache-commons-pool-sql")
include(":extensions:common:transaction")
include(":extensions:common:transaction:transaction-atomikos")
include(":extensions:common:transaction:transaction-local")
include(":extensions:common:vault:azure-vault")
include(":extensions:common:vault:filesystem-vault")
include(":extensions:common:vault:hashicorp-vault")

include(":extensions:control-plane:api:data-management")
include(":extensions:control-plane:api:data-management:api-configuration")
include(":extensions:control-plane:api:data-management:asset-api")
include(":extensions:control-plane:api:data-management:catalog-api")
include(":extensions:control-plane:api:data-management:contractagreement-api")
include(":extensions:control-plane:api:data-management:contractdefinition-api")
include(":extensions:control-plane:api:data-management:contractnegotiation-api")
include(":extensions:control-plane:api:data-management:policydefinition-api")
include(":extensions:control-plane:api:data-management:transferprocess-api")
include(":extensions:control-plane:data-plane-transfer:data-plane-transfer-client")
include(":extensions:control-plane:data-plane-transfer:data-plane-transfer-sync")
include(":extensions:control-plane:http-receiver")
include(":extensions:control-plane:provision:blob-provision")
include(":extensions:control-plane:provision:http-provisioner")
include(":extensions:control-plane:provision:s3-provision")
include(":extensions:control-plane:store:cosmos:assetindex-cosmos")
include(":extensions:control-plane:store:cosmos:contract-definition-store-cosmos")
include(":extensions:control-plane:store:cosmos:contract-negotiation-store-cosmos")
include(":extensions:control-plane:store:cosmos:control-plane-cosmos")
include(":extensions:control-plane:store:cosmos:policy-store-cosmos")
include(":extensions:control-plane:store:cosmos:transfer-process-store-cosmos")
include(":extensions:control-plane:store:sql:asset-index-sql")
include(":extensions:control-plane:store:sql:contract-definition-store-sql")
include(":extensions:control-plane:store:sql:contract-negotiation-store-sql")
include(":extensions:control-plane:store:sql:control-plane-sql")
include(":extensions:control-plane:store:sql:policy-store-sql")
include(":extensions:control-plane:store:sql:transfer-process-store-sql")

include(":extensions:data-plane:data-plane-api")
include(":extensions:data-plane:data-plane-azure-storage")
include(":extensions:data-plane:data-plane-data-factory")
include(":extensions:data-plane:data-plane-http")
include(":extensions:data-plane:data-plane-s3")
include(":extensions:data-plane:integration-tests")

include(":extensions:data-plane-selector:selector-api")
include(":extensions:data-plane-selector:selector-client")

include(":extensions:federated-catalog:store:fcc-node-directory-cosmos")

// modules for launchers, i.e. runnable compositions of the app ------------------------------------
include(":launchers:data-plane-server")
include(":launchers:dpf-selector")
include(":launchers:ids-connector")

// numbered samples for the onboarding experience --------------------------------------------------
include(":samples:01-basic-connector")
include(":samples:02-health-endpoint")
include(":samples:03-configuration")

include(":samples:04.0-file-transfer:consumer")
include(":samples:04.0-file-transfer:provider")
include(":samples:04.0-file-transfer:integration-tests")
include(":samples:04.0-file-transfer:transfer-file")

include(":samples:04.1-file-transfer-listener:consumer")
include(":samples:04.1-file-transfer-listener:file-transfer-listener-integration-tests")
include(":samples:04.1-file-transfer-listener:listener")

include(":samples:04.2-modify-transferprocess:api")
include(":samples:04.2-modify-transferprocess:consumer")
include(":samples:04.2-modify-transferprocess:modify-transferprocess-sample-integration-tests")
include(":samples:04.2-modify-transferprocess:simulator")
include(":samples:04.2-modify-transferprocess:watchdog")

include(":samples:04.3-open-telemetry:consumer")
include(":samples:04.3-open-telemetry:provider")

include(":samples:05-file-transfer-cloud:consumer")
include(":samples:05-file-transfer-cloud:provider")
include(":samples:05-file-transfer-cloud:transfer-file")

// modules for code samples ------------------------------------------------------------------------
include(":samples:other:custom-runtime")

// extension points for a connector ----------------------------------------------------------------
include(":spi:common:auth-spi")
include(":spi:common:catalog-spi")
include(":spi:common:core-spi")
include(":spi:common:identity-did-spi")
include(":spi:common:jwt-spi")
include(":spi:common:oauth2-spi")
include(":spi:common:policy-engine-spi")
include(":spi:common:policy-model")
include(":spi:common:transaction-datasource-spi")
include(":spi:common:transaction-spi")
include(":spi:common:transport-spi")
include(":spi:common:web-spi")

include(":spi:control-plane:contract-spi")
include(":spi:control-plane:control-plane-spi")
include(":spi:control-plane:data-plane-transfer-spi")
include(":spi:control-plane:policy-spi")
include(":spi:control-plane:transfer-spi")

include(":spi:data-plane:data-plane-spi")

include(":spi:data-plane-selector:data-plane-selector-spi")

include(":spi:federated-catalog:federated-catalog-spi")

// modules for system tests ------------------------------------------------------------------------
include(":system-tests:azure-data-factory-tests")
include(":system-tests:azure-tests")
include(":system-tests:e2e-transfer-test:backend-service")
include(":system-tests:e2e-transfer-test:control-plane")
include(":system-tests:e2e-transfer-test:control-plane-cosmosdb")
include(":system-tests:e2e-transfer-test:control-plane-postgresql")
include(":system-tests:e2e-transfer-test:data-plane")
include(":system-tests:e2e-transfer-test:runner")
include(":system-tests:runtimes:azure-data-factory-transfer-consumer")
include(":system-tests:runtimes:azure-data-factory-transfer-provider")
include(":system-tests:runtimes:azure-storage-transfer-consumer")
include(":system-tests:runtimes:azure-storage-transfer-provider")
include(":system-tests:runtimes:file-transfer-consumer")
include(":system-tests:runtimes:file-transfer-provider")
include(":system-tests:tests")