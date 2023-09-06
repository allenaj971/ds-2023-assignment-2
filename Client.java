// A Java program for a Client
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import org.json.*;

public class Client {
    private Socket socket;
    private DataInputStream serverResponse;
    private DataOutputStream clientRequests;
    private Integer lamportTime;

    public Client(String address, int port)
    {
        try {
            // establish a connection
            socket = new Socket(address, port);
            System.out.println("Connected");

            serverResponse = new DataInputStream(socket.getInputStream());
            clientRequests = new DataOutputStream(socket.getOutputStream());

            sendGetRequest();
            // Thread.sleep(1000);
            // sendGetRequest();
            System.out.println(serverResponse.readUTF());
            disconnect();
            
        }
        catch (Exception e)
        {
            // catch & print out any errors
            System.err.println(e);
        }
        
    }

    public void sendGetRequest()
    {   
        try {
            // create hashmap 
            Map<String, String> data = new HashMap<>();
            // place request type into map
            data.put("request-type", "GET /weather.json HTTP/1.1");
            data.put("user-agent", "CLIENT");
            data.put("content-type", "NONE");
            data.put("content-length", "0");
            data.put("lamport-timestamp", "2");
            
            clientRequests.writeUTF(mapToString(data));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public String mapToString(Map<String,String> data)
    {
        JSONObject obj = new JSONObject(data);
        return obj.toString();
    }

    public void disconnect()
    {
        try {
            // create hashmap 
            Map<String, String> data = new HashMap<>();
            // place request type into map
            data.put("request-type", "over");

            clientRequests.writeUTF(mapToString(data));
            System.out.println(serverResponse.readUTF());

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
 
    public static void main(String args[])
    {
        // initialise the client 
        Client c1 = new Client("127.0.0.1", 3000);
    }
}
