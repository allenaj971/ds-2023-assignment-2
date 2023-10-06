
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Vector;

import org.json.*;

public class GETTest {

    private String getData(Vector<String> request) {
        for (String string : request) {
            if (string.contains("{")) {
                return string.strip();
            }
        }
        return null;
    }

    public boolean getRequest() {
        // to test the client's ability to
        // perform get request, I initialise 4 threads
        // if the data received by the client matches
        // the LatestWeatherData.json file
        // then get requests work
        Client cs = new Client();
        cs.start();

        try {
            // get the contents of the data in the aggregation server
            Path p2 = Path.of("LatestWeatherData.json");
            cs.join();
            JSONObject s1 = new JSONObject(getData(cs.getResponse()).replaceAll("\\s+", ""));
            JSONObject s2 = new JSONObject(Files.readString(p2).replaceAll("\\s+", ""));
            // remove all whitespaces and newlines
            // compare if the contents in both strings match
            // if they match, this returns true.
            return s1.toString().equals(s2.toString());
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }

    }

    public static void main(String[] args) {
        GETTest t = new GETTest();
        // here we initialise 4 clients on 4 threads to
        // perform GET requests
        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                System.out.println("\033[0;1mGET request works for Client " + Thread.currentThread().getId() + "? "
                        + t.getRequest() + "\033[0m");
            }).start();
        }
    }
}
