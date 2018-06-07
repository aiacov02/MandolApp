package cy.ac.ucy.cs.mandolapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cy.ac.ucy.cs.mandolapp.Database.ArrayString;
import cy.ac.ucy.cs.mandolapp.MyApplication;
import cy.ac.ucy.cs.mandolapp.R;
import cy.ac.ucy.cs.mandolapp.SettingsActivities.Settings;

/**
 * Created by andreas on 20/03/2018.
 */

public class DropDownListAdapterAuthSettings extends BaseAdapter {

    private ArrayList<String> mListItems;
    private LayoutInflater mInflater;
    private Button mSelectedItems;
    private static int selectedCount = 0;
    private static String firstSelected = "";
    private ViewHolder holder;
    private static String selected = "";
    List<String> Authorities;
    Context context;

    //shortened selected values representation

    public static String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        DropDownListAdapterAuthSettings.selected = selected;
    }

    public DropDownListAdapterAuthSettings(Context context, ArrayList<String> items,
                                           Button btn) {
        this.context = context;
        mListItems = new ArrayList<String>();
        mListItems.addAll(items);
        mInflater = LayoutInflater.from(context);
        mSelectedItems = btn;
        selectedCount = Settings.selectedAuthoritiesNumber;

        Authorities = Arrays.asList(context.getResources().getStringArray(R.array.authority_array));


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

        if(Settings.checkSelectedAuth[position])
            holder.chkbox.setChecked(true);
        else
            holder.chkbox.setChecked(false);
        return convertView;
    }


    /*
     * Function which updates the selected values display and information(checkSelectedAuth[])
     * */
    private void setText(int position1){
        if (!Settings.checkSelectedAuth[position1]) {
            Settings.checkSelectedAuth[position1] = true;
            selectedCount++;
        } else {
            Settings.checkSelectedAuth[position1] = false;
            selectedCount--;
        }
        Settings.selectedAuthoritiesNumber = selectedCount;

        if (selectedCount == 0) {
            mSelectedItems.setText("Choose Authority");
        } else if (selectedCount == 1) {
            for (int i = 0; i < Settings.checkSelectedAuth.length; i++) {
                if (Settings.checkSelectedAuth[i] == true) {
                    firstSelected = mListItems.get(i);
                    break;
                }
            }
            mSelectedItems.setText(firstSelected);

            setSelected(firstSelected);
        } else if (selectedCount > 1) {
            for (int i = 0; i < Settings.checkSelectedAuth.length; i++) {
                if (Settings.checkSelectedAuth[i] == true) {
                    firstSelected = mListItems.get(i);
                    break;
                }
            }
            mSelectedItems.setText(firstSelected + " & "+ (selectedCount - 1) + " more");
            setSelected(firstSelected + " & "+ (selectedCount - 1) + " more");
        }

        List<String> SelectedAuths =  new ArrayList<String>();
        for (int i = 0; i <Settings.checkSelectedAuth.length; i++) {
            if(Settings.checkSelectedAuth[i]){
                SelectedAuths.add(Authorities.get(i));
            }
        }
        String[] AuthoritiesArray = SelectedAuths.toArray(new String[SelectedAuths.size()]);

        SharedPreferences sharedPref = MyApplication.getAppContext().getSharedPreferences("MandolappSettings",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("DefaultAuthorities", ArrayString.convertArrayToString(AuthoritiesArray));
        editor.apply();

    }

    private class ViewHolder {
        TextView tv;
        CheckBox chkbox;
    }
}
