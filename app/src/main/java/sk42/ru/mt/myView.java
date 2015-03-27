package sk42.ru.mt;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;

public class myView {

    Context context;

    public myView(Context mcontext)
    {
        this.context =  mcontext;
    }


    TextView getValueTextView(String text, int weight){

        TextView tv = new TextView(context);
        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        lp1.weight = weight ;
        tv.setLayoutParams(lp1);
        tv.setMaxWidth(150);
        //tv.setHeight(40);
        //tv.setMaxHeight(50);
        tv.setGravity(Gravity.LEFT);
        tv.setText(text);
        tv.setTextSize(getFontSize());
        tv.setTextColor(context.getResources().getColor(R.color.forecolor_values));
        tv.setPadding(15, 5, 15, 5);

        //tv.setBackgroundResource(R.drawable.box);
        //tv.setLayoutParams(lp);
        return tv;
    }

    TextView getValueTextViewCenter(String text, int weight){

        TextView tv = new TextView(context);

        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        lp1.weight = weight ;

        tv.setLayoutParams(lp1);
        tv.setGravity(Gravity.CENTER);
//        tv.setMaxWidth(150);
//        tv.setHeight(50);
//        tv.setMaxHeight(50);
        tv.setText(text);
        tv.setTextSize(getFontSize());
        tv.setTextColor(context.getResources().getColor(R.color.forecolor_values));
        tv.setPadding(15, 5, 15, 5);

        //tv.setBackgroundResource(R.drawable.box);
        //tv.setLayoutParams(lp);
        return tv;
    }


    TextView getHeaderTextView(String text, int weight){


        TextView tv = new TextView(this.context);

        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        lp1.weight = weight ;
        tv.setLayoutParams(lp1);

        tv.setText(text);
        tv.setTextColor(Color.YELLOW);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(context.getResources().getColor(R.color.forecolor_caption));
        tv.setTextSize(getFontSize());
        tv.setPadding(5, 5, 5, 5);
        tv.setBackgroundResource(R.drawable.box);
        //tv.setLayoutParams(lp);
        return tv;
    }




    public int getFontSize(){
        if ((context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return 20;

        }
        if ((context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            return 10;

        }
        return 13;

    }

}
