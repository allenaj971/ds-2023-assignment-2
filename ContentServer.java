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
    private Integer lamportTime;

    public ContentServer(String address, int port)
    {
        try {
            // establish a connection
            socket = new Socket(address, port);
            System.out.println("Connected");

            serverResponse = new DataInputStream(socket.getInputStream());
            contentServerRequests = new DataOutputStream(socket.getOutputStream());
            sendPutRequest();
            // Thread.sleep(1000);
            // sendGetRequest();
            // disconnect();
            System.out.println(serverResponse.readUTF());
            disconnect();
        }
        catch (Exception e)
        {
            // catch & print out any errors
            System.err.println(e);
        }

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
            data.put("content-server-id", "1");
            data.put("user-agent", "ATOMClient/1/0");
            data.put("content-type", "JSONSTRING");
            data.put("content-length", "500");
            data.put("lamport-timestamp", "1");
            data.put("data", new String(Files.readAllBytes(Paths.get("ContentServer.json"))));
            // send request to server and print response
            contentServerRequests.writeUTF(mapToString(data));
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

            contentServerRequests.writeUTF(mapToString(data));
            System.out.println(serverResponse.readUTF());

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public static void main(String args[])
    {
        // initialise the client 
        ContentServer cs1 = new ContentServer("127.0.0.1", 3000);
    }
}
