package com.nstuttle.illdapt.chickencoop;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView txtCoopTemp;
    TextView txtHeater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtCoopTemp = (TextView)findViewById(R.id.txtCoopTemp);
        TempAsyncTask tempAsyncTask = new TempAsyncTask(txtCoopTemp, "http://sample-env-2.bezhdxzkip.us-west-2.elasticbeanstalk.com/coopInfo/");
        tempAsyncTask.execute();
    }
    @Override
    public void onResume(){
        super.onResume();
        TempAsyncTask tempAsyncTask = new TempAsyncTask(txtCoopTemp, "http://sample-env-2.bezhdxzkip.us-west-2.elasticbeanstalk.com/coopInfo/");
        tempAsyncTask.execute();
    }
}

class TempAsyncTask extends AsyncTask<Void, Void, String> {
    //Variables
    private View mView;
    private String mUrl;
    private String tempString;
    private TextView Temp;
    String heat;

    //Construct
    TempAsyncTask(View view, String url) {
        mView = view;
        mUrl = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Temp = (TextView) mView.findViewById(R.id.txtCoopTemp);
    }
    @Override
    protected String doInBackground(Void... params) {
        String resultString;
        resultString = getJSON(mUrl);
        try {
            JSONObject mainObject = new JSONObject(resultString); //Make JSON Object
            tempString = mainObject.getString("temp"); //Set Variable targeting Object Param "temp"
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempString;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        Temp.setText(string + (char) 0x00B0 + "F"); //Set TextView to read Temp (passed in) + deg + F
    }

    //Methods
    private String getJSON(String url) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.connect();
            int status = c.getResponseCode();
            switch (status) {
                case 200:   //Cases for Success HTTP
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    return sb.toString();
            }
        } catch (Exception ex) {
            return ex.toString();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    //Error
                }
            }
        }
        return null;
    }
}