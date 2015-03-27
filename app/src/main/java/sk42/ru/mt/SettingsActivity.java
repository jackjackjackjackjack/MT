package sk42.ru.mt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    String deviceCode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        deviceCode = sp.getString("deviceCode","");
        EditText edt = (EditText) findViewById(R.id.edtDeviceCode);
        edt.setText(deviceCode);
    }
    void savePrefs() {
        EditText edt = (EditText) findViewById(R.id.edtDeviceCode);
        deviceCode = edt.getText().toString();
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("deviceCode", deviceCode);
        ed.commit();
        Model.setDeviceCode(deviceCode);
        Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show();
    }


    public void btnSaveSettings(View view) {
        savePrefs();
       // finish();
    }
}
