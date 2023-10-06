import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Test400req {
    public static void main(String[] args) {
        // this test will show that the 400 invalid request type
        // works. it will attempt to send a POST request
        // and the aggregation server should send back a 400 invalid request type
        try {
            Socket soc = new Socket("127.0.0.1", 3000);
            PrintWriter pw = new PrintWriter(soc.getOutputStream());
            Scanner scan = new Scanner(soc.getInputStream());

            String req = "POST /weather.json HTTP/1.1\nUser-Agent: Client1\nLamport-Timestamp:1\nContent-Type: application/json\nContent-Length: 0\n";
            pw.println(req);
            pw.flush();

            String res = "";
            while (scan.hasNextLine()) {
                res += scan.nextLine() + '\n';
            }
            System.out.println(res);
            if (res.contains("400")) {
                System.out.println("\033[0;1m400 response code works? true \033[0m");
            } else {
                System.out.println("\033[0;1m400 response code works? false \033[0m");
            }
            soc.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
