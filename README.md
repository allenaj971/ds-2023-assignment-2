<!-- - Refactor Content Server to a server architecture rather than client -->
<!-- - If multiple clients perform GET and PUT, then it must be done in the order of the lamport time 
    1. each part must maintain a lamport time
    2. for each operation, send to FileReadWrite, and it will perform the GET
    or PUT based on lamport clock
    3. increment lamport clock --> done
- Fault tolerance: use probe message with 15-sec timer
    A. AG crashes?
        i. What happens to client and CS?
        a. client check for success response, else failed 
        b. same with CS, check for success response
    B. CS crashes?
        i. What happens to AG?
        a. Check on AG if connection to CS exists, if not:
        b. AG deletes requests associated with CS
        c. AG also deletes weather data associated with CS
        d. Perform heartbeat thing every 15 seconds by checking each socket every 15 seconds
    C. Client crashes?
        i. What happens to AG?
        a. Check on AG if connection to client exists, if not: 
        b. AG deletes requests associated with client
    D. CS inputs invalid data for weather data?
        i. AG should check 
    E. CS inputs invalid file location for weather json?
        i. Client should return no file found and try again

- TESTING!
- UPDATE README + DOCUMENTATION!

-	What happens when AG (Aggregation Server) crashes?
o	Client and content server use adaptive timeouts by sending a probe message every 15 seconds.
o	Since AG crashes, then client and content server know it is disconnected and stop sending requests.
o	Possibility of read failure for GET request?
	We check if read data is not null, if it is then we send a request back to client stating that there was a file read failure
o	Possibility of write failure for PUT request?
	Check if data written to file matches the data in the request. If not, then send a request back to content server stating that there was a write file failure
-	What happens when CS (Content Server) crashes?
o	AG adaptive timeouts by sending a probe message every 15 seconds.
o	Since CS crashes, AG knows that CS has crashed, so the probe message has no response, and AG can delete requests from that CS.
-	What happens when Client crashes?
o	AG adaptive timeouts by sending a probe message every 15 seconds.
o	Since Client crashes, AG knows that Client has crashed, so the probe message has no response, and AG can delete requests from that client.


You can use existing XML parsers and CS will send content to AS using PUT. AS will receive GET request from client and will send client new feed. Each CS can have a heart beat message in every 15 seconds to let the AS know if the CS is alive. The client also needs to have that fault tolerance mechanism.