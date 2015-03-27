package sk42.ru.mt;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

public class MyApp extends Application {
    private static MyApp instance;
    private Context mContext;

    public MyApp() {
        instance = this;
    }

    public static MyApp getInstance() {
        return instance;
    }

    public static void showError(String source, String text){
            Intent intent = new Intent(MyApp.getInstance().getApplicationContext(), ErrorActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("source", source);
            intent.putExtra("text", text);

            getContext().startActivity(intent);

    }
    public static Context getContext(){return MyApp.getInstance().mContext;}
    public static void setContext(Context c){MyApp.getInstance().mContext = c;}

    public static void notifyNegative(String text){
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.no);
        mediaPlayer.start();
        Toast toast = Toast.makeText(MyApp.getContext(),
                text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void notifyNegative(){
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.no);
        mediaPlayer.start();
    }


    public static void notifyPositive(String text){

        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.ok1);
        mediaPlayer.start();
        Toast toast = Toast.makeText(MyApp.getContext(),
                text, Toast.LENGTH_SHORT);
        toast.show();
    }
}