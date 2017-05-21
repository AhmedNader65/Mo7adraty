package graduation.mo7adraty.activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import graduation.mo7adraty.NotifyService;
import graduation.mo7adraty.R;
import graduation.mo7adraty.Utils;
import graduation.mo7adraty.adapters.ExpandableAdapterForDR;
import graduation.mo7adraty.models.lectures;

public class HomeDr extends AppCompatActivity implements ExpandableAdapterForDR.changeLect {
    ExpandableListView lst;
    DatabaseReference myRef, myRef2;
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    ProgressDialog dialog;
    ExpandableAdapterForDR adapter;
    LinkedHashMap<String, ArrayList<lectures>> hashMap;
    String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dr);
        startAlarm();
        hashMap = new LinkedHashMap<>();
        hashMap.put("Saturday", null);
        hashMap.put("Sunday", null);
        hashMap.put("Monday", null);
        hashMap.put("Tuesday", null);
        hashMap.put("Wednesday", null);
        hashMap.put("Thursday", null);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        lst = (ExpandableListView) findViewById(R.id.list);
        adapter = new ExpandableAdapterForDR(HomeDr.this, hashMap, this);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        lst.setEmptyView(emptyView);
        lst.setAdapter(adapter);
        dialog = new ProgressDialog(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog);
        dialog.setIndeterminate(true);
        dialog.setMessage("Loading...");
        dialog.show();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference("users").child(mAuth.getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> value = (HashMap<String, String>) dataSnapshot.getValue();
                name = value.get("name");
                fetchData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void startAlarm() {
        Intent myIntent = new Intent(this, NotifyService.class).putExtra("type", "dr");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void checkTemp() throws ParseException {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        String weekdays[] = dfs.getWeekdays();
        final Calendar cal = Calendar.getInstance();

        Log.e("today is saturday", cal.getTime().getDay() + "");
        final String CurrentDay = weekdays[cal.get(Calendar.DAY_OF_WEEK)];

        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        final String currentDayString = df.format(cal.getTime());
        final Date CurrentDate = df.parse(currentDayString);


        myRef2 = database.getReference("temp");
        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    System.out.println("valueee " + dataSnapshot.getValue());
                    HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                    String day = (String) value.get("day");
                    String mClass = ((String) value.get("section")).split("-")[0];
                    String section = ((String) value.get("section")).split("-")[1];
                    String place = (String) value.get("place");
                    int time = Integer.parseInt(value.get("start").toString());
                    String name = value.get("name").toString();
                    String instructor = value.get("instructor").toString();
                    String uid = value.get("uid").toString();
                    String new_day = value.get("new_day").toString();
                    String new_dateString = value.get("new_date").toString();
                    final Date newDate = df.parse(new_dateString);

                    System.out.println("oooooooooold" + section + mClass + new_day + day + uid);
                    int newDayNum = Utils.getNumOfDay(new_day);

                    if (!new_day.equals("Friday")) {
                        if (section.equals("General")) {
                            myRef = database.getReference("Lectures").child(mClass).child(new_day).child(uid);
                        } else {
                            myRef = database.getInstance().getReference().child("Lectures").child(mClass).child(new_day).child("sec").child(value.get("section").toString()).child(uid);
                        }
                    }
                    System.out.println("current date >> " + CurrentDate.toString() + " new Date is >> " + newDate.toString());
                    if (CurrentDate.compareTo(newDate) > 0) {
                        if (!new_day.equals("Friday")) {
                            myRef.removeValue();
                        }
                        if (section.equals("General")) {

                            myRef = database.getInstance().getReference().child("Lectures").child(mClass).child(day).child(uid);
                        } else {
                            myRef = database.getInstance().getReference().child("Lectures").child(mClass).child(day).child("sec").child(value.get("section").toString()).child(uid);
                        }

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("place", place);
                        childUpdates.put("start", time);
                        childUpdates.put("instructor", instructor);
                        childUpdates.put("name", name);
                        myRef.updateChildren(childUpdates);
                        myRef2.removeValue();
                    }
                } catch (Exception e) {
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

    boolean flage = false;

    private void getDataByDay(final String myClass, final String day) {
        Log.e("HOOOME ", day);
        myRef = database.getReference("Lectures").child(myClass).child(day);

        final ArrayList<lectures> dayLec = new ArrayList<>();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                flage = true;
                Log.e("HomeDR ", day);
                Log.e("hioooo", dataSnapshot.getKey());
                Log.e("HomeDR ", value.toString());
                if (value.get("name") != null) {
                    if (value.get("instructor").toString().equals(name)) {
                        Log.v("HomeDR " + day, value.toString());
                        lectures l = new lectures();
                        l.setUid(dataSnapshot.getKey());
                        l.setName(value.get("name").toString());
                        l.setInst(value.get("instructor").toString());
                        l.setTime(Integer.parseInt(value.get("start").toString()));
                        l.setPlace(value.get("place").toString());
                        l.setSection(myClass + "-General");
                        try {
                            l.setTemp((Boolean) value.get("isTemp"));
                        } catch (Exception e) {
                            l.setTemp(false);
                        }
                        dayLec.add(l);
                    }
                }
                getSections(dayLec, value, myClass + "-A");
                getSections(dayLec, value, myClass + "-B");
                getSections(dayLec, value, myClass + "-C");
                getSections(dayLec, value, myClass + "-D");
                getSections(dayLec, value, myClass + "-E");
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
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (day == "Thursday")
                    dialog.dismiss();
                if (hashMap.get(day) == null) {
                    hashMap.put(day, dayLec);
                } else {
                    ArrayList<lectures> list = hashMap.get(day);
                    for (int i = 0; i < dayLec.size(); i++) {
                        list.add(dayLec.get(i));
                    }
                    hashMap.put(day, list);
                }
                adapter.notifyDataSetChanged();
                System.out.println("HomeDR   We're done loading the initial " + dataSnapshot.getChildrenCount() + " items");
                System.out.println("HomeDR   We're done putting in  " + day + " items " + dayLec.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getSections(ArrayList<lectures> dayLec, HashMap<String, Object> value, String s) {
        if (value.get(s) != null) {
            HashMap<String, Object> valuee = (HashMap<String, Object>) value.get(s);
            for (int i = 0; i < valuee.size(); i++) {
                HashMap<String, Object> value2 = (HashMap<String, Object>) valuee.get(valuee.keySet().toArray()[i]);
                if (value2.get("instructor").toString().equals(name)) {
                    Log.v("HomeDR ", value2.toString());
                    lectures l = new lectures();
                    l.setName(value2.get("name").toString());
                    l.setUid((String) valuee.keySet().toArray()[i]);
                    l.setInst(value2.get("instructor").toString());
                    l.setTime(Integer.parseInt(value2.get("start").toString()));
                    l.setPlace(value2.get("place").toString());
                    l.setSection(s);
                    try {
                        l.setTemp((Boolean) value.get("isTemp"));
                    } catch (Exception e) {
                        l.setTemp(false);
                    }
                    dayLec.add(l);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                this.finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_send:
                showSendDialog();
                break;
        }
        return true;
    }
    public void showSendDialog(){
        final Dialog dialog =new Dialog(this);
        dialog.setTitle("Select class");
        dialog.setContentView(R.layout.send_dialog);
        dialog.show();
        final Spinner classSpinner = (Spinner) dialog.findViewById(R.id.classSpinner);
        final Button next = (Button) dialog.findViewById(R.id.btn_dialog);

        final List<String> classList = new ArrayList<String>();
        classList.add("1");
        classList.add("2");
        classList.add("3");
        classList.add("4");
        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(HomeDr.this,
                android.R.layout.simple_spinner_item, classList);

        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(classAdapter);
        classSpinner.setPrompt("choose");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(HomeDr.this,R.style.MyDialogTheme);
                final EditText input = new EditText(HomeDr.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                builder.setInverseBackgroundForced(true);
                builder.setTitle("Enter Message");
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String msg = input.getText().toString();
                        if(msg!=null&&msg.length()>0) {
                            Utils.sendFCMPush(HomeDr.this, "You have new message", msg, classList.get(classSpinner.getSelectedItemPosition()));
                            dialog.dismiss();
                            Toast.makeText(HomeDr.this, "Sending...", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(HomeDr.this, "You didn't enter a message!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    private void fetchData() {
        try {
            checkTemp();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        getDataByDay("1", "Saturday");
        getDataByDay("1", "Sunday");
        getDataByDay("1", "Monday");
        getDataByDay("1", "Tuesday");
        getDataByDay("1", "Wednesday");
        getDataByDay("1", "Thursday");
        getDataByDay("2", "Saturday");
        getDataByDay("2", "Sunday");
        getDataByDay("2", "Monday");
        getDataByDay("2", "Tuesday");
        getDataByDay("2", "Wednesday");
        getDataByDay("2", "Thursday");
        getDataByDay("3", "Saturday");
        getDataByDay("3", "Sunday");
        getDataByDay("3", "Monday");
        getDataByDay("3", "Tuesday");
        getDataByDay("3", "Wednesday");
        getDataByDay("3", "Thursday");
        getDataByDay("4", "Saturday");
        getDataByDay("4", "Sunday");
        getDataByDay("4", "Monday");
        getDataByDay("4", "Tuesday");
        getDataByDay("4", "Wednesday");
        getDataByDay("4", "Thursday");
    }

    @Override
    public void onLectChanged() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
