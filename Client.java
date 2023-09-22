
// A Java program for a Client
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import org.json.*;

public class Client extends Thread {
    private Socket socket;
    private DataInputStream serverResponse;
    private PrintWriter clientRequests;
    private String clientId;
    private Integer lamportTime;
    private String address;
    private String port;

    public Client() {
        this.lamportTime = 0;
        this.clientId = Thread.currentThread().getName();
    }

    @Override
    public void run() {
        // get terminal input for server address and port
        BufferedReader terminalinput = new BufferedReader(new InputStreamReader(System.in));

        try {
            // // read the command line to find the server name and port number (in URL
            // format)
            // System.out.println("Client: Please enter server address and port no with
            // format servername:portnumber");
            // // Split the address and port number at the colon
            // String[] serverAdd = terminalinput.readLine().split(":");
            // // close the terminal input reader
            // terminalinput.close();

            // this.address = serverAdd[0];
            // this.port = serverAdd[1];

            // // establish a connection to the aggregation server
            // if(this.address != null && this.port != null)
            // {
            // socket = new Socket(serverAdd[0], Integer.valueOf(serverAdd[1]));
            // }
            // else
            // {
            for (int i = 0; i < 3; i++) {
                socket = new Socket("127.0.0.1", 3000);

                // create a server response reader and client request writer
                this.serverResponse = new DataInputStream(socket.getInputStream());
                this.clientRequests = new PrintWriter(socket.getOutputStream());

                sendGetRequest();

                String temp = "";
                // Vector<String> line = new Vector<>();
                Scanner s = new Scanner(this.socket.getInputStream());
                while (s.hasNextLine()) {
                    temp += s.nextLine() + '\n';
                }

                System.out.println("Server response for Client " + this.clientId + " : " + temp);
                System.out.println(
                        "Client " + this.clientId + " lamport: " + this.lamportTime + "\r\n\r\n");
                terminalinput.close();
                clientRequests.close();
                serverResponse.close();
                Thread.sleep(5000);
            }

        } catch (Exception e) {
            // print error if client inputs
            // incorrect address and/or port no
            // or if any other errors occur
            e.printStackTrace();
        }
    }

    public void updateLamportTime(Vector<String> data) {
        String serverTime = "";
        for (String string : data) {
            if (string.contains("lamport-timestamp")) {
                serverTime = string.split(":")[1].strip();
                this.lamportTime = Math.max(Integer.parseInt(serverTime), this.lamportTime) + 1;
            }
        }
    }

    public void sendGetRequest() {
        try {
            this.clientRequests.println("GET /weather.json HTTP/1.1\nUser-Agent: Client " +
                    this.clientId + "\nLamport-Timestamp: " +
                    String.valueOf(this.lamportTime) + "\nContent-Type: application/json\nContent-Length: 0\n");

            this.clientRequests.flush();

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
