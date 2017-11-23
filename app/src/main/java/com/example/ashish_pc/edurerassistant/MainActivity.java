package com.example.ashish_pc.edurerassistant;

import android.app.Activity;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

    Button btn;
    TextView tv,tv1;
    NodeList nodelist;
    ListView lv;
    String msgdata= " ";
    EditText e;
    ArrayAdapter<ChatMessage> adapter;
    private ArrayList<ChatMessage> chatHistory= new ArrayList<ChatMessage>();

    //String url1 = "http://google.com/complete/search?output=toolbar&q=microsoft";
    //String url1 = "http://www.androidbegin.com/tutorial/XMLParseTutorial.xml";
   //String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=Stack%20Overflow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView)findViewById(R.id.listView2);
        e = (EditText)findViewById(R.id.editText3);
    }

    public void datafetch(View v)
    {
        String que = e.getText().toString();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(que);
        chatMessage.setSuggestion("qqq");
        chatMessage.setTitle(" ");
        chatMessage.setSuggestion1("qqq");
        chatMessage.setSuggestion2("qqq");
        chatMessage.setMe(true);
        e.setText("");
        chatHistory.add(chatMessage);
        adapter = new Mylistadapter();
        lv.setAdapter(adapter);
        String str="";
        str = que.replace(" ","%20");
        String url = "https://en.wikipedia.org/w/api.php?action=query&list=search&prop=images&format=json&srsearch="+str+"&srnamespace=0&srprop=snippet&srlimit=10&imlimit=1";
        new JsonTask().execute(url);

    }



    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }


                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String oneObjectsItem="";
            String suggestionstr= "";

                try {
                    JSONObject jObject = new JSONObject(result);
                    JSONObject jObject1 = jObject.getJSONObject("query");
                    JSONArray jArray1 = jObject1.getJSONArray("search");
                    if(jArray1.length()==0)
                    {
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setMessage("Oho!I didn't find any relevant data.Please Correct your query.");
                            chatMessage.setTitle(" ");
                            chatMessage.setSuggestion("qqq");
                            chatMessage.setSuggestion1("qqq");
                            chatMessage.setSuggestion2("qqq");
                            chatMessage.setMe(false);
                            chatHistory.add(chatMessage);
                            adapter = new Mylistadapter();
                            lv.setAdapter(adapter);

                    }
                    else {
                        JSONObject de = jArray1.getJSONObject(0);
                        String togetmsg = de.getString("title");

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setMessage(" ");
                        chatMessage.setTitle(togetmsg);
                        JSONObject suggest = jArray1.getJSONObject(1);
                        suggestionstr = suggest.getString("title");
                        chatMessage.setSuggestion(suggestionstr);
                        suggest = jArray1.getJSONObject(2);
                        suggestionstr = suggest.getString("title");
                        chatMessage.setSuggestion1(suggestionstr);
                        suggest = jArray1.getJSONObject(3);
                        suggestionstr = suggest.getString("title");
                        chatMessage.setSuggestion2(suggestionstr);
                        chatMessage.setMe(false);
                        new JsonTaskmsg().execute(chatMessage);
                    }

                } catch (Exception e) {

                }


        }
    }





    /* code for jsontaskmsg for msg retrievel */



    private class JsonTaskmsg extends AsyncTask<ChatMessage, ChatMessage, ChatMessage> {

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected ChatMessage doInBackground(ChatMessage... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            String a = params[0].getTitle();
            String c = a.replace(" ", "%20");
            String ur = "https://en.wikipedia.org/w/api.php?action=opensearch&search="+c+"&limit=1&format=json";
            try {
                URL url = new URL(ur);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }



                String result = buffer.toString();

                try {

                    JSONArray jsonArray = new JSONArray(result);
                    JSONArray jarray = jsonArray.getJSONArray(2);

                    String de = jarray.getString(0);
                    params[0].setMessage(de);
                    return params[0];


                }
                catch(Exception e)
                {

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ChatMessage result) {
            super.onPostExecute(result);

            try {

                chatHistory.add(result);
                adapter = new Mylistadapter();
                lv.setAdapter(adapter);

            }
            catch(Exception e)
            {

            }

        }
    }






/* list adapter code */




    private class Mylistadapter extends ArrayAdapter<ChatMessage> {
        public Mylistadapter() {
            super(MainActivity.this, R.layout.listlayout, chatHistory);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemview = convertView;
            if (itemview == null) {
                itemview = getLayoutInflater().inflate(R.layout.listlayout, parent, false);

            }
            ChatMessage currentquestion = chatHistory.get(position);
            LinearLayout ln = (LinearLayout) itemview.findViewById(R.id.contentWithBackground);
            LinearLayout ll = (LinearLayout) itemview.findViewById(R.id.suggest);
            if (currentquestion.getIsme() == false) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ln.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                ln.setLayoutParams(layoutParams);
                ll.setVisibility(itemview.VISIBLE);

            }
            else
            {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ln.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                ln.setLayoutParams(layoutParams);
            }

            TextView tv = (TextView) itemview.findViewById(R.id.txtMessage);
            tv.setText("" + currentquestion.getMessage());
            TextView tv2,tv3,tv4;
            tv2 = (TextView) itemview.findViewById(R.id.txtInfo);
            tv3 = (TextView) itemview.findViewById(R.id.txtInfo1);
            tv4 = (TextView) itemview.findViewById(R.id.txtInfo2);
            if (currentquestion.getSuggestion() != "qqq") {

                tv2.setText("" + currentquestion.getSuggestion());
                tv2.setVisibility(itemview.VISIBLE);
            }
            else
               tv2.setVisibility(itemview.GONE);
            if (currentquestion.getSuggestion1() != "qqq") {
                tv3.setText("" + currentquestion.getSuggestion1());
                tv3.setVisibility(itemview.VISIBLE);
            }
            else
                tv3.setVisibility(itemview.GONE);

            if (currentquestion.getSuggestion2() != "qqq") {
                tv4.setText("" + currentquestion.getSuggestion2());
                tv4.setVisibility(itemview.VISIBLE);
            }
            else
                tv4.setVisibility(itemview.GONE);

            return itemview;
        }


    }








}
