# Instructions to run a file-transfer example locally

In this tutorial, we deploy 2 eclipse dataspace connectors (EDC): a provider EDC that shares a file, and a consumer EDC that acquires the file. 

## Prerequisites

To run this example, you need:

- Docker
- Postman
- 2 Pre-signed URLs (Java code to generate [pre-signed URLs for AWS S3](https://code.siemens.com/-/snippets/2984)):
    1. Pre-signed URL for download (for HTTP GET)
    1. Pre-signed URL for upload (for HTTP PUT)

## Important Notes

- The configuration file of the provider EDC (i.e., `dataspaceconnector-configuration.properties`) is located in `DataSpaceConnector-Fork/samples/other/file-transfer-http-to-http/local-deployment/provider-config/`.
- Similarly, the configuration file of the EDC consumer is in `DataSpaceConnector-Fork/samples/other/file-transfer-http-to-http/local-deployment/consumer-config/`.
- In case the connectors are deployed without the provided `docker-compose.yml` file (see section Deployment), make sure to adjust the webhood addresses (inside `dataspaceconnector-configuration.properties`) of both the provider EDC and the consumer EDC. Otherwise, the file transfer will not work.

## Deployment

1. Clone/download the repository.
1. Open a Terminal window and browse to `DataSpaceConnector-Fork/samples/other/file-transfer-http-to-http/local-deployment/`
1. Run `docker-compose up &`. It will take about 10-15 minutes to build the images. As soon as the images are built, this command will start 3 containers:
    - Provider EDC
    - Consumer EDC
    - Digital Twin Registry

## File-transfer example

1. Start Postman and import the collection `DataSpaceConnector-Fork/samples/other/file-transfer-http-to-http/local-deployment/catenax-edc-local.postman_collection.json`.

1. In the `Variables` tab of Postman change the following values:
    - `fileName`: Should be the name of the file to be transfered.
    - `downloadFileURL`: Should be the provider URL of the file, e.g., a pre-signed AWS URL of a file in the provider's bucket.
    - `uploadFileURL`: Should be the consumer URL of the file, e.g., a pre-signed AWS URL for the consumer's bucket.

1. In the folder Provider, we can do the following requests:

    1. Health check: To check if the provider is alive.
    1. Create asset: To create an asset for the file that we want to share.
    1. Get all assets: To list all the assets. The asset we created has id = 0002.
    1. Create policy: To create a new policy for our asset.
    1. Get all policies: To list all the policies. The policy for our asset has target = 0002.
    1. Create Contract: To create a new contract for our asset.
    1. Get all contracts: To list all the contracts. The contract we created has id = 0002 and the contractPolicyId that we put in step 4.
    1. (Optional) Register digital twin: To register the asset with a digital twin registry. Note that the `specificAssetIds` field contains key-value pairs that can be used for lookup operations later on, i.e., to search for assets in the digital twin registry.

1. In the folder Consumer, we can do the following requests:

    1. Health check: To check if the consumer is alive.
    1. (Optional) Digital twin lookup: Search operation for finding assets in the digital twin registry. Specify search terms as key-value pairs in the `assetIds` parameter. Returns the digital twin id of assets that match the search terms.
    1. (Optional) Get digital twin: To find the asset in the digital twin registry (based on the digital twin id). In the response, you can see the URL of the provider EDC and the asset id (in the EDC).
    1. Get offers: To list the assets of the provider EDC. In the response, find the contract offer that has `asset:prop:id` = 0002 (which we created earlier), and take a note of the contract offer id (looks like this: `"0002:10955e81-8362-4c16-b66e-9ef91a0df522"`).
    1. Negotiate contract: To request the asset from the provider. Copy-paste the contract offer `id` from the previous step to the field `offerId`. After sending this request, take a note of the negotiation id which is included in the response.
    1. Get contract ID: To get the contract agreement ID, modify the URL of this request, and add the negotiation id from the previous step, i.e., `http://localhost:9192/api/v1/data/contractnegotiations/{negotiation id}`. In the response, take a note of the `contractAgreementId`.
    1. Inititate transfer: To start the file transfer. Copy-paste the `contractAgreementId` from the previous step to the `contractId` field. The process to tranfer the file usually takes few seconds (depends also on the file size).
    1. Get status of all transfers: To check the status of all the transfers. 

1. The file should now be transfered to the consumer.