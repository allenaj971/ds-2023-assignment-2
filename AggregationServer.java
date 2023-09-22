import java.net.*;

// This file instantiates the producer-consumer 
// in a new thread. For each new aggregation server-client connection
// or aggregation server-content serve connection the while(true) also 
// starts a new connection to support multiple content servers
// and multiple clients connecting simultaneously. 
public class AggregationServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        ProducerConsumer pc = new ProducerConsumer();
        pc.start();

        try {
            serverSocket = new ServerSocket(3000);
            System.out.println("Starting aggregation server...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                socket = serverSocket.accept();
                new RequestHandler(socket, pc).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}