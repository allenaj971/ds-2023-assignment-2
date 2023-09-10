// A Java program for a Server
import java.net.*;
 
public class AggregationServer
{
    public static void main(String args[])
    {  
        ServerSocket serverSocket = null;
        Socket socket = null;
        ProducerConsumer pc = new ProducerConsumer();
        pc.start();

        try {
            serverSocket = new ServerSocket(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
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