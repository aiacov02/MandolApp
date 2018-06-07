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
import cy.ac.ucy.cs.mandolapp.SavedReportsActivities.SavedReportsTwitter;

/**
 * Created by andreas on 20/03/2018.
 */

public class DropDownListAdapterSRTwitterAuth extends BaseAdapter {

    private ArrayList<String> mListItems;
    private LayoutInflater mInflater;
    private Button mSelectedItems;
    private static int selectedCount = 0;
    private static String firstSelected = "";
    private ViewHolder holder;
    private static String selected = "";	//shortened selected values representation

    public static String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        DropDownListAdapterSRTwitterAuth.selected = selected;
    }

    public DropDownListAdapterSRTwitterAuth(Context context, ArrayList<String> items,
                                            Button btn) {
        mListItems = new ArrayList<String>();
        mListItems.addAll(items);
        mInflater = LayoutInflater.from(context);
        mSelectedItems = btn;
        selectedCount = SavedReportsTwitter.selectedAuthoritiesNumber;
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

        if(SavedReportsTwitter.checkSelectedAuth[position])
            holder.chkbox.setChecked(true);
        else
            holder.chkbox.setChecked(false);
        return convertView;
    }


    /*
     * Function which updates the selected values display and information(checkSelectedAuth[])
     * */
    private void setText(int position1){
        if (!SavedReportsTwitter.checkSelectedAuth[position1]) {
            SavedReportsTwitter.checkSelectedAuth[position1] = true;
            selectedCount++;
        } else {
            SavedReportsTwitter.checkSelectedAuth[position1] = false;
            selectedCount--;
        }
        SavedReportsTwitter.selectedAuthoritiesNumber = selectedCount;

        if (selectedCount == 0) {
            mSelectedItems.setText("Choose Authority");
        } else if (selectedCount == 1) {
            for (int i = 0; i < SavedReportsTwitter.checkSelectedAuth.length; i++) {
                if (SavedReportsTwitter.checkSelectedAuth[i] == true) {
                    firstSelected = mListItems.get(i);
                    break;
                }
            }
            mSelectedItems.setText(firstSelected);

            setSelected(firstSelected);
        } else if (selectedCount > 1) {
            for (int i = 0; i < SavedReportsTwitter.checkSelectedAuth.length; i++) {
                if (SavedReportsTwitter.checkSelectedAuth[i] == true) {
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
