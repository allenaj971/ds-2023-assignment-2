import java.net.*;

// This file instantiates the producer-consumer 
// in a new thread. For each new aggregation server-client connection
// or aggregation server-content serve connection the while(true) also 
// starts a new connection to support multiple content servers
// and multiple clients connecting simultaneously. 
public class AggregationServer
{
    public static void main(String args[])
    {  
        ServerSocket serverSocket = null;
        Socket socket = null;
        // create a new producer-consumer object
        // and start it in a new thread. we only create 1 instance
        // since we want to perform 
        ProducerConsumer pc = new ProducerConsumer();
        pc.start();

        // start a new server socket
        try {
            serverSocket = new ServerSocket(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // while true, check for new socket connections
        // between aggregation server and content server
        // or aggregation server and client and start
        // the new socket connection in a new thread.
        while(true)
        {
            try {
                socket = serverSocket.accept();
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ThreadedConnection(socket, pc).start();
        }
    }
}