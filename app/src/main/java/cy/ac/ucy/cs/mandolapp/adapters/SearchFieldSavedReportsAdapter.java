package cy.ac.ucy.cs.mandolapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import cy.ac.ucy.cs.mandolapp.R;
import cy.ac.ucy.cs.mandolapp.ReportsHistoryActivities.ReportHistory;
import cy.ac.ucy.cs.mandolapp.SavedReportsActivities.SavedReports;


/**
 * Created by andreas on 20/03/2018.
 */

public class SearchFieldSavedReportsAdapter extends BaseAdapter {

    private ArrayList<String> mListItems;
    private LayoutInflater mInflater;
    private Button mSelectedItems;
    private static int selectedCount = 0;
    private static String firstSelected = "";
    private ViewHolder holder;
    private static String selected = "";
    private Context context;//shortened selected values representation


    public static String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        SearchFieldSavedReportsAdapter.selected = selected;
    }

    public SearchFieldSavedReportsAdapter(Context context, ArrayList<String> items,
                                          Button btn) {
        this.context = context;
        mListItems = new ArrayList<String>();
        mListItems.addAll(items);
        mInflater = LayoutInflater.from(context);
        mSelectedItems = btn;
        selectedCount=0;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drop_down_list_row_search, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.SelectOption);
            holder.chkbox = (RadioButton) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(mListItems.get(position));

        final int position1 = position;

        //whenever the checkbox is clicked the selected values textview is updated with new selected values
        holder.chkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setText(position1);
            }
        });

        if(SavedReports.checkSelectedField[position])
            holder.chkbox.setChecked(true);
        else
            holder.chkbox.setChecked(false);
        return convertView;
    }


    /*
     * Function which updates the selected values display and information(checkSelectedField[])
     * */
    private void setText(int position1){
        if (!SavedReports.checkSelectedField[position1]) {

            SavedReports.checkSelectedField[position1] = true;
            System.out.println("position " + position1);
            if(position1==1){
                SavedReports.checkSelectedField[(position1-1)] = false;
            }
            else{
                SavedReports.checkSelectedField[(position1+1)] = false;
            }
        } else {
            SavedReports.checkSelectedField[position1] = false;
        }
        EditText txtSearch = (EditText)((Activity)context).findViewById(R.id.txtSearch);
        if(SavedReports.checkSelectedField[0]){
            System.out.println("Changing hint");
            txtSearch.setHint("2000-12-31");
        }else{
            txtSearch.setHint("Search");
        }
        this.notifyDataSetChanged();

    }

    private class ViewHolder {
        TextView tv;
        RadioButton chkbox;
    }
}
