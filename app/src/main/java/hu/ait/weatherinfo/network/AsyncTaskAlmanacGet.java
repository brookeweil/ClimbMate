package hu.ait.weatherinfo.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;

public class AsyncTaskAlmanacGet extends AsyncTask<String, Void, String> {

    private AlmanacResultListener resultListener;

    public AsyncTaskAlmanacGet(AlmanacResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int ch;
                while ((ch = is.read()) != -1) {
                    bos.write((byte)ch);
                }

                String delims = "[,/]+";
                String[] tokens = (url.toString()).split(delims);

                result = new String(bos.toByteArray());
                result = tokens[6] + "~" + (tokens[7].substring(0, tokens[7].length()-5)) + "~" + result;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }


        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        resultListener.almanacResultArrived(result);
    }

}
