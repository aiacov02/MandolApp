package cy.ac.ucy.cs.mandolapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.ArrayList;

import cy.ac.ucy.cs.mandolapp.R;
import cy.ac.ucy.cs.mandolapp.ReportActivities.ReportTwitterNew;

/**
 * Created by andreas on 20/03/2018.
 */

public class DropDownListAdapterAuth extends BaseAdapter {

    private ArrayList<String> mListItems;
    private LayoutInflater mInflater;
    private Button mSelectedItems;
    public static int selectedCount = 0;
    private static String firstSelected = "";
    private ViewHolder holder;
    private static String selected = "";	//shortened selected values representation

    public static String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        DropDownListAdapterAuth.selected = selected;
    }

    public DropDownListAdapterAuth(Context context, ArrayList<String> items,
                                   Button btn) {
        mListItems = new ArrayList<String>();
        mListItems.addAll(items);
        mInflater = LayoutInflater.from(context);
        mSelectedItems = btn;
        selectedCount = ReportTwitterNew.selectedAuthoritiesNumber;
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
            convertView = mInflater.inflate(R.layout.drop_down_list_row, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.SelectOption);
            holder.chkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
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

        if(ReportTwitterNew.checkSelectedAuth[position])
            holder.chkbox.setChecked(true);
        else
            holder.chkbox.setChecked(false);
        return convertView;
    }


    /*
     * Function which updates the selected values display and information(checkSelectedAuth[])
     * */
    private void setText(int position1){
        if (!ReportTwitterNew.checkSelectedAuth[position1]) {
            ReportTwitterNew.checkSelectedAuth[position1] = true;
            selectedCount++;
        } else {
            ReportTwitterNew.checkSelectedAuth[position1] = false;
            selectedCount--;
        }
        ReportTwitterNew.selectedAuthoritiesNumber = selectedCount;

        if (selectedCount == 0) {
            mSelectedItems.setText("Choose Category");
        } else if (selectedCount == 1) {
            for (int i = 0; i < ReportTwitterNew.checkSelectedAuth.length; i++) {
                if (ReportTwitterNew.checkSelectedAuth[i] == true) {
                    firstSelected = mListItems.get(i);
                    break;
                }
            }
            mSelectedItems.setText(firstSelected);

            setSelected(firstSelected);
        } else if (selectedCount > 1) {
            for (int i = 0; i < ReportTwitterNew.checkSelectedAuth.length; i++) {
                if (ReportTwitterNew.checkSelectedAuth[i] == true) {
                    firstSelected = mListItems.get(i);
                    break;
                }
            }
            mSelectedItems.setText(firstSelected + " & "+ (selectedCount - 1) + " more");
            setSelected(firstSelected + " & "+ (selectedCount - 1) + " more");
        }
    }

    private class ViewHolder {
        TextView tv;
        CheckBox chkbox;
    }
}
