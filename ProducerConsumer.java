import java.util.Comparator;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;
import org.json.*;

// 
public class ProducerConsumer extends Thread {
    private PriorityQueue<JSONObject> requestQueue;
    private HashMap<String, JSONObject> responses; 
    private ReentrantLock lock = new ReentrantLock(); 

    public void run()
    {
        this.requestQueue = new PriorityQueue<>(new LamportComparator()); 
        this.responses = new HashMap<>();
    }

    public String addRequest(String req)
    {
        lock.lock();
        JSONObject request = new JSONObject(req);
        this.requestQueue.add(request);

        System.out.println("Producer-Consumer Requests:\n" + requestQueue);
        lock.unlock();

        return request.getString("client-id");
    }
    
    public JSONObject getRequest(String id)
    {
        while(this.responses.get(id) == null){performRequest();}
        return this.responses.get(id);
    }

    private void performRequest()
    {
        lock.lock();
        while(!requestQueue.isEmpty()){
            // poll request
            JSONObject req = requestQueue.poll();

            if(req.getString("lamport-timestamp").equals(""))
            {
                this.responses.put(req.getString("client-id"), requestJSONgenerator(req,400));
            }
            else
            {
                // if request is GET, read file and perform GET request
                if(req.getString("request-type").equals("GET /weather.json HTTP/1.1"))
                {
                    this.responses.put(req.getString("client-id"), get(req));
                }
                // if request is PUT, write to WeatherData.json file and perform PUT request
                else if(req.getString("request-type").equals("PUT /weather.json HTTP/1.1"))
                {
                    // if data is empty then respond with code 500 for invalid request
                    if(req.getString("data").equals(""))
                    {
                        this.responses.put(req.getString("client-id"), requestJSONgenerator(req,500));
                    }
                    // else perform PUT request
                    else
                    {
                        this.responses.put(req.getString("client-id"), put(req));
                    }
                }
                // if the connection is being terminated by content server or client
                else if(req.getString("request-type").equals("over"))
                {
                    // send back response 200 disconnected 
                    this.responses.put(req.getString("client-id"), requestJSONgenerator(req,-1));
                }
                // send back a 400 error for invalid request type. 
                else                     
                {
                    this.responses.put(req.getString("client-id"), requestJSONgenerator(req,400));
                }
            }
        }
        System.out.println("Producer-Consumer Responses:\n" + this.responses);
        lock.unlock();
    }
    
    private JSONObject get(JSONObject req)
    {
        try (FileReader fr = new FileReader("WeatherData.json"))
        {
            JSONTokener jsTokener = new JSONTokener(fr);
            JSONObject weather = new JSONObject(jsTokener);
    
            weather.put("response-code" , "200");
            weather.put("response-type", "HTTP_SUCCESS");
            weather.put("client-id", req.getString("client-id"));
            weather.put("lamport-timestamp", readLamportTime(req.getString("lamport-timestamp")));
            
            return weather;
        } catch (Exception e) {
            return requestJSONgenerator(req,404);
        }
    }

    private JSONObject put(JSONObject jsonReq)
    {
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
                    return requestJSONgenerator(jsonReq, 201);
                }
                // else write new data to existing file
                else
                {
                    FileWriter fw = new FileWriter("WeatherData.json");
                    fw.write(jsonReq.getString("data"));
                    fw.close();
                    return requestJSONgenerator(jsonReq, 200);
                }
            }
            else if (jsonReq.getString("data").length() == 0)
            {
                return requestJSONgenerator(jsonReq,204);
            }
            else
            {
                return requestJSONgenerator(jsonReq, 400);
            }
        } catch (Exception e) {
            return new JSONObject(e.toString());
        }
    }

    public JSONObject requestJSONgenerator(JSONObject req, int code)
    {
        JSONObject jsonRes = new JSONObject();
        try {
            jsonRes.put("lamport-timestamp", readLamportTime(req.getString("lamport-timestamp")));
            jsonRes.put("client-id", req.getString("client-id"));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
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
                jsonRes.put("response-code", "200");
                jsonRes.put("response-type", "HTTP_SUCCESS");
                break;
            case 404:
                jsonRes.put("response-code", "404");
                jsonRes.put("response-type", "HTTP_NOT_FOUND");
                break;
            case -1:
                jsonRes.put("response-code", "200");
                jsonRes.put("response-type", "HTTP_DISCONNECTED");
                break;
            case 1:
                jsonRes.put("response-code", "200");
                jsonRes.put("response-type", "HTTP_CONNECTED");
                break;
            default:
                break;
        }
        return jsonRes;
    }
    
    private String readLamportTime(String currentTime)
    {
        try (FileReader fr = new FileReader("AggregationServerLamport.json"))
        {
            JSONTokener jsTokener = new JSONTokener(fr);
            JSONObject data = new JSONObject(jsTokener);
            Integer serverTime = Integer.parseInt(data.getString("AggregationServerLamport"));
            Integer newTime = Math.max(serverTime, Integer.parseInt(currentTime)) + 1;
            
            data.put("AggregationServerLamport", String.valueOf(newTime));
            FileWriter fw = new FileWriter("AggregationServerLamport.json");
            fw.write(data.toString());
            fw.close();
            return String.valueOf(newTime);
        } catch (Exception e) {
            return e.toString();
        }
    }   

}

class LamportComparator implements Comparator<JSONObject>
{
    @Override
    public int compare(JSONObject a, JSONObject b)
    {
        Integer aVal = Integer.parseInt(a.getString("lamport-timestamp"));
        Integer bVal = Integer.parseInt(b.getString("lamport-timestamp"));

        if(aVal < bVal)
        {
            return -1;
        }
        else if(aVal > bVal)
        {
            return 1;
        }
        return 0;
    }
}