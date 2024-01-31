# Distributed Systems COMP SCI 3012 - Semester 2, 2023: Assignment 2
## ASSIGNMENT DESCRIPTION
### Objective
Assignment Description
Objective
To gain an understanding of what is required to build a client/server system, you are to build a system that aggregates and distributes weather data in JSON format using a RESTful API.

Introduction
A RESTful API is an interface that computer systems use to exchange information securely over the Internet. Most business applications have to communicate with other internal and third-party applications to perform various tasks. A RESTful APIs support this information exchange because they follow secure, reliable, and efficient software communication standards.

The application programming interface (API) defines the rules that you must follow to communicate with other software systems. Developers expose or create APIs so that other applications can communicate with their applications programmatically. Representational State Transfer (REST) is a software architecture that imposes conditions on how an API should work. REST was initially created as a guideline to manage communication on a complex network like the internet. You can use REST-based architecture to support high-performing and reliable communication at scale.

Two important principles of the REST architectural style are:

Uniform interface: it indicates that the server transfers information in a standard format. The formatted resource is called a representation in REST. This format can be different from the internal representation of the resource on the server application. In this assignment, the format used is the JSON standard.
Statelessness: refers to a communication method in which the server completes every client request independently of all previous requests. Clients can request resources in any order, and every request is stateless or isolated from other requests. 
The basic function of a RESTful API is the same as browsing the internet. The client contacts the server by using the API when it requires a resource. API developers explain how the client should use the REST API in the server application API documentation.

It is common now to use the JSON standard and existing HTTP mechanisms to send messages. For this assignment you can use socket-based communication between client and server and do not need to use the Java RMI mechanism to support it - as you would expect as you don't have to use an RMI client to access a web page! So you need to read the input data and convert it into JSON format and then send it to a server. The server will check it and then distribute data to every client who connects and asks for it. When you want to change the data in the server, you overwrite the existing file, which makes the update operation idempotent (you can do it as many times as you like and get the same result). The real test of your system will be that you can accept PUT and GET requests from other students on your server and your clients can talk to them. However, don't share the code with the other students :)

JSON standard
JavaScript Object Notation (JSON) is an open standard file format and data interchange format that uses human-readable text to store and transmit data objects consisting of attribute–value pairs and arrays (or other serializable values). It is a common data format with diverse uses in electronic data interchange, including that of web applications with servers.

The following example shows a possible JSON representation describing current weather information.

{
    "id" : "IDS60901",
    "name" : "Adelaide (West Terrace /  ngayirdapira)",
    "state" : "SA",
    "time_zone" : "CST",
    "lat": -34.9,
    "lon": 138.6,
    "local_date_time": "15/04:00pm",
    "local_date_time_full": "20230715160000",
    "air_temp": 13.3,
    "apparent_t": 9.5,
    "cloud": "Partly cloudy",
    "dewpt": 5.7,
    "press": 1023.9,
    "rel_hum": 60,
    "wind_dir": "S",
    "wind_spd_kmh": 15,
    "wind_spd_kt": 8
}  
The server, once configured, will serve out this JSON formatted file to any client that requests it over HTTP. Usually, this would be part of a web-client but, in this case, you will be writing the aggregation server, the content servers and the read clients. The content server will PUT content on the server, while the read client will GET content from the server.

Elements
The main elements of this assignment are:

An aggregation server that responds to client requests for weather data and also accepts new weather updates from content servers. The aggregation server will store weather information persistently, only removing it when the content server who provided it is no longer in contact, or when the weather data is too old (e.g. not one of the most recent 20 updates).
A client that makes an HTTP GET request to the server and then displays the weather data.
A content server that makes an HTTP PUT request to the server and then uploads new weather data to the server, replacing the old one. This information is assembled into JSON after being read from a file on the content server's local filesystem.
All code elements will be written in the Java programming language. Your clients are expected to have a thorough failure handling mechanism where they behave predictably in the face of failure, maintain consistency, are not prone to race conditions and recover reliably and predictably.

 

Summary of this assignment
In this assignment, you will build the aggregation system described below, including a failure management system to deal with as many of the possible failure modes that you can think of for this problem. This obviously includes client, server and network failure, but now you must deal with the following additional constraints (come back to these constraints after you read the description below):

Multiple clients may attempt to GET simultaneously and are required to GET the aggregated feed that is correct for the Lamport clock adjusted time if interleaved with any PUTs. Hence, if A PUT, a GET, and another PUT arrive in that sequence then the first PUT must be applied and the content server advised, then the GET returns the updated feed to the client then the next PUT is applied. In each case, the participants will be guaranteed that this order is maintained if they are using Lamport clocks.
Multiple content servers may attempt to simultaneously PUT. This must be serialised and the order maintained by Lamport clock timestamp.
Your aggregation server will expire and remove any content from a content server that it has not communicated within the last 30 seconds. You may choose the mechanism for this but you must consider efficiency and scale.
All elements in your assignment must be capable of implementing Lamport clocks, for synchronization and coordination purposes.
 

Your Aggregation Server
To keep things simple, we will assume that there is one file in your filesystem which contains a list of entries and where are they come from. It does not need to be an JSON format specifically, but it must be able to convert to a standard JSON file when the client sends a GET request. However, this file must survive the server crashing and re-starting, including recovering if the file was being updated when the server crashed! Your server should restore it as was before re-starting or a crash. You should, therefore, be thinking about the PUT as a request to handle the information passed in, possibly to an intermediate storage format, rather than just as overwriting a file. This reflects the subtle nature of PUT - it is not just a file write request! You should check the feed file provided from a PUT request to ensure that it is valid. The file details that you can expect are detailed in the Content Server specification.

