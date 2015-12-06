package net.kimdongup.daehoonp;

/**
 * Created by kimdongup on 2015-09-05.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataActivity extends Activity {
    TextView txtView;
    ListView listView;
    phpDown task;
    String jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dataactivity);
        task = new phpDown();
        txtView = (TextView) findViewById(R.id.txtView1);
        listView = (ListView) findViewById(R.id.listView1);
        task.execute("http://kimdongup.net/appdata.php");

    }


    private class phpDown extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
                jsonResult = jsonHtml.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String str) {
            ListDrawer();
        }

    }

    public void ListDrawer() {
        List<Map<String, String>> commentList = new ArrayList<Map<String, String>>();
        try {    JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("results");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String comment_post_id = jsonChildNode.optString("comment_post_id");
                String comment_author = jsonChildNode.optString("comment_author");
                String comment_content = jsonChildNode.optString("comment_content");
                String outPut = comment_post_id + "-" + comment_author + "-" + comment_content;
                commentList.add(createComment("comments", outPut));
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, commentList,
                android.R.layout.simple_list_item_1, new String[] { "comments" }, new int[] { android.R.id.text1 });
        listView.setAdapter(simpleAdapter);
    }

    private HashMap<String, String> createComment(String name, String number) {
        HashMap<String, String> CommentNameNo = new HashMap<String, String>();
        CommentNameNo.put(name, number);
        return CommentNameNo;
    }
}

