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

    public Client()
    {
        this.lamportTime = 0;
        this.clientId = Thread.currentThread().getName();
    }

    public static void main()
    {
        BufferedReader terminalinput = new BufferedReader(new InputStreamReader(System.in));

        try {
            // read the command line to find the server name and port number (in URL format)
            System.out.println("Please enter server address and port no with format servername:portnumber");
            // Split the address and port number at the colon
            String[] serverAdd = terminalinput.readLine().split(":");
            // close the terminal input reader
            terminalinput.close();

            // establish a connection to the aggregation server
            Socket socket = new Socket(serverAdd[0], Integer.valueOf(serverAdd[1]));
            System.out.println("Client " + this.clientId.replace("Thread-", "") + " has connected!");

            // create a client request writer variable and server response reader
            // variable
            DataInputStream serverResponse = new DataInputStream(socket.getInputStream());
            DataOutputStream clientRequests = new DataOutputStream(socket.getOutputStream());

            for (int i = 0; i < 3; i++) {
                sendGetRequest();
                String temp = serverResponse.readUTF();
                updateLamportTime(temp);
                System.out.println("Server response: " + temp);
                System.out.println("Client " + Thread.currentThread().getName() + " lamport: " + this.lamportTime);
                Thread.sleep(1000);
            }
            disconnect();
            
        }
        catch (Exception e)
        {
            // print error if client inputs 
            // incorrect address and/or port no
            // or if any other errors occur
            System.err.println(e.toString());
        }
    }

    public void updateLamportTime(String data)
    {
        JSONObject temp = new JSONObject(data);
        this.lamportTime = Integer.parseInt(temp.getString("lamport-timestamp")) > this.lamportTime 
        ? Integer.parseInt(temp.getString("lamport-timestamp")) + 1
        : this.lamportTime + 1;
    }

    public static void sendGetRequest()
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
