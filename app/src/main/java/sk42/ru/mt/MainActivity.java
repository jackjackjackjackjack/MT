package sk42.ru.mt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class MainActivity extends Activity implements android.view.View.OnClickListener {

    TableLayout table;
    TableRow tr;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Убираем заголовок
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Убираем панель уведомлений
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main_activity);
        MyApp.setContext(this);

        setDeviceCode();

        refreshView();
    }

    public void refreshView(){

        Orders orders = Model.getOrders();

        myView myView = new myView(this);

        table = (TableLayout) findViewById(R.id.table);
        table.removeAllViews();

        if(orders.size() == 0){
            tr = new TableRow(this);

            TextView tv =  new TextView(this);
            tv.setText("Пока здесь пусто, можно создать новый заказ.");
            tr.addView(tv);
            table.addView(tr);
        }
        else {
            // table header
            tr = new TableRow(this);

            tv = myView.getHeaderTextView("Номер", 5);
            tr.addView(tv);

            tv = myView.getHeaderTextView("Сумма", 5);
            tr.addView(tv);

            table.addView(tr);
        }


        for(int index = 0; index < orders.size(); index++){

            Order order = orders.getOrderByIndex(index);

            tr = new TableRow(this);

            tv =  myView.getValueTextViewCenter(order.number, 5);
            tr.addView(tv);

            tv =  myView.getValueTextViewCenter(String.format("%.2f", order.total),5);
            tr.addView(tv);

            tr.setBackgroundResource(R.drawable.box);

            tr.setId(index);

            tr.setOnClickListener(this);

            table.addView(tr);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(android.view.View v) {
        int index = v.getId();
        TableRow tbl_row = (TableRow) findViewById(index);
        for (int i = 0; i < tbl_row.getChildCount(); i++) {
            tbl_row.getChildAt(i).setBackgroundResource(R.drawable.active_row);
        }
       Model.setCurrentOrder(index);
       openCurrentOrder();

    }


    public void btnNewDoc(View view) {

        Model.addNewOrder();
        openCurrentOrder();
    }

    private void openCurrentOrder(){
        Intent myIntent = new Intent(this, DocumentActivity.class);
        this.startActivityForResult(myIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshView();
    }

    public void btnClose(View view) {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private void setDeviceCode(){
        SharedPreferences sPrefs = getSharedPreferences("SettingsActivity", MODE_PRIVATE);
        String deviceCode = sPrefs.getString("deviceCode", "");
        Model.setDeviceCode(deviceCode);
    }

}
