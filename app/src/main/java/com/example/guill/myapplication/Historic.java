package com.example.guill.myapplication;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jeremydebelleix on 22/11/2017.
 */

public class Historic {

    public void writeJson(String recordName, String filename, String text, int time) {

        FileOutputStream outputStream;

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

        String oldContent = getJsonString(filename);

        try {
            outputStream = new FileOutputStream(file);
            JSONObject main = new JSONObject();

            if (oldContent != null) {
                main = new JSONObject(oldContent);
            }

            JSONObject json = new JSONObject();

            json.put("text", text);
            json.put("time", time);

            main.put(recordName, json);

            String jsonContent = main.toString();

            outputStream.write(jsonContent.getBytes());

            outputStream.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getJsonString(String filename) {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

        String content = null;

        try {
            FileInputStream is = new FileInputStream(file);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            content = new String(buffer, "UTF-8");

            Log.d("json constent: ", "" + content);

            return content;
        }
        catch (IOException e) {

        }
        return content;

    }

    public ArrayList<String> getRecords(String filename) {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

        FileOutputStream outputStream;

        String content = getJsonString(filename);

        ArrayList<String> list = new ArrayList<String>();

        try {
            JSONObject main = new JSONObject(content);

            Iterator<?> keys = main.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();

                list.add(key);

                Log.d("key", ""+ key);
                if ( main.get(key) instanceof JSONObject ) {
                    Log.d("key", ""+ main.get(key));
                }
            }

            return list;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return list;

    }

    public String getValue(String filename, String recordName, String key) {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

        FileOutputStream outputStream;

        String content = getJsonString(filename);

        String value = null;

        try {
            JSONObject main = new JSONObject(content);
            JSONObject object = new JSONObject();

            object = main.getJSONObject(recordName);

            value = object.getString(key);

            return value;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return value;
    }
}
