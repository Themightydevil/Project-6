package com.example.project6;


import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Helper methods related to requesting and receiving news from Guardian.
 */

public class QueryUtils {

    private static final String TAG = QueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Returns a list of {@link News} object that have been built from parsing the JSON response.
     */
    private static List<News> extractNews(String newsJSON) {

        // If the JSON String is empty then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty List where we can add the news.
        List<News> newsList = new ArrayList<News>();

        // Try to parse the newJSON. If there's any problem with the formatting of the JSON Response
        // then a JSON Exception will be thrown.
        // Catch the expression so that the app doesn't crashes.
        try {
            JSONObject jsonObject = new JSONObject(newsJSON);

            // Getting JSON Array node.

            JSONObject list = jsonObject.optJSONObject("response");
            JSONArray results = list.getJSONArray("results");


            // Looping through the whole list to extract whole information
            for (int i = 0; i < results.length(); i++) {
                JSONObject response = results.getJSONObject(i);
                String nameAuthor = "\'Not available\'";

                JSONArray tagArray = response.getJSONArray("tags");

                if (tagArray != null && tagArray.length() > 0) {

                    JSONObject author = tagArray.getJSONObject(0);
                    nameAuthor = author.getString("webTitle");

                }

                // Accept the data in it's specified format.
                String headline = response.getString("webTitle");
                String section = response.getString("sectionName");
                String date = response.getString("webPublicationDate");
                String url = response.getString("webUrl");

                // Create a new {@link News} object with the headline, section, date,
                // and url from the JSON response.

                newsList.add(new News(headline, section, date, url, nameAuthor));

            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(TAG, "Problem parsing News JSON result", e);
        }

        // Return a list of News
        return newsList;
    }

    /**
     * @param stringUrl takes in the API URL.
     * @return new URL object from the given string object
     */
    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error while creating URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     *
     * @param url
     * @return jsonResponse
     * @throws IOException if unable to retrieve the results
     */

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the url is null then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000/*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request is successful i.e. Response Code = 200
            // then read the input and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retreiving the results from the API", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} which contains the whole JSON Response
     *
     * @param inputStream
     * @return the output if it is not null
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    public static List<News> fetchNewsData(String requestUrl) {

        // Create URL object here.
        URL url = createURL(requestUrl);

        // Perform HTTP Request to server and receive the JSON Response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Error closing Input Stream", e);
        }
        return extractNews(jsonResponse);
    }
}