All the entities in your system must be capable of maintaining a Lamport clock.

The first time weather data is received and the storage file is created, you should return status 201 - HTTP_CREATED. If later uploads (updates) are successful, you should return status 200. (This means, if a Content Server first connects to the Aggregation Server, then return 201 as succeed code, then before the content server lost connection, all other succeed response should use 200). Any request other than GET or PUT should return status 400 (note: this is not standard but to simplify your task). Sending no content to the server should cause a 204 status code to be returned. Finally, if the JSON data does not make sense (incorrect JSON) you may return status code 500 - Internal server error.

Your server will, by default, start on port 4567 but will accept a single command line argument that gives the starting port number. Your server's main method will reside in a file called AggregationServer.java.

Your server is designed to stay current and will remove any items in the JSON that have come from content servers which it has not communicated with for 30 seconds. How you do this is up to you but please be efficient!

Your GET client
Your GET client will start up, read the command line to find the server name and port number (in URL format) and optionally a station ID; it will then send a GET request for the weather data. This data will then be stripped of JSON formatting and displayed, one line at a time, with the attribute and its value. Your GET client's main method will reside in a file called GETClient.java. Possible formats for the server name and port number include "http://servername.domain.domain:portnumber", "http://servername:portnumber" (with implicit domain information) and "servername:portnumber" (with implicit domain and protocol information).

You should display the output so that it is easy to read but you do not need to provide active hyperlinks. You should also make this client failure-tolerant and, obviously, you will have to make your client capable of maintaining a Lamport clock.

Your Content Server
Your content server will start up, reading two parameters from the command line, where the first is the server name and port number (as for GET) and the second is the location of a file in the file system local to the Content Server (It is expected that this file located in your project folder). The file will contain a number of fields that are to be assembled into JSON format and then uploaded to the server. You may assume that all fields are text and that there will be no embedded HTML or XHMTL. The list of JSON elements that you need to support are shown in the example above.

Input file format
To make parsing easier, you may assume that input files will follow this format:

id:IDS60901
name:Adelaide (West Terrace /  ngayirdapira)
state: SA
time_zone:CST
lat:-34.9
lon:138.6
local_date_time:15/04:00pm
local_date_time_full:20230715160000
air_temp:13.3
apparent_t:9.5
cloud:Partly cloudy
dewpt:5.7
press:1023.9
rel_hum:60
wind_dir:S
wind_spd_kmh:15
wind_spd_kt:8
An entry is terminated by either another entry keyword, or by the end of file, which also terminates the feed. You may reject any feed or entry with no id as being in error. You may ignore any markup in a text field and just print it as is.

 

PUT message format
Your PUT message should take the format:

PUT /weather.json HTTP/1.1
User-Agent: ATOMClient/1/0
Content-Type: (You should work this one out)
Content-Length: (And this one too)

{
    "id" : "IDS60901",
...
(data)
...
    "wind_spd_kt": 8
}   
Your content server will need to confirm that it has received the correct acknowledgment from the server and then check to make sure that the information is in the feed as it was expecting. It must also support Lamport clocks.

## Failure management and testing 
I have built the aggregation system described below, including a failure management system to deal with as many of the possible failure modes that I can think of for this problem. This obviously includes client, server and network failure, but now I must deal with the following additional constraints:

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

### AUTOMATED TEST COMMANDS: BASIC FUNCTIONALITY

### CLIENT PROCESSES START UP AND COMMUNICATE

client:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ Client

### AGGREGATION SERVER PROCESSES START UP AND COMMUNICATE

aggregation:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ AggregationServer

### CONTENT SERVER PROCESSES START UP AND COMMUNICATE

conserve:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ ContentServer

### TEXT SENDING WORKS & PUT OPERATION WORKS FOR 1 CONTENTSERVER

testputrequest:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ PUTTest < putTestInput.txt

### GET OPERATION WORKS FOR MANY CLIENTS

testgetrequest:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ GETTest < getTestInput.txt

### AGGREGATION EXPUNGING EXPIRED DATA WORKS (30s)

testdataexpunge:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ DataExpunge < dataExpungeInput.txt

### RETRY FUNCTIONALITY WORKS

To see the retry functionality, you must run "make client" first and you will see the retry prompt, then run "make aggregation" and it will reconnect to aggregation server.

### AUTOMATED TEST COMMANDS: FULL FUNCTIONALITY

Lamport clocks are implemented, and you can see the requests and their lamport timestamps in the aggregation server and the output when you run 'make lamporttesting' in another terminal, and you will see that the responses are sent back in lamport timestamp order.

lamporttesting:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ LamportTesting < LamportTestingInput.txt

### Content servers are replicated and fault tolerant

Content servers are fault tolerant (as they can retry if the aggregation server disconnects) as the retry functionality works however, my implementaion is failing to save the contents, so it is not replicated. Multiple, concurrent PUT and GET requests you must run this command to concurrently run multiple GET clients and PUT requests.

multipleput:
make testputrequest && make testgetrequest

### All error codes are implemented:

### 200, 201 and 400 work

### SUCCESS 201: works

test201request:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ Test201req < test201reqInput.txt

### INVALID REQUEST TYPE 400: works

test400request:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ Test400req

### ERROR 500: does not work, gets hung up

testinvalidjson:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ InvalidJSON < InvalidJSONinput.txt

### ERROR 204: does not work, gets hung up

testemptyjson:
javac -cp "./json-20230618.jar" -d ./ \*.java && java -cp ./ EmptyJSON < EmptyJSON.json
