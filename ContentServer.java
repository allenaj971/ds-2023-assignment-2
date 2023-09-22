
// A Java program for a Client
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import org.json.*;

public class ContentServer extends Thread {
    private Socket socket;
    private DataInputStream serverResponse;
    private PrintWriter contentServerRequests;
    private String contentServerId;
    private Integer lamportTime;

    public ContentServer() {
        this.contentServerId = String.valueOf(Thread.currentThread().getName());
        this.lamportTime = (int) Math.floor(Math.random() * (10 - 0 + 1) + 0);
        ;
    }

    @Override
    public void run(){
        BufferedReader terminalinput = new BufferedReader(new InputStreamReader(System.in));
        try {
            // // read the command line to find the server name and port number (in URL
            // // format)
            // System.out.println("Client: Please enter server address and port no with
            // format servername:portnumber");
            // // Split the address and port number at the colon
            // String[] serverAdd = terminalinput.readLine().split(":");
            // // close the terminal input reader
            // terminalinput.close();

            // String address = serverAdd[0];
            // Integer port = Integer.parseInt(serverAdd[1]);

            // // establish a connection to the aggregation server
            // if (address != null && port != null) {
            // socket = new Socket(address, port);
            // } else {
            for (int i = 0; i < 3; i++) {
                socket = new Socket("127.0.0.1", 3000);
                // }

                serverResponse = new DataInputStream(socket.getInputStream());
                contentServerRequests = new PrintWriter(socket.getOutputStream());

                System.out.print("Please input file location of weather data: ");
                String fileLoc = terminalinput.readLine();

                sendPutRequest(fileLoc);
                Vector<String> line = new Vector<>();
                Scanner s = new Scanner(this.socket.getInputStream());
                while (s.hasNextLine()) {
                    line.add(s.nextLine());
                }

                updateLamportTime(line);
                System.out.println("Server response for ContentServer " + this.contentServerId + " : " + line);
                System.out.println(
                        "ContentServer " + this.contentServerId + " lamport: " + this.lamportTime + "\r\n\r\n");

                // terminalinput.close();
                contentServerRequests.close();
                serverResponse.close();
                Thread.sleep(15000);
            }
        } catch (Exception e) {
            // catch & print out any errors
            System.err.println(e);
        }
    }

    public void updateLamportTime(Vector<String> data) {
        String serverTime = "";
        for (String string : data) {
            if (string.contains("Lamport-Timestamp")) {
                serverTime = string.split(":")[1].strip();
                this.lamportTime = Math.max(Integer.parseInt(serverTime), this.lamportTime) + 1;
            }
        }
    }

    public void sendPutRequest(String fileLoc) {
        // PUT /weather.json HTTP/1.1
        // User-Agent: ATOMClient/1/0
        // Content-Type: (You should work this one out)
        // Content-Length: (And this one too)

        // {
        // "id" : "IDS60901",
        // ...
        // (data)
        // ...
        // "wind_spd_kt": 8
        // }
        // create hashmap

        // read weather data
        try {
            String data = "";
            // place request type into map
            String req = new String(Files.readAllBytes(Paths.get(fileLoc)));
            // System.out.println(req);
            data += "PUT /weather.json HTTP/1.1\n";
            data += "User-Agent: ContentServer " + contentServerId + '\n';
            data += "Lamport-Timestamp: " + String.valueOf(this.lamportTime) + '\n';
            data += "Content-Type: application/json\n";
            data += "Content-Length: " + req.length() + '\n';
            // System.out.println(req);
            data += "\r\n\r\n" + req.toString();
            // send request to server and print response
            // System.out.println(data);
            contentServerRequests.println(data);
            contentServerRequests.flush();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
