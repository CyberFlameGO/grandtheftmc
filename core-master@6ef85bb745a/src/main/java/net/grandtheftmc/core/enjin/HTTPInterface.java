package net.grandtheftmc.core.enjin;

import org.json.JSONObject;
import sun.net.www.http.HttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by Adam on 10/06/2017.
 */
public class HTTPInterface {

    /**
     * Send a HTTP POST request to the specified url, with the body content.
     *
     * @param u    URL to POST to.
     * @param body Body of the POST request.
     */
    public static JSONObject post(String u, String body) {
        try {
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", Integer.toString(body.length()));
            conn.getOutputStream().write(body.getBytes("UTF8"));
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            int responseCode = conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //System.out.println("Response code = " + responseCode);
            //System.out.println("Response = " + response.toString());

            if(response.toString().length() > 0) {
                JSONObject json = new JSONObject(response.toString());
                return json;
            } else {
                return new JSONObject();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
