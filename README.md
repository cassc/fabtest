Test Hyperledger Fabric API performance


# Configuration

Edit `src/main/java/fab/perf/Tools.java` to configure request concurrency, etc.,

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

# Create initial accounts

```bash
gradle -PmainClass=fab.perf.InitAccounts runApp
```

# Test get balance performance

```bash
gradle -PmainClass=fab.perf.BalanceTest runApp
```

# Test send performance

```bash
gradle -PmainClass=fab.perf.SendTest runApp
```
