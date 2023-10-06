
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Vector;

import org.json.*;

public class InvalidJSON {

    public boolean invalidPUTrequest() {
        // we send the contents of InvalidJSON.json
        // to see whether the aggregation server
        // ensures that invalid json is not saved
        // the InvalidJSON.json file is missing a {
        // in the file.
        ContentServer cs = new ContentServer();
        cs.start();

        try {
            // wait for the aggregation server to process request
            Thread.sleep(1000);
            // cs.join();
            String res = cs.getResponse();
            // if the response contains
            // the 500 response code, then it works correctly
            if(res.contains("500"))
            {
                return true;
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        return false;
    }
    public static void main(String[] args) {
        InvalidJSON t = new InvalidJSON();

        System.out.println("\033[0;1mInvalid JSON is blocked by Aggregation Server " + Thread.currentThread().getId() + "? "
                + t.invalidPUTrequest() + "\033[0m");

    }

}
