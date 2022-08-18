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
 *       Fraunhofer Institute for Software and Systems Engineering
 *       ZF Friedrichshafen AG - add dependency & reorder entries
 *
 */

rootProject.name = "dataspaceconnector"

// modules for common/util code

include(":common:util")
include(":extensions:junit")
include(":common:state-machine-lib")
include(":common:token-generation-lib")
include(":common:token-validation-lib")


// EDC core modules
include(":core")
include(":core:policy:policy-engine")
include(":core:policy:policy-evaluator")
include(":core:transfer")
include(":core:contract")
include(":core:base")
include(":core:defaults")
include(":core:boot")
include(":core:micrometer")

// modules that provide implementations for data ingress/egress
include(":data-protocols:ids:ids-api-multipart-endpoint-v1")
include(":data-protocols:ids:ids-api-multipart-dispatcher-v1")
include(":data-protocols:ids:ids-core")
include(":data-protocols:ids:ids-jsonld-serdes-lib")
include(":data-protocols:ids:ids-spi")
include(":data-protocols:ids:ids-transform-v1")
include(":data-protocols:ids:ids-token-validation")
include(":data-protocols:ids:ids-api-configuration")

// modules for technology- or cloud-provider extensions
include(":extensions:aws")
include(":extensions:api:api-core")
include(":extensions:api:auth-basic")
include(":extensions:api:auth-tokenbased")
include(":extensions:api:data-management:api-configuration")
include(":extensions:api:data-management:asset")
include(":extensions:api:data-management:catalog-api")
include(":extensions:api:data-management:contractdefinition")
include(":extensions:api:data-management:contractnegotiation")
include(":extensions:api:data-management:contractagreement")
include(":extensions:api:data-management:policydefinition")
include(":extensions:api:data-management:transferprocess")
include(":extensions:api:observability")
include(":extensions:aws:data-plane-s3")
include(":extensions:aws:s3:s3-core")
include(":extensions:aws:s3:s3-provision")
include(":extensions:aws:aws-test")
include(":extensions:azure:blobstorage")
include(":extensions:azure:blobstorage:blob-core")
include(":extensions:azure:blobstorage:blob-provision")
include(":extensions:azure:events")
include(":extensions:azure:events-config")
include(":extensions:azure:azure-test")
include(":extensions:azure:cosmos:transfer-process-store-cosmos")
include(":extensions:azure:cosmos:fcc-node-directory-cosmos")
include(":extensions:azure:cosmos:contract-definition-store-cosmos")
include(":extensions:azure:cosmos:policy-store-cosmos")
include(":extensions:azure:cosmos:contract-negotiation-store-cosmos")
include(":extensions:azure:cosmos:cosmos-common")
include(":extensions:azure:cosmos:assetindex-cosmos")
include(":extensions:azure:vault")
include(":extensions:azure:resource-manager")
include(":extensions:azure:data-plane:data-factory")
include(":extensions:azure:data-plane:storage")
include(":extensions:events:cloudevents-http")
include(":extensions:filesystem:configuration-fs")
include(":extensions:filesystem:vault-fs")
include(":extensions:hashicorp-vault")
include(":extensions:iam:iam-mock")
include(":extensions:iam:oauth2:oauth2-core")
include(":extensions:iam:daps")
include(":extensions:iam:decentralized-identity")
include(":extensions:iam:decentralized-identity:identity-did-core")
include(":extensions:iam:decentralized-identity:identity-did-service")
include(":extensions:iam:decentralized-identity:identity-did-web")
include(":extensions:iam:decentralized-identity:identity-did-crypto")
include(":extensions:iam:decentralized-identity:identity-common-test")
include(":extensions:iam:decentralized-identity:dummy-credentials-verifier")
include(":extensions:catalog:federated-catalog-cache")
include(":extensions:dataloading")
include(":extensions:jdk-logger-monitor")
include(":extensions:http")
include(":extensions:http:jersey")
include(":extensions:http:jersey-micrometer")
include(":extensions:http:jetty")
include(":extensions:http:jetty-micrometer")
include(":extensions:transaction")
include(":extensions:transaction:transaction-atomikos")
include(":extensions:transaction:transaction-local")
include(":extensions:data-plane-transfer:data-plane-transfer-sync")
include(":extensions:data-plane-transfer:data-plane-transfer-client")
include(":extensions:data-plane:data-plane-framework")
include(":extensions:data-plane:data-plane-http")
include(":extensions:data-plane-selector")
include(":extensions:data-plane-selector:selector-api")
include(":extensions:data-plane-selector:selector-core")
include(":extensions:data-plane-selector:selector-store")
include(":extensions:data-plane-selector:selector-client")
include(":extensions:azure:data-plane:storage")
include(":extensions:azure:data-plane:data-factory")
include(":extensions:data-plane:data-plane-api")
include(":extensions:data-plane:integration-tests")
include(":extensions:http-receiver")
include(":extensions:http-provisioner")
include(":extensions:sql:asset-index-sql")

