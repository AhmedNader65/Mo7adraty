package graduation.mo7adraty.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

import graduation.mo7adraty.R;
import graduation.mo7adraty.models.lectures;


public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Context ctx;
    LinkedHashMap<String,ArrayList<lectures>> hashMap;

    public ExpandableAdapter(Context context,LinkedHashMap<String,ArrayList<lectures>> hashMap){
        this.ctx = context;
        this.hashMap = hashMap;
    }

    @Override
    public int getGroupCount() {
        return hashMap.size();
    }

    @Override
    public int getChildrenCount(int i) {

        return 1;
    }
    @Override
    public Object getGroup(int i) {
        return hashMap.keySet().toArray()[i];
    }

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
        String groupTitle = (String) getGroup(parent);
        if(view ==null){
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item,viewGroup,false);
        }
        TextView textView = (TextView) view.findViewById(R.id.item);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(groupTitle);
        return view;
    }
    TableLayout tb;
    @Override
    public View getChildView(int parent, int child, boolean lastChild, View view, ViewGroup viewGroup) {
        ArrayList<lectures> lecturesArrayList = (ArrayList<lectures>) getChild(parent,child);
        notifyDataSetChanged();
        if(view ==null){
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_layout ,viewGroup,false);
        }
        tb = (TableLayout) view.findViewById(R.id.displayLinear);
        tb.removeAllViews();
        TableRow row = (TableRow)LayoutInflater.from(ctx).inflate(R.layout.table_row, null);
        ((TextView)row.findViewById(R.id.lec)).setText("Lecture");
        (row.findViewById(R.id.lec)).setBackgroundResource(R.drawable.table_header_shape);
        ((TextView)row.findViewById(R.id.time)).setText("Time");
        (row.findViewById(R.id.time)).setBackgroundResource(R.drawable.table_header_shape);
        ((TextView)row.findViewById(R.id.p)).setText("Place");
        (row.findViewById(R.id.p)).setBackgroundResource(R.drawable.table_header_shape);
        ((TextView)row.findViewById(R.id.dr)).setText("Instructor");
        (row.findViewById(R.id.dr)).setBackgroundResource(R.drawable.table_header_shape);
        tb.addView(row);
        Collections.sort(lecturesArrayList, new Comparator<lectures>() {
            @Override
            public int compare(lectures o1, lectures o2) {
                return Integer.valueOf(o1.getTime()).compareTo(Integer.valueOf(o2.getTime()));
            }
        });
        addData(lecturesArrayList);
        return view;
    }

    /** This function add the data to the table **/
    public void addData(ArrayList<lectures> lecturesArrayList){
        for (int i = 0; i < lecturesArrayList.size(); i++)
        {
            TableRow row = (TableRow)LayoutInflater.from(ctx).inflate(R.layout.table_row, null);
            lectures lecture = lecturesArrayList.get(i);
            ((TextView)row.findViewById(R.id.lec)).setText(lecture.getName());
            ((TextView)row.findViewById(R.id.time)).setText(String.valueOf(lecture.getTime()));
            ((TextView)row.findViewById(R.id.p)).setText(lecture.getPlace());
            ((TextView)row.findViewById(R.id.dr)).setText(lecture.getInst());
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
}
