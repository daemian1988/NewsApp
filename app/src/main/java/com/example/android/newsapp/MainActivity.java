package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final String urlAPI = "http://content.guardianapis.com/search?q=debates&show-fields=starRating,headline,thumbnail,short-url&order-by=relevance&api-key=test";
    ArrayList<NewsDetails> newsStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myClickHandler();

    }

    public void myClickHandler() {
        // Gets the URL from the UI's text field.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(urlAPI);
        } else {

        }
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            URL url = null;
            // params comes from the execute() call: params[0] is the url.
            try {
                url = new URL(urlAPI);
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    String passToJson = getStringFromInputStream(in);

                    return passToJson;

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    assert urlConnection != null;
                    urlConnection.disconnect();
                }
                return null;
                //  return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }


        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObject = new JSONObject(result);

                newsStorage = new ArrayList<NewsDetails>();
                JSONObject responseObject = jsonObject.getJSONObject("response");
                JSONArray results = responseObject.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject newResult = results.getJSONObject(i);
                    JSONObject fields = newResult.getJSONObject("fields");

                    newsStorage.add(new NewsDetails(fields.getString("headline"), fields.getString("thumbnail"), fields.getString("shortUrl")));

                }
                NewsAdapter adapter = new NewsAdapter(MainActivity.this, newsStorage);

                ListView listView = (ListView) findViewById(R.id.activity_main);

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(newsStorage.get(position).getHyperLink()));
                        startActivity(i);

                    }
                });


            } catch (JSONException ex) {
                ex.printStackTrace();
            }

        }
    }


}
