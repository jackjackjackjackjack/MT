package sk42.ru.mt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ErrorActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error);
        String text = "Прозошла ошибка, но я вам о ней не скажу, потому что у вас документов нету!";
        String source = "А хрен его знает!";
        Intent intent = getIntent();
        if (intent.hasExtra("text")) {
            text = intent.getStringExtra("text");
        }
        if (intent.hasExtra("source")) {
            source = intent.getStringExtra("source");
        }

        TextView tv = (TextView) findViewById(R.id.Error_tvText);
        tv.setText(text);
        tv = (TextView) findViewById(R.id.Error_tvSource);
        tv.setText(tv.getText() + " " + source);
    }

    public void btnOKClick(View view) {
        finish();
    }
}
