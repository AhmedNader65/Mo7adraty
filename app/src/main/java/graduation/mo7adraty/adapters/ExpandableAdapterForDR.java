package graduation.mo7adraty.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import graduation.mo7adraty.R;
import graduation.mo7adraty.Utils;
import graduation.mo7adraty.models.lectures;


public class ExpandableAdapterForDR extends BaseExpandableListAdapter implements View.OnClickListener{
    changeLect changeLect;
    public interface changeLect{
        void onLectChanged();
    }
    private Context ctx;
    DatabaseReference myRef;
    FirebaseDatabase database;
    LinkedHashMap<String,ArrayList<lectures>> hashMap;
    public ExpandableAdapterForDR(Context context, LinkedHashMap<String,ArrayList<lectures>> hashMap,changeLect changeLect){
        this.changeLect = changeLect;
        this.ctx = context;
        this.hashMap = hashMap;
    }

    //عدد ايام الاسبوع
    @Override
    public int getGroupCount() {
        return hashMap.size();
    }

    @Override
    public int getChildrenCount(int i) {

        return 1;
    }

    //بيرجع اليوم اللى احنا عايزينه
    // نديله الاي دي وبيرع اليوم
    @Override
    public Object getGroup(int i) {
        return hashMap.keySet().toArray()[i];
    }

    //بيرجع المحاضرة اللى احنا عايزينها
    // نديله الاي دي بتاع اليوم واي دي المحاضرة
    @Override
    public Object getChild(int parent, int child) {
        return hashMap.get(hashMap.keySet().toArray()[parent]);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int parent, boolean isExpanded, View view, ViewGroup viewGroup) {
        // بجيب اليوم وبخزنه
        String groupTitle = (String) getGroup(parent);
        if(view ==null){
            // بعرض الديزاين بتاع اليوم
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item,viewGroup,false);
        }
        // بحط اسم اليوم في الديزاين
        TextView textView = (TextView) view.findViewById(R.id.item);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(groupTitle);

