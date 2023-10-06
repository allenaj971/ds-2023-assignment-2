## Introduction

This is the readme file for Assignment 2 for COMP SCI 3012. In this assignment, I have built the aggregation system described below, including a failure management system to deal with as many of the possible failure modes that I can think of for this problem. This obviously includes client, server and network failure, but now I must deal with the following additional constraints:

1. Multiple clients may attempt to GET simultaneously and are required to GET the aggregated feed that is correct for the Lamport clock adjusted time if interleaved with any PUTs. Hence, if A PUT, a GET, and another PUT arrive in that sequence then the first PUT must be applied and the content server advised, then the GET returns the updated feed to the client then the next PUT is applied. In each case, the participants will be guaranteed that this order is maintained if they are using Lamport clocks.
2. Multiple content servers may attempt to simultaneously PUT. This must be serialised and the order maintained by Lamport clock timestamp.
3. The aggregation server will expire and remove any content from a content server that it has not communicated within the last 30 seconds.
4. All element must be capable of implementing Lamport clocks, for synchronization and coordination purposes.

## How to manually perform HTTP GET requests & PUT requests

- You must first run "make compile" to compile all the java files
- Then you must run "make aggregation" to run the aggregation server in a terminal window. You should be able to see the incoming requests in the terminal window.
- To test the GET request, you must run "make client" in another terminal window. You should be able to see the outgoing GET requests in the terminal window.
- To test the PUT request, you must run "make conserve" in another terminal window. You should be able to see the outgoing PUT requests in the terminal window.

## How I implemented lamport clock sync and implementation

- The aggregation server, content server and client maintain their own lamport clocks.
- After each request performed (either GET request by the client or PUT request by the content server), they increment their own clocks.
- When the aggregation server receives the GET/PUT request, it takes the maximum of its own clock and the incoming request and then increments its own clock.
- When the client/content server receives a response from the aggregation server, it takes the max of its own timestamp and then increments its own.
- You will see this algorithm implemented in the lamport timestamps in the requests and responses in the terminal windows for the aggregation server, content server and client when you compile and run them.

## Testing:

1.) Aggregation Server:
i.) Testing GET & PUT requests - To test GET and PUT request functionality, I will perform a request from the client and content server, and receive a response from the aggregation server. Then the client and content server will compare its received response versus the expected response required from the aggregation server.
ii.) If the weather data json file is missing when a GET request is performed - The aggregation server should return a 500 not found with a message stating that the weather data was not found. Then the client will compare the server response received with the expected server response to determine if the server responded correctly.
iii.) If the weather data format is not json - The aggregation server should return 500 invalid data format. 
iv.) If the weather data json file is missing when a PUT request is performed - The aggregation server should response with 201 file created.
v.) If the GET/PUT request was successful - The aggregation server should return 200 OK.

## Fault Tolerance

1.) Aggregation Server Failure
i.) What the content server will do - The content server will check if it is connected each time before it performs the request. If the aggregation server is disconnected, then it will cancel the request and display an error stating "aggregation server disconnected".
ii.) What the client will do - The client will check if it is connected each time before it performs the request. If the aggregation server is disconnected, then it will cancel the request and display an error stating "aggregation server disconnected".
2.) Content Server Failure
i.) What the Content Server will do - It will attempt to save the weather data content data into a file and attempt to close the content server.
3.) Client Failure
i.) What the aggregation server will do - It will attempt to close the client.

## Assignment 2 Checklist

Here in the Assignment 2 checklist, I have described how to test my program. To run any of the PUT and GET tests, you must first run "make aggregation" which will run the aggregation server. Then, you can run any of the GET and PUT tests.

# AUTOMATED TEST COMMANDS: BASIC FUNCTIONALITY

# CLIENT PROCESSES START UP AND COMMUNICATE

client:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ Client

# AGGREGATION SERVER PROCESSES START UP AND COMMUNICATE

aggregation:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ AggregationServer

# CONTENT SERVER PROCESSES START UP AND COMMUNICATE

conserve:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ ContentServer

# TEXT SENDING WORKS & PUT OPERATION WORKS FOR 1 CONTENTSERVER

testputrequest:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ PUTTest < putTestInput.txt

# GET OPERATION WORKS FOR MANY CLIENTS

testgetrequest:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ GETTest < getTestInput.txt

# AGGREGATION EXPUNGING EXPIRED DATA WORKS (30s)

testdataexpunge:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ DataExpunge < dataExpungeInput.txt

# RETRY FUNCTIONALITY WORKS

To see the retry functionality, you must run "make client" first and you will see the retry prompt, then run "make aggregation" and it will reconnect to aggregation server.

# AUTOMATED TEST COMMANDS: FULL FUNCTIONALITY

Lamport clocks are implemented, and you can see the requests and their lamport timestamps in the aggregation server and the output when you run 'make lamporttesting' in another terminal, and you will see that the responses are sent back in lamport timestamp order.

lamporttesting:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ LamportTesting < LamportTestingInput.txt

# Content servers are replicated and fault tolerant

Content servers are fault tolerant (as they can retry if the aggregation server disconnects) as the retry functionality works however, my implementaion is failing to save the contents, so it is not replicated. Multiple, concurrent PUT and GET requests you must run this command to concurrently run multiple GET clients and PUT requests.

multipleput:
make testputrequest && make testgetrequest

# All error codes are implemented:

# 200, 201 and 400 work

# SUCCESS 201: works

test201request:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ Test201req < test201reqInput.txt

# INVALID REQUEST TYPE 400: works

test400request:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ Test400req

# ERROR 500: does not work, gets hung up

testinvalidjson:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ InvalidJSON < InvalidJSONinput.txt

# ERROR 204: does not work, gets hung up

testemptyjson:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ EmptyJSON < EmptyJSON.json
