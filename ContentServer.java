// A Java program for a Client
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.json.*;

public class ContentServer {
    private Socket socket;
    private DataInputStream serverResponse;
    private DataOutputStream contentServerRequests;
    private String contentServerId; 
    private Integer lamportTime;

    public ContentServer(String address, int port, int id)
    {
        this.contentServerId = String.valueOf(id);
        this.lamportTime = 0;
        try {
            // establish a connection
            socket = new Socket(address, port);
            System.out.println("ContentServer " + this.contentServerId + " has connected!");

            serverResponse = new DataInputStream(socket.getInputStream());
            contentServerRequests = new DataOutputStream(socket.getOutputStream());
            
            for (int i = 0; i < 2; i++) {
                sendPutRequest();
                String temp = serverResponse.readUTF();
                updateLamportTime(temp);
                System.out.println("Server response: " + temp);
                System.out.println("ContentServer " + Thread.currentThread().getName() + " lamport: " + this.lamportTime);
            }
            disconnect();
        }
        catch (Exception e)
        {
            // catch & print out any errors
            System.err.println(e);
        }
    }

    public void updateLamportTime(String data)
    {
        JSONObject temp = new JSONObject(data);
        this.lamportTime = Integer.parseInt(temp.getString("lamport-timestamp")) > this.lamportTime 
        ? Integer.parseInt(temp.getString("lamport-timestamp")) + 1
        : this.lamportTime + 1;
    }
    

    public void sendPutRequest()
    {   
        // PUT /weather.json HTTP/1.1
        // User-Agent: ATOMClient/1/0
        // Content-Type: (You should work this one out)
        // Content-Length: (And this one too)

        // {
        //     "id" : "IDS60901",
        // ...
        // (data)
        // ...
        //     "wind_spd_kt": 8
        // }   
        // create hashmap 

        // read weather data
        try {
            Map<String, String> data = new HashMap<>();
            // place request type into map
            data.put("request-type", "PUT /weather.json HTTP/1.1");
            data.put("user-agent:","ATOMClient/1/0");
            data.put("client-id", "ContentServer " + contentServerId);
            data.put("content-type", "JSONSTRING");
            data.put("content-length", "500");
            data.put("lamport-timestamp", String.valueOf(this.lamportTime));
            data.put("data", new String(Files.readAllBytes(Paths.get("ContentServer" + this.contentServerId + ".json"))));
            // send request to server and print response
            contentServerRequests.writeUTF(mapToString(data));
            this.lamportTime += 1;
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
            data.put("client-id", "ContentServer " + contentServerId);
            
            contentServerRequests.writeUTF(mapToString(data));

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
