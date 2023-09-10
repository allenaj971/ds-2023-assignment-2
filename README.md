#Key features list
- Aggregation server stores 20 weather data updates: DONE
- If multiple clients perform GET and PUT, then it must be done in the order of the lamport time
    1. each part must maintain a lamport time
    2. for each operation, send to FileReadWrite, and it will perform the GET
    or PUT based on lamport clock
    3. increment lamport clock
- Remove content from a content server that it has not communicated with within the last 15 seconds
    1. perform heartbeat thing every 15 seconds by checking each socket every 15 seconds

You can use existing XML parsers and CS will send content to AS using PUT. AS will receive GET request from client and will send client new feed. Each CS can have a heart beat message in every 15 seconds to let the AS know if the CS is alive. The client also needs to have that fault tolerance mechanism.