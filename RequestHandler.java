
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

import org.json.*;

public class RequestHandler extends Thread {
    private Socket socket;
    private ProducerConsumer pc;

    public RequestHandler(Socket soc, ProducerConsumer pc) {
        this.socket = soc;
        this.pc = pc;
    }

    public boolean requestIsOk(String data) {
        boolean requestType = false, userAgent = false, lamport_timestamp = false, content_type = false,
                content_len = false;

        if (data.contains("PUT /weather.json HTTP/1.1") || data.contains("GET /weather.json HTTP/1.1")) {
            requestType = true;
        }
        if (data.contains("User-Agent:")) {
            userAgent = true;
        }
        if (data.contains("Lamport-Timestamp:")) {
            lamport_timestamp = true;
        }
        if (data.contains("Accept:")) {
            content_type = true;
        }
        if (data.contains("Content-Length:")) {
            content_len = true;
        }
        return requestType && userAgent && lamport_timestamp && content_len && content_type;
    }

    private Vector<String> stringToVector(String data) {
        String[] temp = data.split("\n");
        Vector<String> ans = new Vector<>();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != "\n") {
                ans.add(temp[i]);
            }
        }

        return ans;
    }

    @Override
    public void run() {
        try {
            PrintWriter pw = new PrintWriter(this.socket.getOutputStream(), isAlive());
            Scanner s = new Scanner(this.socket.getInputStream()).useDelimiter("\n");
            String line = "";

            while (!requestIsOk(line)) {
                String temp = s.hasNextLine() ? s.nextLine().strip() + '\n' : "";
                line += temp;

                if (temp.isBlank()) {
                    break;
                }
            }

            if (line.contains("PUT")) {
                for (int i = 0; i < 19; i++) {
                    line += s.nextLine().strip();
                }
                if (!line.contains("}")) {
                    line += "}";
                }
            }
            Vector<String> converted = stringToVector(line);

            System.out.println(line);

            String id = this.pc.getValue(converted, "User-Agent");
            this.pc.addRequest(converted);

            String res = this.pc.getRequest(id);
            pw.println(res);
            pw.flush();
            pw.close();

            this.socket.close();

        } catch (

        Exception e) {
            e.printStackTrace();
        }
    }

}
