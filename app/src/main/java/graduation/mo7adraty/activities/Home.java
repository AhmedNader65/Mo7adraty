package graduation.mo7adraty.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import graduation.mo7adraty.NotifyService;
import graduation.mo7adraty.R;
import graduation.mo7adraty.Utils;
import graduation.mo7adraty.adapters.ExpandableAdapter;
import graduation.mo7adraty.models.lectures;

public class Home extends AppCompatActivity {

    ExpandableListView lst;
    DatabaseReference myRef,myRef7,myRef2;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    String Uclass;
    ProgressDialog dialog;
    ExpandableAdapter adapter;
    LinkedHashMap<String,ArrayList<lectures>> hashMap;
    int thurscount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        hashMap = new LinkedHashMap<>();
        hashMap.put("Saturday",null);
        hashMap.put("Sunday",null);
        hashMap.put("Monday",null);
        hashMap.put("Tuesday",null);
        hashMap.put("Wednesday",null);
        hashMap.put("Thursday",null);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mAuth = FirebaseAuth.getInstance();

        lst = (ExpandableListView) findViewById(R.id.list);
        adapter = new ExpandableAdapter(this,hashMap);
        lst.setAdapter(adapter);

        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        lst.setEmptyView(emptyView);
        //نسخة من الداتابيز
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage("Loading...");
        dialog.show();
        //ريفرنس لليوزر داتا
        myRef = database.getReference("users").child(mAuth.getCurrentUser().getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            //بيجيب الداتا بتاعت اليوزر هنا
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,String> value = (HashMap<String, String>) dataSnapshot.getValue();
                Uclass =value.get("class");
                try {
                    checkTemp();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                FirebaseMessaging.getInstance().subscribeToTopic(Uclass);
                getDataByDay("Saturday");
                getDataByDay("Sunday");
                getDataByDay("Monday");
                getDataByDay("Tuesday");
                getDataByDay("Wednesday");
                getDataByDay("Thursday");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        startAlarm();
    }


    public void startAlarm(){
        Intent myIntent = new Intent(this , NotifyService.class).putExtra("type","student");
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 50);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);
    }

    private void getDataByDay(final String day){
        myRef7 = database.getReference("Lectures").child(Uclass.substring(0,1)).child(day);
        final ArrayList<lectures>  dayLec = new ArrayList<>();
        myRef7.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("dataaaaa"+dataSnapshot.getValue().toString());
                HashMap<String,Object> value = (HashMap<String,Object> )dataSnapshot.getValue();
                thurscount++;
                if(value.get("name")!=null){
                    value.get("name").toString();
                    lectures l = new lectures();
                    l.setName(value.get("name").toString());
                    l.setInst(value.get("instructor").toString());
                    l.setTime(Integer.parseInt(value.get("start").toString()));
                    l.setPlace(value.get("place").toString());
                    try{
                        l.setTemp((Boolean) value.get("isTemp"));
                    }catch (Exception e){
                        l.setTemp(false);
                    }
                    dayLec.add(l);
                }
                getSections(dayLec,value,Uclass);

                if(thurscount >= dataSnapshot.getChildrenCount()){
                    if(day == "Thursday")
                        dialog.dismiss();
                    hashMap.put(day,dayLec);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout:
                mAuth.signOut();
                startActivity(new Intent(this,LoginActivity.class));
                this.finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                break;
        }
        return true;
    }

    private void getSections(ArrayList<lectures> dayLec, HashMap<String, Object> value, String s) {
        if(value.get(s)!=null){
            thurscount++;
            HashMap<String,Object> valuee = (HashMap<String,Object> )value.get(s);
            for(int i = 0 ; i < valuee.size();i++){
                HashMap<String,Object> value2 = (HashMap<String,Object> )valuee.get(valuee.keySet().toArray()[i]);
                    lectures l = new lectures();
                    l.setName(value2.get("name").toString());
                    l.setUid((String) valuee.keySet().toArray()[i]);
                    l.setInst(value2.get("instructor").toString());
                    l.setTime(Integer.parseInt(value2.get("start").toString()));
                    l.setPlace(value2.get("place").toString());
                try{
                    l.setTemp((Boolean) value.get("isTemp"));
                }catch (Exception e){
                    l.setTemp(false);
                }
                    l.setSection(s);
                    try{
                        l.setTemp((Boolean) value.get("isTemp"));
                    }catch (Exception e){
                        l.setTemp(false);
                    }
                    dayLec.add(l);
            }
        }
    }
    private void checkTemp() throws ParseException {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        String weekdays[] = dfs.getWeekdays();
        final Calendar cal = Calendar.getInstance();
            Log.e("today is saturday",cal.getTime().getDay()+"");

        final String CurrentDay = weekdays[cal.get(Calendar.DAY_OF_WEEK)];

        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        final String currentDayString = df.format(cal.getTime());
        final Date CurrentDate = df.parse(currentDayString);


        myRef = database.getReference("temp");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try{
                    System.out.println("valueee "+dataSnapshot.getValue());
                    HashMap<String,Object> value = (HashMap<String,Object> )dataSnapshot.getValue();
                    String day = (String) value.get("day");
                    String mClass =((String)value.get("section")).split("-")[0];
                    String section =((String) value.get("section")).split("-")[1];
                    String place = (String) value.get("place");
                    int time =Integer.parseInt(value.get("start").toString());
                    String name = value.get("name").toString();
                    String instructor = value.get("instructor").toString();
                    String uid = value.get("uid").toString();
                    String new_day = value.get("new_day").toString();
                    String new_dateString = value.get("new_date").toString();
                    final Date newDate = df.parse(new_dateString);

                    System.out.println("oooooooooold" + section + mClass + new_day + day + uid);
                    int newDayNum = Utils.getNumOfDay(new_day);
                    if (section.equals("General")) {
                        myRef2 = database.getReference("Lectures").child(mClass).child(new_day).child(uid);
                    } else {
                        myRef2 = database.getInstance().getReference().child("Lectures").child(mClass).child(new_day).child("sec").child(value.get("section").toString()).child(uid);
                    }
                    System.out.println("current date >> "+CurrentDate.toString()+ " new Date is >> " +newDate.toString());
                    if(CurrentDate.compareTo(newDate)>0) {
                        myRef2.removeValue();
                        if (section.equals("General")) {

                            myRef2 = database.getInstance().getReference().child("Lectures").child(mClass).child(day).child(uid);
                        } else {
                            myRef2 = database.getInstance().getReference().child("Lectures").child(mClass).child(day).child("sec").child(value.get("section").toString()).child(uid);
                        }

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("place", place);
                        childUpdates.put("start", time);
                        childUpdates.put("instructor", instructor);
                        childUpdates.put("name", name);
                        myRef2.updateChildren(childUpdates);
                        myRef.removeValue();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
