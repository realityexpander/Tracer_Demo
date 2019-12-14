package com.example.instanatracerdemo;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.awaitility.Duration;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class HttpInstrumentationEndToEndTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule(MainActivity.class);

    private InstanaBackendStub backendStub;
    private OkHttpClient client;

    @Before
    public void setUp() throws IOException {
        backendStub = new InstanaBackendStub(8080);
        backendStub.start();
        client = new OkHttpClient();
    }

    @After
    public void tearDown() {
        backendStub.stop();
    }

    /***
     * This is the test that should be fixed. Right now this test is failing as the application is
     * not instrumented. The goal is to write instrumentation that intercepts every HTTP request
     * made from the application with the OkHttp library and reports this to the Instana backend.
     *
     * An example of the request that you should make to the Instana backend is made in the
     * `backendStubTest` below.
     *
     */
    @Test
    @Ignore("FIX ME!")
    public void mustReportAllHttpCallsToInstanaBackend() {
        MainActivity activity = mActivityRule.getActivity();
        activity.visitWebsite(activity.findViewById(R.id.go));

        // This test will fail at the line below
        await().atMost(Duration.TEN_SECONDS)
               .until(new Callable<Boolean>() {
                   @Override
                   public Boolean call() throws Exception {
                       return reportedCalls().size() > 0;
                   }
        });

        final List<Map<String, String>> calls = reportedCalls();

        assertThat(calls, Matchers.hasSize(1));

        Map<String, String> call = calls.get(0);

        assertThat(call, hasKey("timestamp"));
        assertThat(call, hasEntry("method", "GET"));
        assertThat(call, hasEntry("host", "www.google.com"));
        assertThat(call, hasEntry("path", "/search?q=instana"));
    }

    /***
     * This test serves as an example as to how intercepted http calls should be posted to the
     * Instana backend. It is also a sanity check that the stub backend is working as expected.
     *
     * @throws IOException
     */
    @Test
    public void backendStubTest() throws IOException {
        // On startup we expect the backend to not have received any calls
        assertThat(reportedCalls(), is(empty()));

        // Send a sample call to the backend
        String exampleCall =
                "{" +
                        "\"timestamp\":1575402206813," +
                        "\"method\":\"GET\"," +
                        "\"host\":\"docs.instana.io\"," +
                        "\"path\":\"/products/website_monitoring\"" +
                "}";
        Request request = new Request.Builder()
                .url("http://localhost:8080/api")
                .method(
                        "POST",
                        RequestBody.create(
                                exampleCall,
                                MediaType.parse("application/json")))
                .build();
        Response response = client.newCall(request).execute();

        // The backend returns NO_CONTENT on successfully receiving a call
        assertThat(response.code(), is(204));

        // Now we again fetch the the calls the backend has received and we expect a single call
        List<Map<String, String>> calls = reportedCalls();

        assertThat(calls, hasSize(1));
        assertThat(calls.get(0), hasEntry("timestamp", "1575402206813"));
        assertThat(calls.get(0), hasEntry("method", "GET"));
        assertThat(calls.get(0), hasEntry("host", "docs.instana.io"));
        assertThat(calls.get(0), hasEntry("path", "/products/website_monitoring"));
    }

    /**
     * Retrieves a list of all the calls that have been reported to the backend. The stub backend
     * provides an endpoint just for testing purposes.
     *
     * @return The list of reported calls
     */
    private List<Map<String, String>> reportedCalls() {
        List<Map<String, String>> calls = new ArrayList<>();
        Request request = new Request.Builder ().url("http://localhost:8080/calls" ).build();
        try (Response response = client.newCall(request).execute()) {
            assertThat(response.isSuccessful(), is(true));
            for (String callJson: response.body().string().split("[\\r\\n]+")) {
                calls.add(deserializeCall(callJson));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return calls;
    }

    /**
     * Deserializes a json string as a Map<String, String>
     * @param json The json string to deserialize
     *
     * @return The deserialized result
     */
    private Map<String, String> deserializeCall(String json) {
        Gson gson = new Gson();
        Type stringMap = new TypeToken<Map<String, String>>() {}.getType();

        return gson.fromJson(json, stringMap);
    }
}
