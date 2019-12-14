package com.example.instanatracerdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.realityexpander.okhttpinstana.OkHttpInstana;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpInstana client = new OkHttpInstana();
        client.test();

        // create a new volley request queue with an OkHttp stack
        requestQueue = Volley.newRequestQueue(this, new OkHttpStack());
    }

    public void visitWebsite(View view) {
        final TextView textView = (TextView) findViewById(R.id.text);
        // clear the output from the previous request (if any)
        textView.setText("...");

        // create the request
        String url = "https://www.google.com/search?q=instana";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        textView.setText(response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work! " + error.toString());
            }
        });

        // enqueue the request
        requestQueue.add(stringRequest);
    }
}
