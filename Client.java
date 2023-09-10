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
    private String clientId;
    private Integer lamportTime;

    public Client(String address, int port, String id)
    {
        this.lamportTime = 0;
        this.clientId = id;
        try {
            // establish a connection
            socket = new Socket(address, port);
            System.out.println("Client " + this.clientId.replace("Thread-", "") + " has connected!");

            serverResponse = new DataInputStream(socket.getInputStream());
            clientRequests = new DataOutputStream(socket.getOutputStream());

            for (int i = 0; i < 2; i++) {
                sendGetRequest();
                String temp = serverResponse.readUTF();
                updateLamportTime(temp);
                System.out.println("Server response: " + temp);
                System.out.println("Client " + Thread.currentThread().getName() + " lamport: " + this.lamportTime);
            }
            disconnect();
            
        }
        catch (Exception e)
        {
            // catch & print out any errors
            System.err.println(e);
        }
        finally
        {
            try {
                socket.close();
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    public void updateLamportTime(String data)
    {
        JSONObject temp = new JSONObject(data);
        this.lamportTime = Integer.parseInt(temp.getString("lamport-timestamp")) > this.lamportTime 
        ? Integer.parseInt(temp.getString("lamport-timestamp")) + 1
        : this.lamportTime + 1;
    }

    public void sendGetRequest()
    {   
        try {
            // create hashmap 
            Map<String, String> data = new HashMap<>();
            // place request type into map
            data.put("request-type", "GET /weather.json HTTP/1.1");
            data.put("lamport-timestamp", String.valueOf(this.lamportTime));
            data.put("client-id", "Client " + this.clientId);
            
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
            data.put("lamport-timestamp", String.valueOf(this.lamportTime));
            data.put("client-id", "Client " + this.clientId);

            clientRequests.writeUTF(mapToString(data));
            // System.out.println(serverResponse.readUTF());

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
