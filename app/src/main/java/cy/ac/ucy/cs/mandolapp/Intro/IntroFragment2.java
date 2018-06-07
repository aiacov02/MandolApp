package cy.ac.ucy.cs.mandolapp.Intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cy.ac.ucy.cs.mandolapp.R;
import pl.droidsonroids.gif.GifTextView;


//generic fragment
public class IntroFragment2 extends Fragment {

    ImageView imageView;
    GifTextView gifTextView;
    int image;
    String Title;
    String Description;
    TextView textView;
    TextView textView2;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get image here
        Bundle args = getArguments();
        Title = args.getString("title");
        Description = args.getString("description");
        image = args.getInt("image");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_fragment2, container, false);
        textView = (TextView)view.findViewById(R.id.txtTitle);
        //ButterKnife.bind(this,view);
        textView.setText(Title);
        textView2 = (TextView)view.findViewById(R.id.txtDescription);
        textView2.setText(Description);
        if(image!=0){
            gifTextView = (GifTextView) view.findViewById(R.id.gifImage);
            gifTextView.setBackgroundResource(image);
        }


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public static IntroFragment2 newInstance(String title, String description) {

        Bundle args = new Bundle();
        args.putString("title",title);
        args.putString("description",description);
        IntroFragment2 fragment = new IntroFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    public static IntroFragment2 newInstance(String title, String description, int image) {

        Bundle args = new Bundle();
        args.putInt("image",image);
        args.putString("title",title);
        args.putString("description",description);
        IntroFragment2 fragment = new IntroFragment2();
        fragment.setArguments(args);
        return fragment;
    }
}