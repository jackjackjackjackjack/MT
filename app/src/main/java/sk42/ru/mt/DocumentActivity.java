package sk42.ru.mt;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ksoap2.serialization.SoapObject;


public class DocumentActivity extends Activity implements TableRow.OnClickListener {

    private final int IOError_retry_times = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Убираем заголовок
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Убираем панель уведомлений
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.document);
        MyApp.setContext(this);
        refreshView();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        EditText mMyEditText = (EditText) findViewById(R.id.Document_edtBarcode);

        mMyEditText.addTextChangedListener
                (
                        new TextWatcher(){
                            public void afterTextChanged(Editable s) {
                                if (s.length() == 0) return;
                                char c =  s.charAt(s.length() - 1);
                                if(c=='\n') {
                                    String barcode =  s.toString();
                                    barcode = barcode.substring(0, barcode.length() - 1);
                                    s.clear();
                                    findProductByBarcode(barcode);
                                }
                            }
                            public void beforeTextChanged(CharSequence s, int start, int count, int after)
                            {

                            }
                            public void onTextChanged(CharSequence s, int start, int before, int count)   {                                }
                        }
                );


    }

    public void refreshView(){
        setHint();

        myView myview = new myView(this);

        Order order = Model.getCurrentOrder();

        TextView tvNumber = (TextView) findViewById(R.id.tvNumber);
        TextView tvTotal = (TextView) findViewById(R.id.tvTotal);

        if(order.number.isEmpty()) tvNumber.setText(" еще не присвоен");
        else tvNumber.setText(order.number);
        tvTotal.setText(String.format("%.2f", order.total));

        TableLayout table = (TableLayout) findViewById(R.id.tableDocument);

        table.removeAllViews();


        // table header
        TableRow tr = new TableRow(this);


        TextView tv = myview.getHeaderTextView("Товар", 4);
        tr.addView(tv);

        tv = myview.getHeaderTextView( "Кол-во", 3);
        tr.addView(tv);

        tv = myview.getHeaderTextView( "Сумма", 3);
        tr.addView(tv);

        table.addView(tr);


        if(order.products.size() > 0) {
            for (int i = 0; i < order.products.size(); i++) {
                Product p = order.getRow(i);
                tr = new TableRow(this);

                tv = myview.getValueTextView(p.name, 4);
                tr.addView(tv);


                tv = myview.getValueTextViewCenter(p.totalQtyBuy.toString(), 3);
                tr.addView(tv);

                tv = myview.getValueTextViewCenter(String.format("%.2f", p.total), 3);
                tr.addView(tv);

                tr.setBackgroundResource(R.drawable.box);

                tr.setId(i);
                tr.setOnClickListener(this);


                table.addView(tr);
            }
        }
    }

    public void btnSaveTo1C(View view) {
        if(Model.getCurrentOrder().products.size() == 0) {
            MyApp.notifyNegative("В документе нет товаров!");
            return;
        }
        if(Model.QuerySent())
        {
            String text = "Обрабатывается запрос, подождите!";
            MyApp.notifyNegative(text);
            setHint(text);
            return;
        }
        DocumentActivity.AsyncTaskSaveOrder1C task = new AsyncTaskSaveOrder1C();
        task.execute();
    }

    class AsyncTaskSaveOrder1C extends AsyncTask<String, Void, String> {
        SoapObject soap;

        @Override
        protected String doInBackground(String... params) {
            try {
                Model.setQuerySent();
                SoapRequests soapRequest = new SoapRequests();
                soap = soapRequest.saveOrder1C();
                if(Model.isIOError()) {
                    for(int i = 0; i < IOError_retry_times; i++){
                        soap = soapRequest.saveOrder1C();
                        if(soap != null) break;
                    }

                }
                if(soap == null) {
                    String text = "Ошибка (Null) сохранения документа в базу, попробуйте еще раз!";
                    MyApp.notifyNegative(text);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            String res = Model.readSaveResultFromSOAP(soap);
            if(res.isEmpty()) {
                String text = "Документ сохранен  в 1С с номером " + Model.getCurrentOrder().number;
                MyApp.notifyPositive(text);
                refreshView();
                finishActivity();
            }
            else{
                MyApp.notifyNegative(res);
                //refreshView();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    class AsyncTaskQueryBarcode1C extends AsyncTask<String, Void, String> {
        SoapObject soap;
        String barcode;

        @Override
        protected String doInBackground(String... params) {
            Model.setQuerySent();
            try {
                String b = params[0];
                SoapRequests soapRequest = new SoapRequests();
                barcode = b;
                soap = soapRequest.findBarcode(barcode);
                if(Model.isIOError()) {
                    for(int i = 0; i < IOError_retry_times; i++){
                        soap = soapRequest.findBarcode(barcode);
                        if(soap != null) break;

                    }

                }

                if(soap == null) {
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //запихать полученные данные в модель данных
            //послать бродкаст
            String res = Model.readProductDataFromSOAP(soap);

            if(res.isEmpty()) {
                //MyApp.notifyPositive();
                openProductEditActivity();
            }
            else
            {
                MyApp.notifyNegative(res);
                setHint(res);
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    public void findProductByBarcode(String barcode){
        String text = "Обрабатывается запрос, подождите!";
        setHint(text);
        if(Model.QuerySent()){
            MyApp.notifyNegative(text);
            return;
        }
        Product p = Model.findProductInCurrentOrder(barcode);
        if ( p != null) {
            Model.setCurrentProduct(p);
            openProductEditActivity();
            return;
        }

        AsyncTaskQueryBarcode1C task = new AsyncTaskQueryBarcode1C();
        task.execute(barcode);
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        Model.setCurrentProduct(i);
        TableLayout t = (TableLayout) findViewById(R.id.tableDocument);
        TableRow tr = (TableRow) t.getChildAt(i+1);
            for (int j = 0; j < tr.getChildCount(); j++) {
                tr.getChildAt(j).setBackgroundResource(R.drawable.active_row);
            }
        Intent intent = new Intent(this, EditProductActivity.class);
        this.startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
            return true;
//        }
        //return super.onOptionsItemSelected(item);
    }

    void openProductEditActivity(){
        Intent intent = new Intent(this, EditProductActivity.class);
        this.startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Model.updateCurrentOrder();
        refreshView();
    }

    public void btnOKClick(View view) {
        finishActivity();
    }

    private void finishActivity(){
        Model.saveCurrentOrder();
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }


    private void setHint(String text){
        EditText edt = (EditText) findViewById(R.id.Document_edtBarcode);
        edt.setHint(text);
    }
    private void setHint(){
        String text = "Сканируйте или введите штрихкод";
        EditText edt = (EditText) findViewById(R.id.Document_edtBarcode);
        edt.setHint(text);
    }

}
