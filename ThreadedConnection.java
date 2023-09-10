import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.*;
import org.json.*;

public class ThreadedConnection extends Thread {
    private Socket socket;
    private ProducerConsumer pc;

    public ThreadedConnection(Socket connectionSocket, ProducerConsumer prodCon)
    {
        this.socket = connectionSocket;
        this.pc = prodCon;
        
    }

    public void run() 
    {
        DataInputStream requests = null;
        DataOutputStream serverRes = null;

        try {
            requests = new DataInputStream(this.socket.getInputStream());
            serverRes = new DataOutputStream(this.socket.getOutputStream());

            System.out.println("Server: Client is connected.");

        } catch (Exception e) {
            System.err.println("Server initialisation error: " + e.toString());
        }
        
        String request = null;

        while(true)
        {
            try {
                request = requests.readUTF();
                
                if(request.contains("over"))
                {
                    this.socket.close();
                    Thread.currentThread().interrupt();
                    break;
                }
        
                String id = this.pc.addRequest(request);
                JSONObject response = this.pc.getRequest(id);

                serverRes.writeUTF(response.toString());
                serverRes.flush();
            } catch (Exception e) {
                System.err.println("Server error: " + e.toString());
                break;
            }
        }
    }
}
