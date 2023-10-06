import java.nio.file.Files;
import java.nio.file.Path;

public class EmptyJSON {
    public boolean putRequest(String filePath) {
        // we send the contents of EmptyJSON.json
        // and compare it to LatestWeatherData.json
        // to see whether our text was sent successfully from
        // the content server to the aggregation server
        ContentServer cs = new ContentServer();
        cs.start();

        try {
            // wait for the aggregation server to process request
            Thread.sleep(1000);
            String res = cs.getResponse();
            // if the response contains
            // the 204 response code, then it works correctly
            if(res.contains("204"))
            {
                return true;
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        return false;
    }

    public static void main(String[] args) {
        PUTTest put = new PUTTest();
        System.out.println("\033[0;1mEmpty data in put request works? " + put.putRequest("EmptyJSON.json") + "\033[0m");
    }
}