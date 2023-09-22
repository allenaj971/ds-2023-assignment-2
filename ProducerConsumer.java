import java.util.Comparator;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;
import org.json.*;

// 
public class ProducerConsumer extends Thread {
    private PriorityQueue<Vector<String>> requestQueue;
    private HashMap<String, String> responses;
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        this.requestQueue = new PriorityQueue<>(new LamportComparator());
        this.responses = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public void addRequest(Vector<String> req) {
        lock.lock();

        this.requestQueue.add(req);
        if(requestQueue.size() >= 2)
        {
            System.out.println("Producer-Consumer Requests:\n" + requestQueue);
        }

        lock.unlock();
    }

    public String getValue(Vector<String> request, String val) {
        for (String string : request) {
            if (string.contains(val)) {
                String[] ans = string.split(":");
                return ans[1];
            }
        }
        return null;
    }

    public String getData(Vector<String> request) {
        for (String string : request) {
            if (string.contains("{")) {
                return string.strip();
            }
        }
        return null;
    }

    public String getRequest(String id) {
        while (this.responses.get(id) == null) {
            performRequest();
        }
        String ans = this.responses.get(id);
        this.responses.put(id, null);
        return ans;
    }

    private void performRequest() {
        lock.lock();
        if (!requestQueue.isEmpty()) {
            // poll request
            Vector<String> req = this.requestQueue.poll();
            if (getValue(req, "User-Agent") == null || getValue(req, "Lamport-Timestamp") == null) {
                this.responses.put(getValue(req, "User-Agent"), requestJSONgenerator(400,
                        "{'message':'No User-Agent or Lamport-Timestamp provided!'}", "Lamport-Timestamp: -1"));
            } else {
                // if request is GET, read file and perform GET request
                if (req.contains("GET /weather.json HTTP/1.1")) {
                    this.responses.put(getValue(req, "User-Agent"), get(readLamportTime(req)));
                }
                // if request is PUT, write to WeatherData.json file and perform PUT request
                else if (req.contains("PUT /weather.json HTTP/1.1")) {
                    // if data is empty then respond with code 400 for no data in request body
                    if (getData(req) == null) {
                        this.responses.put(getValue(req, "User-Agent"),
                                requestJSONgenerator(400, "{'message': 'No data field in request body!'}",
                                        readLamportTime(req)));
                    } else {
                        this.responses.put(getValue(req, "User-Agent"), put(req));
                    }
                }
                // send back a 400 error for invalid request
                else {
                    this.responses.put(getValue(req, "User-Agent"),
                            requestJSONgenerator(400, "{'message': 'Invalid request type'}", readLamportTime(req)));
                }
            }
        }
        // System.out.println("Producer-Consumer Responses:\n" + this.responses);
        lock.unlock();
    }

    private String requestJSONgenerator(int code, String message, String newLamportTime) {
        String response = "";

        switch (code) {
            case 400:
                response += "HTTP/1.1 400 Bad Request\n";
                break;
            case 204:
                response += "HTTP/1.1 204 No Content\n";
                break;
            case 201:
                response += "HTTP/1.1 201 Created File\n";
                break;
            case 500:
                response += "HTTP/1.1 500 Server Failure\n";
                break;
            case 200:
                response += "HTTP/1.1 200 OK\n";
                break;
            case 404:
                response += "HTTP/1.1 404 Not Found\n";
                break;
            default:
                break;
        }
        response += newLamportTime + "\nContent-Length: " + String.valueOf(message.length()) + "\nContent-Type: application/json\r\n\r\n" + message;
        // System.out.println(response);
        return response;
    }

    private String get(String newLamportTime) {
        try (FileReader fr = new FileReader("WeatherData.json")) {
            JSONTokener jsTokener = new JSONTokener(fr);
            JSONObject weather = new JSONObject(jsTokener);

            return requestJSONgenerator(200, weather.toString(), newLamportTime);
        } catch (Exception e) {
            return requestJSONgenerator(404, "{'message': 'WeatherData.json not found!'}", newLamportTime);
        }
    }

    private String put(Vector<String> data) {
        String newLamportTime = readLamportTime(data);
        try {
            File fr = new File("WeatherData.json");
            // check if response format is correct
            if (getData(data).length() != 0) {
                Boolean fileExists = fr.createNewFile();
                FileWriter fw = new FileWriter("WeatherData.json");

                fw.write(getData(data));
                fw.close();

                if (fileExists == false) {
                    return requestJSONgenerator(200, "{'message': 'Successfully updated weather file!'}",
                            newLamportTime);
                } else {
                    return requestJSONgenerator(201, "{'message': 'Successfully created WeatherData.json!'}",
                            newLamportTime);
                }
            } else {
                return requestJSONgenerator(204, "{'message': 'No content to update WeatherData.json'}",
                        newLamportTime);
            }
        } catch (Exception e) {
            return requestJSONgenerator(500,"{'message': 'WeatherData.json not found!'}" , newLamportTime);
        }
    }

    private String readLamportTime(Vector<String> data) {
        Integer newTime = Integer.parseInt(getValue(data, "Lamport-Timestamp").strip());
        try (FileReader fr = new FileReader("AggregationServerLamport.json")) {
            JSONTokener jsTokener = new JSONTokener(fr);
            JSONObject serverTime = new JSONObject(jsTokener);
            newTime = Math.max(Integer.parseInt(serverTime.getString("AggregationServerLamport")), newTime) + 1;

            serverTime.put("AggregationServerLamport", String.valueOf(newTime));
            FileWriter fw = new FileWriter("AggregationServerLamport.json");
            fw.write(serverTime.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Lamport-Timestamp: " + String.valueOf(newTime);
    }

}

class LamportComparator implements Comparator<Vector<String>> {

    private String getValue(Vector<String> request, String val) {
        for (String string : request) {
            if (string.contains(val)) {
                String[] ans = string.split(":");
                return ans[1];
            }
        }
        return null;
    }

    @Override
    public int compare(Vector<String> a, Vector<String> b) {
        Integer aVal = Integer.parseInt(getValue(a, "Lamport-Timestamp").strip());
        Integer bVal = Integer.parseInt(getValue(b, "Lamport-Timestamp").strip());
        if (aVal < bVal) {
            return -1;
        } else if (aVal > bVal) {
            return 1;
        }
        return 0;
    }
}