package graduation.mo7adraty;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class Utils {

    public static int getNumOfDay(String day){
        switch (day){
            case "Saturday":
                return 7;
            case "Sunday":
                return 1;
            case "Monday":
                return 2;
            case "Tuesday":
                return 3;
            case "Wednesday":
                return 4;
            case "Thursday":
                return 5;
            case "Friday":
                return 6;
        }
        return 0;
    }
    public static String getDateOfWeekDay(int weekDay)
    {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        if(weekDay!=Calendar.SATURDAY) {
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                cal.set(Calendar.DAY_OF_WEEK, weekDay);
                cal.add(Calendar.DATE, 7);
            } else {
                cal.set(Calendar.DAY_OF_WEEK, 0);
                System.out.println("curr day "+ df.format(cal.getTime()));
                cal.set(Calendar.DAY_OF_WEEK, weekDay);
                System.out.println("curr day "+ weekDay);
            }
        }else {
            cal.set(Calendar.DAY_OF_WEEK, weekDay);
        }
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date date = cal.getTime();
        System.out.println("curr new Date date3 "+df.format(date));

        return df.format(date);
    }
    public static void sendFCMPush(Context context, String title, final String msg,String topic) {

        final String Legacy_SERVER_KEY = "AIzaSyC2P1I4zvoLkjmFk22Xk43zGLiEKeeyrLA";
        String token = "/topics/"+topic;
        JSONObject obj = null;
        JSONObject objData = null;
        try {
            obj = new JSONObject();
            objData = new JSONObject();
            objData.put("body", msg);
            objData.put("title", title);
            obj.put("to", token);
            obj.put("data", objData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("!_@@_SUCESS", response + "");
                        Log.e("!_@@_SUCESS", msg + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("!_@@_Errors--", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}
