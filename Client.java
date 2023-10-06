
// A Java program for a Client
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;
import org.json.*;

public class Client extends Thread {
    private Socket socket;
    private DataInputStream serverResponse;
    private PrintWriter clientRequests;
    private String clientId;
    private Integer lamportTime;
    private Vector<String> response;

    public Client() {
        this.lamportTime = (int) Math.floor(Math.random() * (10 - 0 + 1) + 0);
        this.clientId = String.valueOf(Thread.currentThread().getId());
    }

    public Vector<String> getResponse() {

        return this.response;
    }

    @Override
    public void run() {
        // get terminal input for server address and port
        BufferedReader terminalinput = new BufferedReader(new InputStreamReader(System.in));
        boolean sendRequest = true;
        System.out.println("Hello Client " + clientId + "! Please enter address and port in the format address:port");
        String address = null;
        Integer port = null;
        try {
            // get the address and port from the terminal input
            String temp = terminalinput.readLine();
            if (temp != null) {
                String[] connectionDetails = temp.split(":");
                address = connectionDetails[0];
                port = Integer.parseInt(connectionDetails[1]);
            }
        } catch (Exception e) {
            System.err.println("Error with reading terminal input: " + e.toString());
        }

        while (sendRequest) {
            try {
                if (address == null || port == null) {
                    socket = new Socket("127.0.0.1", 3000);
                } else {
                    socket = new Socket(address, port);
                }

                // create a server response reader and client request writer
                this.serverResponse = new DataInputStream(socket.getInputStream());
                this.clientRequests = new PrintWriter(socket.getOutputStream());

                System.out.println("Optional: Please enter the station id (leave empty if you want latest data):");
                String stationId = terminalinput.readLine();
                sendGetRequest(stationId);

                Vector<String> line = new Vector<>();
                Scanner s = new Scanner(this.socket.getInputStream());
                while (s.hasNextLine()) {
                    line.add(s.nextLine());
                }

                updateLamportTime(line);
                this.response = line;

                System.out.println("Server response for Client " + this.clientId + " : " + line + "\r\n\r\n");

                clientRequests.close();
                serverResponse.close();

                System.out.println(
                        "Request successful. Would you like to send another GET request? ('true' for yes, 'false' for no)");

                sendRequest = Boolean.parseBoolean(terminalinput.readLine());

            } catch (Exception e) {
                // catch & print out any when attempting to connect
                System.err.println(
                        "Client " + this.clientId + " - Failed to connect to aggregation server: "
                                + e.toString()
                                + "\nWould you like to try connecting again? ('true' for yes, 'false' for no)");
                try {
                    sendRequest = Boolean.parseBoolean(terminalinput.readLine());
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        }

        // try {
        // terminalinput.close();
        // } catch (Exception e) {
        // System.err.println("Error closing terminal input: " + e.toString());
        // }
        System.out.println("Goodbye Client " + clientId + "!");
    }

    private void updateLamportTime(Vector<String> data) {
        String serverTime = "";
        for (String string : data) {
            if (string.contains("Lamport-Timestamp")) {
                serverTime = string.split(":")[1].strip();
                this.lamportTime = Math.max(Integer.parseInt(serverTime), this.lamportTime) + 1;
            }
        }
    }

    private void sendGetRequest(String stationId) {
        try {
            if (stationId == null || stationId.length() == 0) {
                this.clientRequests.println("GET /weather.json HTTP/1.1\nUser-Agent: Client " +
                        this.clientId + "\nLamport-Timestamp: " +
                        String.valueOf(this.lamportTime)
                        + "\nContent-Type: application/json\nContent-Length: 0\n");

            } else {
                this.clientRequests.println("GET /weather.json HTTP/1.1\nUser-Agent: Client " +
                        this.clientId + "\nLamport-Timestamp: " +
                        String.valueOf(this.lamportTime) + "\nStation-ID:" + stationId
                        + "\nContent-Type: application/json\nContent-Length: 0\n");
            }

            this.clientRequests.flush();

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public static void main(String[] args) {
        Client c = new Client();
        c.start();
    }
}