include(":extensions:sql:common-sql")
include(":extensions:sql:contract-definition-store-sql")
include(":extensions:sql:contract-negotiation-store-sql")
include(":extensions:sql:lease-sql")
include(":extensions:sql:policy-store-sql")
include(":extensions:sql:pool:apache-commons-pool-sql")
include(":extensions:sql:transfer-process-store-sql")
// modules for launchers, i.e. runnable compositions of the app

include(":launchers:test")
include(":launchers:ids-connector")
include(":launchers:data-loader-cli")
include(":launchers:dpf-selector")
include(":launchers:data-plane-server")
// modules for code samples

include(":samples:other:copy-file-to-s3bucket")
include(":samples:other:dataseed:dataseed-aws")
include(":samples:other:dataseed:dataseed-azure")
include(":samples:other:dataseed:dataseed-policy")
include(":samples:other:run-from-junit")
include(":samples:other:custom-runtime")
// extension points for a connector

include(":spi:common:auth-spi")
include(":spi:common:catalog-spi")
include(":spi:common:core-spi")
include(":spi:common:identity-did-spi")
include(":spi:common:oauth2-spi")
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
include(":spi:data-plane-selector:selector-spi")
include(":spi:federated-catalog:federated-catalog-spi")

// numbered samples for the onboarding experience
include(":samples:01-basic-connector")
include(":samples:02-health-endpoint")
include(":samples:03-configuration")

include(":samples:04.0-file-transfer:consumer")
include(":samples:04.0-file-transfer:provider")
include(":samples:04.0-file-transfer:transfer-file")
include(":samples:04.0-file-transfer:integration-tests")

include(":samples:04.1-file-transfer-listener:consumer")
include(":samples:04.1-file-transfer-listener:listener")
include(":samples:04.1-file-transfer-listener:transfer-file")
include(":samples:04.1-file-transfer-listener:api")
include(":samples:04.1-file-transfer-listener:file-transfer-listener-integration-tests")

include(":samples:04.2-modify-transferprocess:consumer")
include(":samples:04.2-modify-transferprocess:watchdog")
include(":samples:04.2-modify-transferprocess:simulator")
include(":samples:04.2-modify-transferprocess:modify-transferprocess-sample-integration-tests")

include(":samples:04.3-open-telemetry:micrometer")
include(":samples:04.3-open-telemetry:consumer")
include(":samples:04.3-open-telemetry:provider")

include(":samples:05-file-transfer-cloud:consumer")
include(":samples:05-file-transfer-cloud:provider")
include(":samples:05-file-transfer-cloud:transfer-file")

include(":system-tests:e2e-transfer-test:runner")
include(":system-tests:e2e-transfer-test:backend-service")
include(":system-tests:e2e-transfer-test:control-plane")
include(":system-tests:e2e-transfer-test:control-plane-postgresql")
include(":system-tests:e2e-transfer-test:control-plane-cosmosdb")
include(":system-tests:e2e-transfer-test:data-plane")
include(":system-tests:runtimes:file-transfer-provider")
include(":system-tests:runtimes:file-transfer-consumer")
include(":system-tests:tests")
include(":system-tests:runtimes:azure-storage-transfer-provider")
include(":system-tests:runtimes:azure-data-factory-transfer-provider")
include(":system-tests:runtimes:azure-data-factory-transfer-consumer")
include(":system-tests:runtimes:azure-storage-transfer-consumer")
include(":system-tests:azure-tests")
include(":system-tests:azure-data-factory-tests")

include(":tooling:module-domain")
include(":tooling:module-processor")
include(":tooling:module-processor-extension-test")
include(":tooling:module-processor-spi-test")
