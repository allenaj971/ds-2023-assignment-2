import java.io.BufferedReader;
import java.net.*;

// This file instantiates the producer-consumer 
// in a new thread. For each new aggregation server-client connection
// or aggregation server-content serve connection the while(true) also 
// starts a new connection to support multiple content servers
// and multiple clients connecting simultaneously. 
public class AggregationServer extends Thread
{
    public static void main(String[] args)
    {
        ServerSocket serverSocket = new ServerSocket(3000);
        System.out.println("Starting aggregation server...");
    }
}