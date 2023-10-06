
import java.nio.file.Files;
import java.nio.file.Path;

public class PUTTest {
    public boolean putRequest(String filePath) {
        // we send the contents of ContentServer1.json
        // and compare it to LatestWeatherData.json
        // to see whether our text was sent successfully from
        // the content server to the aggregation server
        ContentServer cs = new ContentServer();
        cs.start();
        String s1 = "";
        String s2 = "";
        try {
            // wait for the aggregation server to process request
            Thread.sleep(1000);

            // Get the contents of the input we sent from the content server
            Path p1 = Path.of(filePath);
            // get the contents of the data that was received by
            // aggregation server
            Path p2 = Path.of("LatestWeatherData.json");
            s1 = Files.readString(p1);
            s2 = Files.readString(p2);
            // remove all whitespaces and newlines
            s1 = s1.replaceAll("\\s+", "");
            s2 = s2.replaceAll("\\s+", "");
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        // compare if the contents in both files match
        // if they match, this returns true.

        // System.out.println(s1 + '\n' + s2);
        return s1.equals(s2);
    }

    public static void main(String[] args) {
        PUTTest put = new PUTTest();
        System.out.println("\033[0;1mPut request works? " + put.putRequest("ContentServer1.json") + "\033[0m");
    }
}
