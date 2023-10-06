import java.io.File;

public class Test201req {
    public static void main(String[] args)
    {   
        // This file will test the 201 response 
        // we first delete the LatestWeatherData.json
        // file. Then we send a put request, and the aggregation server 
        // should send us a 201 response
        File weather = new File("LatestWeatherData.json");

        weather.delete();

        ContentServer cs = new ContentServer();
        cs.start();
        try {
            cs.join();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        if(cs.getResponse().contains("201"))
        {
            System.out.println("\033[0;1m201 response code works? true \033[0m");
        }
        else
        {
            System.out.println("\033[0;1m201 response code works? false \033[0m");
        }
    }
}
