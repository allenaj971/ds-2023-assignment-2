// A Java program for a Server
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.*;
import org.json.*;
 
public class AggregationServer
{
    //initialize socket and input stream
    private Socket socket = null;
    private ServerSocket serverSock = null;
    private PriorityQueue<String> requestQueue = new PriorityQueue<>(20);
    private Integer lamportTime;

    // constructor with port
    public AggregationServer(int port)
    {
        // starts server and waits for a connection
        try
        {
            serverSock = new ServerSocket(port);
            System.out.println("Server started on port: " + port);
            while(true) multipleConnections();
        }
        catch(Exception i)
        {
            System.err.println(i);
        }
    }

    private void multipleConnections() throws Exception
    {
        socket = serverSock.accept();
        System.out.println("Client accepted");

        if(socket.isConnected())
        new Thread(()->{
            try {
                NewConnection newconnect = new NewConnection(socket);
                
                // send a response to client/content server that 
                // connection is working
                newconnect.serverResponse.writeUTF(requestJSONgenerator(1));

                String line = "";

                while(!line.equals("over"))
                {
                    line = newconnect.requests.readUTF();
                    System.out.println(line);

                    String data = requestHandler(line);
                    
                    newconnect.serverResponse.writeUTF(data);
                    if(data.contains("DISCONNECTED"))
                    {
                        newconnect.closeConnection();
                        break;
                    }
                }

            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }).start();
    }
    
    // takes input from queue and processes requests 
    public String requestHandler(String request){
        // parse JSON String
        JSONObject req = new JSONObject(request);
        // check here for req type 
        if(req.getString("request-type").equals("GET /weather.json HTTP/1.1"))
        {
            return get();
        }
        else if(req.getString("request-type").equals("PUT /weather.json HTTP/1.1"))
        {
            return put(req.toString());
        }
        else if(req.getString("request-type").equals("over"))
        {
            return requestJSONgenerator(-1);
        }
        // return invalid request type
        else if(req.getString("request-type").equals(""))
        {
            return requestJSONgenerator(400);
        }
        else
        {
            return requestJSONgenerator(500);
        }
    }

    public String get()
    {
        try (FileReader fr = new FileReader("WeatherData.json"))
        {
            JSONTokener jsTokener = new JSONTokener(fr);
            JSONObject weather = new JSONObject(jsTokener);
            
            weather.put("response-code", "200");
            weather.put("response-type", "HTTP_SUCCESS");
            return weather.toString();
        } catch (Exception e) {
            return requestJSONgenerator(404);
        }
    }

    public String put(String data)
    {
        JSONObject jsonReq = new JSONObject(data);
        
        try {
            File fr = new File("WeatherData.json");
            // check if response format is correct
            if(jsonReq.getString("data").length() != 0)
            {
                // no file exists, so create new one, write new data and return 201
                if(fr.createNewFile())
                {
                    FileWriter fw = new FileWriter("WeatherData.json");
                    fw.write(jsonReq.getString("data"));
                    fw.close();
                    return requestJSONgenerator(201);
                }
                // else write new data to existing file
                else
                {
                    FileWriter fw = new FileWriter("WeatherData.json");
                    fw.write(jsonReq.getString("data"));
                    fw.close();
                    return requestJSONgenerator(200);
                }
            }
            else if (jsonReq.getString("data").length() == 0)
            {
                return requestJSONgenerator(204);
            }
            else
            {
                return requestJSONgenerator(400);
            }

        } catch (Exception e) {
            return e.toString();
        }
    }

    public String requestJSONgenerator(int code)
    {
        JSONObject jsonRes = new JSONObject();
        switch (code) {
            case 400:
                jsonRes.put("response-type", "HTTP_BAD_REQUEST");
                jsonRes.put("response-code", "400");
                break;
            case 204:
                jsonRes.put("response-type", "HTTP_NO_CONTENT");
                jsonRes.put("response-code", "204");
                break;
            case 201:
                jsonRes.put("response-type", "HTTP_CREATED");
                jsonRes.put("response-code", "201");
                break;
            case 500:
                jsonRes.put("response-type", "HTTP_SERVER_FAILURE");
                jsonRes.put("response-code", "500");
                break;
            case 200:
                jsonRes.put("response", "200");
                jsonRes.put("response-type", "HTTP_SUCCESS");
                break;
            case 404:
                jsonRes.put("response", "404");
                jsonRes.put("response-type", "HTTP_FILE_NOT_FOUND");
                break;
            case -1:
                jsonRes.put("response", "200");
                jsonRes.put("response-type", "HTTP_DISCONNECTED");
                break;
            case 1:
                jsonRes.put("response", "200");
                jsonRes.put("response-type", "HTTP_CONNECTED");
                break;
            default:
                break;
        }
        return jsonRes.toString();
    }

    public String mapToString(Map<String, String> data)
    {
        JSONObject req = new JSONObject(data);
        return req.toString();
    }

    public static void main(String args[])
    {  
        // initialise 2 threads on server start
        // 1 for I/O 

        // and then 1 for server. as new connections are initiated
        // more threads are created, but only 1 thread remains for the 
        // I/O
        AggregationServer server = new AggregationServer(3000); 
    }
}