        return view;
    }
    TableLayout tb;
    @Override
    public View getChildView(int parent, int child, boolean lastChild, View view, ViewGroup viewGroup) {
        // بجيب ليست بالمحاضرات بتاعت اليوم المعين
        ArrayList<lectures> lecturesArrayList = (ArrayList<lectures>) getChild(parent,child);
        notifyDataSetChanged();
        if(view ==null){
            // بعرض الديزاين بتاع المحاضرة
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_layout ,viewGroup,false);
        }
        // بعمل جدول فاضي
        tb = (TableLayout) view.findViewById(R.id.displayLinear);
        tb.removeAllViews();
        // بضيف الصف الاول اللى هو راس الجدول
        TableRow row = (TableRow)LayoutInflater.from(ctx).inflate(R.layout.table_row_dr, null);
        ((TextView)row.findViewById(R.id.lec)).setText("Lecture");
        ((TextView)row.findViewById(R.id.lec)).setBackgroundResource(R.drawable.table_header_shape);
        ((TextView)row.findViewById(R.id.time)).setText("Time");
        ((TextView)row.findViewById(R.id.time)).setBackgroundResource(R.drawable.table_header_shape);
        ((TextView)row.findViewById(R.id.p)).setText("Place");
        ((TextView)row.findViewById(R.id.p)).setBackgroundResource(R.drawable.table_header_shape);
        ((ImageView)row.findViewById(R.id.cancel)).setVisibility(View.INVISIBLE);
        ((ImageView)row.findViewById(R.id.modify)).setVisibility(View.INVISIBLE);
         // بضيفة للجدول
        tb.addView(row);
        // ترتيب حسب الوقت
        Collections.sort(lecturesArrayList, new Comparator<lectures>() {
            @Override
            public int compare(lectures o1, lectures o2) {
                return Integer.valueOf(o1.getTime()).compareTo(Integer.valueOf(o2.getTime()));
            }
        });
        addData(lecturesArrayList,parent);
        return view;
    }

    /** This function add the data to the table **/
    // بضيف المحاضرات في الجدول
    public void addData(ArrayList<lectures> lecturesArrayList,int parent){
        for (int i = 0; i < lecturesArrayList.size(); i++)
        {
            TableRow row = (TableRow)LayoutInflater.from(ctx).inflate(R.layout.table_row_dr, null);
            ((TextView)row.findViewById(R.id.lec)).setText(lecturesArrayList.get(i).getName());
            int time ;
            if(lecturesArrayList.get(i).getTime() == 12){
                time = 12;
            }else {
                time = lecturesArrayList.get(i).getTime() % 12;
            }
            ((TextView)row.findViewById(R.id.time)).setText(String.valueOf(time));
            ((TextView)row.findViewById(R.id.p)).setText(lecturesArrayList.get(i).getPlace());
            ((ImageView)row.findViewById(R.id.modify)).setTag(parent+"and"+i);
            ((ImageView)row.findViewById(R.id.modify)).setOnClickListener(this);
            ((ImageView)row.findViewById(R.id.cancel)).setTag(parent+"and"+i);
            ((ImageView)row.findViewById(R.id.cancel)).setOnClickListener(this);
            if(lecturesArrayList.get(i).isTemp()){
                row.setBackgroundColor(ctx.getResources().getColor(R.color.temp_color));
            }
            tb.addView(row);
        }
    }
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
    int c,p;
    @Override
    public void onClick(View view) {
        // لما يضغط على تعديل او حذف
        p = Integer.parseInt(view.getTag().toString().split("and")[0]);
        c = Integer.parseInt(view.getTag().toString().split("and")[1]);
        lectures lecture =hashMap.get(hashMap.keySet().toArray()[p]).get(c);
        String origDay = (String) hashMap.keySet().toArray()[p];
        System.out.println("parent "+hashMap.keySet().toArray()[p]+ " child "+hashMap.get(hashMap.keySet().toArray()[p]).get(c).getTime());
        if(view.getId() == R.id.cancel){
            // للحذف
            showDialog3(lecture, origDay);
        }else if(view.getId() == R.id.modify) {
            // للتعديل
            showDialog(lecture, origDay);
        }

    }

    public void showDialog(final lectures lectures, final String origDay){
        // اختيار يوم وساعه المحاضرة الجديدة ومكانها
        final Dialog dialog = new Dialog(ctx);
        dialog.setTitle("Change lecture details");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog);
        List<String> daylist = new ArrayList<String>();
        daylist.add("Saturday");
        daylist.add("Sunday");
        daylist.add("Monday");
        daylist.add("Tuesday");
        daylist.add("Wednesday");
        daylist.add("Thursday");
        List<String> timeList = new ArrayList<String>();
        timeList.add("8");
        timeList.add("10");
        timeList.add("12");
        timeList.add("2");
        timeList.add("4");
        final Spinner daysSpinner = (Spinner) dialog.findViewById(R.id.daySpinner);
        final Spinner timeSpinner = (Spinner) dialog.findViewById(R.id.timeSpinner);
        timeSpinner.setPrompt("Choose time");

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(ctx,
                android.R.layout.simple_spinner_item, daylist);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(ctx,
                android.R.layout.simple_spinner_item, timeList);

        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(dayAdapter);
        timeSpinner.setAdapter(timeAdapter);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final List<String> placeslist = new ArrayList<>();
                final String day = String.valueOf(daysSpinner.getSelectedItem());
                final String date = Utils.getDateOfWeekDay(Utils.getNumOfDay(String.valueOf(daysSpinner.getSelectedItem())));
                System.out.println("curr newDate +++ "+ Utils.getNumOfDay(String.valueOf(daysSpinner.getSelectedItem())));
                final String time = String.valueOf(timeSpinner.getSelectedItem());
                if(!time.isEmpty()|| time !=null){
                     if(!day.isEmpty()|| day !=null) {

                         database = FirebaseDatabase.getInstance();
                         myRef = database.getReference("places").child(day+","+time);
                         myRef.addChildEventListener(new ChildEventListener() {
                             @Override
                             public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                 HashMap<String,String> value = (HashMap<String,String> )dataSnapshot.getValue();
                                 placeslist.add(value.get("name"));
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
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 Log.e("resulllt","finished");
                                 showDialog2(day,date,time,placeslist,lectures,origDay);
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });
                     }else{
                         Toast.makeText(ctx, "Please choose a day", Toast.LENGTH_SHORT).show();
                     }
                }else {
                    Toast.makeText(ctx, "Please choose a time", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();

    }
    public void showDialog2(final String day,final String new_date ,final String time, List<String> placesList, final lectures lectures, final String origDay){
        // تعديل المحاضرة واختيار المكان والوقت
        final Dialog dialog = new Dialog(ctx);
        dialog.setTitle("Change lecture details");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_next);

        final Spinner placesSpinner = (Spinner) dialog.findViewById(R.id.placeSpinner);
        placesSpinner.setPrompt("Choose place");

        ArrayAdapter<String> placeAdapter = new ArrayAdapter<>(ctx,
                android.R.layout.simple_spinner_item, placesList);


        placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placesSpinner.setAdapter(placeAdapter);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        final CheckBox forever = (CheckBox) dialog.findViewById(R.id.forever);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isForever = forever.isChecked();
                final String place = String.valueOf(placesSpinner.getSelectedItem());
                     if(!place.isEmpty()|| place !=null) {
                         database = FirebaseDatabase.getInstance();

                         String mClass = lectures.getSection().split("-")[0];
                         String section = lectures.getSection().split("-")[1];
                         if (section.equals("General")) {

                             myRef = database.getInstance().getReference().child("Lectures").child(mClass).child(origDay).child(lectures.getUid());
                         } else {
                             myRef = database.getInstance().getReference().child("Lectures").child(mClass).child(origDay).child("sec").child(lectures.getSection()).child(lectures.getUid());
                         }
                         if(!day.equals(origDay)) {
                             myRef.removeValue();
                             if (section.equals("General")) {

                                 myRef = database.getInstance().getReference().child("Lectures").child(mClass).child(day).child(lectures.getUid());
                             } else {
                                 myRef = database.getInstance().getReference().child("Lectures").child(mClass).child(day).child("sec").child(lectures.getSection()).child(lectures.getUid());
                             }
                         }
                             Map<String, Object> childUpdates2 = new HashMap<>();
                             childUpdates2.put("place", place);
                             childUpdates2.put("start", time);
                             childUpdates2.put("instructor", lectures.getInst());
                             childUpdates2.put("isTemp", !isForever);
                             childUpdates2.put("name", lectures.getName());
                             myRef.updateChildren(childUpdates2);
                         if(!isForever){
                             myRef = database.getInstance().getReference().child("temp").push();
                             Map<String, Object> childUpdates = new HashMap<>();
                             childUpdates.put("name",lectures.getName());
                             childUpdates.put("instructor",lectures.getInst());
                             childUpdates.put("place",lectures.getPlace());
                             childUpdates.put("start",lectures.getTime());
                             childUpdates.put("section",lectures.getSection());
                             childUpdates.put("uid",lectures.getUid());
                             childUpdates.put("new_day",day);
                             childUpdates.put("new_date",new_date);
                             childUpdates.put("day",origDay);
                             myRef.updateChildren(childUpdates);
                         }
                         String msg = origDay+ " "+lectures.getTime()+" > " + day + " " + time;
                         Utils.sendFCMPush(ctx,ctx.getString(R.string.change_lect_title)+" "+lectures.getName(),msg,lectures.getSection());
                         changeLect.onLectChanged();

                     }else{
                         Toast.makeText(ctx, "Please choose a place", Toast.LENGTH_SHORT).show();
                     }

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void showDialog3(final lectures lectures, final String origDay){
        // تأكيد حذف المحاضرة
        new AlertDialog.Builder(ctx)
                .setTitle("Delete lecture")
                .setMessage("Are you sure you want to delete this lecture it can't be undo?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        database = FirebaseDatabase.getInstance();

                        String mClass = lectures.getSection().split("-")[0];
                        String section = lectures.getSection().split("-")[1];
                        if (section.equals("General")) {

                            myRef = database.getInstance().getReference().child("Lectures").child(mClass).child(origDay).child(lectures.getUid());
                        } else {
                            myRef = database.getInstance().getReference().child("Lectures").child(mClass).child(origDay).child("sec").child(lectures.getSection()).child(lectures.getUid());
                        }
                            myRef.removeValue();
                            myRef = database.getInstance().getReference().child("temp").push();
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("name",lectures.getName());
                            childUpdates.put("instructor",lectures.getInst());
                            childUpdates.put("place",lectures.getPlace());
                            childUpdates.put("start",lectures.getTime());
                            childUpdates.put("section",lectures.getSection());
                            childUpdates.put("uid",lectures.getUid());
                            childUpdates.put("new_day","Friday");
                            childUpdates.put("new_date",Utils.getDateOfWeekDay(Utils.getNumOfDay("Friday")));
                            childUpdates.put("day",origDay);
                            myRef.updateChildren(childUpdates);

                        String msg = origDay+ " "+lectures.getTime();
                        Utils.sendFCMPush(ctx,ctx.getString(R.string.delete_lect_title)+" "+lectures.getName(),msg,lectures.getSection());
                        changeLect.onLectChanged();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}


