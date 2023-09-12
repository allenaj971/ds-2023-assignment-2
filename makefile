# extract JSON jar file
jsonjar: json-20230618.jar
	jar xf json-20230618.jar

# the compile command compiles all java files
compile: *.java
	javac -cp "./json-20230618.jar" -d ./ *.java 

# clean up compiled files
clean: *.class
	rm *.class -rf

# run client 
client: Client.class
	java -cp ./ Client

# run aggregation server
aggregation: AggregationServer.class
	javac -cp "./json-20230618.jar" -d ./ *.java && java -cp ./ AggregationServer

# run content server 
conserve: ContentServer.class
	java -cp ./ ContentServer

# run test
test: Test.class
	java -cp ./ Test > Output.json

# kill process to ensure address is not in use
# kill -9 <pid>
# lsof -i:<pid>
	

# # client runs the client, takes input from the TestInput files
# client: GETClient.class
# 	java -cp ./ GETClient > Output.txt

# # this compares the output of the program with the 2 expected outputs 
# # the reason for 2 expected outputs is because the pop command in line 6
# # of TestInput1.txt could print first or the isEmpty in line 6 of TestInput3.txt 
# outputCompare: 
# 	diff Output.txt ExpectedOutput1.txt & diff Output.txt ExpectedOutput2.txt
