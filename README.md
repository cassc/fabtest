Test Hyperledger Fabric API performance


* Start the test network and deploy our chaincode:

```sh
# create a working directory
mkdir ~/fab-samples
cd ~/fab-samples

# Clone fabric-sample repository
git clone https://github.com/cassc/fabric-samples

# Install fabric samples
curl -sSL https://bit.ly/2ysbOFE | bash -s -- 2.3.0 1.4.9

cd fabric-samples/test-network

# clean start test-network
./network.sh down
./network.sh up -ca

# create default channel
./network.sh createChannel

# deploy the modified asset-transfer-basic chaincode
./network.sh deployCC -ccn basic -ccp ../asset-transfer-basic/chaincode-java -ccl java
```



* Run the API service,

```sh
cd ../asset-transfer-basic/application-java
rm -rf wallet
gradle run
```

This will start a HTTP service, please keep the terminal open.


* To run our tests, open another terminal window,

```sh
cd ~/fab-samples
git clone https://github.com/cassc/fabtest
cd fabtest


```sh
# Create some accounts for use in later tests
gradle -PmainClass=fab.perf.InitAccounts runApp


# Test get balance performance
gradle -PmainClass=fab.perf.BalanceTest runApp

# Test send performance
gradle -PmainClass=fab.perf.SendTest runApp
```

To configure our tests, e.g., concurrency, number of tests to run, edit
`src/main/java/fab/perf/Tools.java`:

```java
// number of accounts to create
public static final int NUM_ACCOUNTS = 10;

// total number of requests to send
public static final int MAX_REQS = 100;

// number of threads to use to perform the test, set to 0 to use cpu_count
public static final int NUM_THREADS = 0;

// print the results from each requests
public static final boolean enablePrint = true;

// path to public and privates used in the test
public static final String PUB_KEY_PATH = "openssl/publickey.pem";
public static final String PRV_KEY_PATH = "openssl/privatekey-pkcs8.pem";
```
