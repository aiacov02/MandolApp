package cy.ac.ucy.cs.mandolapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cy.ac.ucy.cs.mandolapp.Database.DatabaseHelper;
import cy.ac.ucy.cs.mandolapp.R;
import cy.ac.ucy.cs.mandolapp.ReportActivities.Report;
import cy.ac.ucy.cs.mandolapp.SavedReportsActivities.SavedReports;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class CustomAdapter extends ArrayAdapter<Report> implements View.OnClickListener{

    private ArrayList<Report> dataSet;
    Context mContext;
    View finalView;

    // View lookup cache
    private static class ViewHolder {
        TextView txtDate;
        TextView txtDescription;
        ImageView imgApp;



    }



    public CustomAdapter(ArrayList<Report> data, Context context) {
        super(context, R.layout.savedreports_row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {


        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Report report=(Report) object;


        System.out.println("CLICKED");

        switch (v.getId())
        {

        }


    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Report report = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.savedreports_row_item, parent, false);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtSavedReportsDate);
            viewHolder.txtDescription = (TextView) convertView.findViewById(R.id.txtSavedReportsDescription);
            viewHolder.imgApp = (ImageView) convertView.findViewById(R.id.imgSavedReportsIcon);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.txtDate.setText(report.getDate());
        if(report.getDescription()!=null && !report.getDescription().equals("")){
            viewHolder.txtDescription.setText(report.getDescription());
        }else{
            viewHolder.txtDescription.setText("-");
        }
        if(report.getApp().equals("facebook")){
            viewHolder.imgApp.setImageResource(R.drawable.facebookicon);
        }
        else if(report.getApp().equals("twitter")){
            viewHolder.imgApp.setImageResource(R.drawable.twitter);
        }
        else if(report.getApp().equals("browser")){
            viewHolder.imgApp.setImageResource(R.drawable.browser);
        }

        finalView= convertView;
        final View convertView2 = convertView;
        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.btnSavedReportsDelete);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(report.getId() + " " + report.getApp());
                DatabaseHelper DB=null;
                try {
                    SavedReports savedReports = (SavedReports)mContext;
                    savedReports.DeleteReport(report,position);
//                    dataSet.remove(position);
//                    finalView = getView(position,convertView2,parent);

                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    if(DB!=null)DB.close();
                }

            }
        });
//        imageButton.setOnLongClickListener(new View.OnLongClickListener(){
//            @Override
//            public boolean onLongClick(View view){
//                System.out.println("LONG pressed");
//                return true;
//            }
//        });

        // Return the completed view to render on screen
        return convertView;
    }


}
