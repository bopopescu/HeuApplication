package com.example.guill.myapplication;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
}
