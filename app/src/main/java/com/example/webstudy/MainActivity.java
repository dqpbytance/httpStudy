package com.example.webstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.webstudy.Util.WebUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        WebView wb=findViewById(R.id.wv1);
//        wb.getSettings().setJavaScriptEnabled(true);
//        wb.setWebViewClient(new WebViewClient());
//        wb.loadUrl("https://www.baidu.com");

        content = findViewById(R.id.web_content_tv);
        Button btn_send = findViewById(R.id.send_web_request);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //httpurlrequestMethod();
                //sendRequestWithOkHttp();
//                WebUtil.httpUrlConnection("http://10.95.46.192:8080/testjson/test.json", new WebUtil.HttpCallbackListener() {
//                    @Override
//                    public void onFinish(String response) {
//                        parseWithGSON(response);
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//
//                    }
//                });

                WebUtil.OkhttpConnection("http://10.95.46.192:8080/testjson/test.json", new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String result=response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                content.setText(result);
                            }
                        });
                        parseWithGSON(result);
                    }
                });
            }
        });


    }

    private void httpurlrequestMethod() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                BufferedReader br = null;
                try {
                    URL url = new URL("https://www.baidu.com");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setReadTimeout(8000);
                    br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            content.setText(sb.toString());
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
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
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody=new FormBody.Builder()
                        .add("username","sss")
                        .add("passwd","123456")
                        .build();
                Request request = new Request.Builder()
                        .url("http://10.95.46.192:8080/testjson/test.json")
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    //parseXMLWITHPull(result);
                    parseJson(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            content.setText(result);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseXMLWITHPull(String respnoseData) {
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlpullparser = factory.newPullParser();
            xmlpullparser.setInput(new StringReader(respnoseData));

            int eventType = xmlpullparser.getEventType();
            String id = "", name = "", version = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String nodename = xmlpullparser.getName();
                switch (eventType) {
                    //开始解析某个节点
                    case XmlPullParser.START_TAG: {
                        if ("id".equals(nodename)) {
                            id = xmlpullparser.nextText();
                        } else if ("name".equals(nodename)) {
                            name = xmlpullparser.nextText();
                        } else if ("version".equals(nodename)) {
                            version = xmlpullparser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        if ("app".equals(nodename)) {
                            Log.d("MainActivity", "id is:" + id);
                            Log.d("MainActivity", "name is:" + name);
                            Log.d("MainActivity", "version is:" + version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlpullparser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseJson(String respnoseData) {
        try {
            JSONArray objects = new JSONArray(respnoseData);
            for (int i = 0; i < objects.length(); i++) {
                JSONObject object = objects.getJSONObject(i);
                Log.d("MainActivity", "id is:" + object.getString("id"));
                Log.d("MainActivity", "name is:" + object.getString("name"));
                Log.d("MainActivity", "version is:" + object.getString("version"));
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    private void parseWithGSON(String respnoseData){
        Gson gson=new Gson();
        List<Person> persionlist=gson.fromJson(respnoseData,new TypeToken<List<Person>>(){}.getType());
        for (Person person:persionlist){
            Log.d("MainActivity", "id is:" + person.getId());
            Log.d("MainActivity", "name is:" + person.getName());
            Log.d("MainActivity", "version is:" + person.getVersion());
        }
    }
}