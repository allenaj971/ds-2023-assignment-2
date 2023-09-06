// A Java program for a Server
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import org.json.*;

public class NewConnection {
    private Socket connectionSocket;
    public DataInputStream requests;
    public DataOutputStream serverResponse;
    private Integer id;
    private Integer lamportTime;

    public NewConnection(Socket socket)
    {
        try {
            this.lamportTime = 0;
            this.connectionSocket = socket;

            // Have a DataInputStream receives requests from the
            // socket connected to aggregation server
            this.requests = new DataInputStream(socket.getInputStream());

            // sends requests to the socket connecting to the aggregation server
            this.serverResponse = new DataOutputStream(socket.getOutputStream());

        } catch (Exception e) {
            // print out any errors when connecting
            System.err.println(e.toString());
        }
    }

    public String closeConnection()
    {
        // close terminal input, and connection requests and responses 
        // sockets. 
        try {
            connectionSocket.close();
            serverResponse.close();
            requests.close();
            return "Connection closed successfully!";
        }
        catch (Exception e) {
            // Catch any errors when attempting to close the 
            // input and output sockets.
            return e.toString();
        }
    }

}
