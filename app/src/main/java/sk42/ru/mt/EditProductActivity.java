    package sk42.ru.mt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class EditProductActivity extends Activity implements TableRow.OnClickListener {
    myView myview;
    boolean hidden = true;
    boolean decoded;
    Bitmap decodedByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Убираем заголовок
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Убираем панель уведомлений
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.product);

        hidden = false;
        decoded = false;
        MyApp.setContext(this);
        myview = new myView(this);
        Model.setCurrentStore(0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        refreshView();

        EditText edt = (EditText) findViewById(R.id.Edit_edtQty);
        edt.addTextChangedListener(
                new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
//                            s.append("0");
                            Model.getCurrentStore().qtybuy = 0f;
                            Model.updateCurrentProduct();
                            //  refreshView();
                            return;
                        }
                        String newstr = s.toString();
                        float qtytobuy;
                        try {
                            qtytobuy = Float.parseFloat(newstr);
                        }
                        catch (Exception e){
                            qtytobuy = 0;
                        }

                        if (qtytobuy <= Model.getCurrentStore().qty)
                        {
                            Model.getCurrentStore().qtybuy = qtytobuy;
                        }
                        else {
                            Model.getCurrentStore().qtybuy = Model.getCurrentStore().qty;
                        }
                        Model.updateCurrentProduct();
                        refreshView();

                    }


                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                }
        );


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void refreshView() {


        Product p = Model.getCurrentProduct();

        TextView tv = (TextView) findViewById(R.id.Edit_tvProductName);
        tv.setText(p.name);

        ImageView img = (ImageView) findViewById(R.id.Edit_imageView);

        String image = Model.getCurrentProduct().image;
        if(!image.isEmpty() && !decoded) {
            decoded = true;
            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        img.setImageBitmap(decodedByte);


        tv = (TextView) findViewById(R.id.Edit_tvTotalQty);
        tv.setText(p.totalQtyBuy.toString());

        tv = (TextView) findViewById(R.id.Edit_tvTotal);
        tv.setText(String.format("%.2f", p.total));


        TableLayout t = (TableLayout) findViewById(R.id.Edit_TableStores);

        t.removeAllViews();
        TableRow trdata = new TableRow(this);
        trdata.addView(getValueTextView(getString(R.string.StoreCaption), R.integer.store_weight));
        trdata.addView(getValueTextView(getString(R.string.CharacteristicCaption), R.integer.charact_weight));
        trdata.addView(getValueTextView(getString(R.string.PriceCaption), R.integer.price_weight));
        trdata.addView(getValueTextView(getString(R.string.StockQtyCaption), R.integer.qty_weight));
        trdata.addView(getValueTextView(getString(R.string.QtyToBuyCaption), R.integer.qtybuy_weight));
        for (int k = 0; k < trdata.getChildCount(); k++) {

            trdata.getChildAt(k).setBackgroundResource(R.drawable.box);

        }
        t.addView(trdata);

        if (p.stores.size() > 0) {
            for (int i = 0; i < p.stores.size(); i++) {
                Store store = p.stores.get(i);
                String charact = "";
                if(!store.charact.equals("None")) charact = store.charact;
                trdata = new TableRow(this);
                trdata.addView(getValueTextView(store.name, R.integer.store_weight));
                trdata.addView(getValueTextView(charact, R.integer.charact_weight));
                trdata.addView(getValueTextViewCenter(String.format("%.2f", store.price),R.integer.price_weight));
                trdata.addView(getValueTextViewCenter(store.qty.toString(),R.integer.qty_weight));
                trdata.addView(getValueTextViewCenter(store.qtybuy.toString(),R.integer.qtybuy_weight));
                trdata.setBackgroundResource(R.drawable.box);
//                for (int k = 0; k < trdata.getChildCount(); k++) {
//
//                    trdata.getChildAt(k).setBackgroundResource(R.drawable.box);
//
//                }
                trdata.setOnClickListener(this);
                trdata.setId(i);
                t.addView(trdata);
            }
            Store currentStore = Model.getCurrentStore();
            if (currentStore.name == null) {
                Model.setCurrentStore(0);
            }

            EditText edt = (EditText) findViewById(R.id.Edit_edtQty);
            float tq = Float.parseFloat(edt.getText().toString());

            if (tq != currentStore.qtybuy)
            {
                edt.setText(currentStore.qtybuy.toString());
            }
            int rowindex = Model.getCurrentStoreIndex();
            if ( rowindex >= 0 && t.getChildCount() > 1) {
                TableRow tr = (TableRow) t.getChildAt(rowindex + 1); //плюс один потому что одна строка - заголовок таблицы
                for (int i = 0; i < tr.getChildCount(); i++) {
                    tv = (TextView) tr.getChildAt(i);
                    tv.setTextColor(getResources().getColor(R.color.active_row_font_color));
                    tv.setBackgroundResource(R.drawable.active_row);
                }
            }
        }


    }

    @Override
    //клик на строке склада
    //установим текущий склад
    public void onClick(View v) {
        int row = v.getId();
        Model.setCurrentStore(row);
        refreshView();
    }

    public void btnPlusСlick(View view) {
        Store currentStore = Model.getCurrentStore();
        if (currentStore.qtybuy + 1 > currentStore.qty)
            MyApp.notifyNegative();
        else{
            currentStore.qtybuy++;
            Model.updateCurrentProduct();
            refreshView();
        }
    }


    public void btnMinusClick(View view) {
        Store currentStore = Model.getCurrentStore();
        if (currentStore.qtybuy - 1 < 0) {
            MyApp.notifyNegative();
        } else {
            currentStore.qtybuy--;
            Model.updateCurrentProduct();
            refreshView();
        }
    }

    public void btnOkClick(View view) {
        Model.saveCurrentProduct();
        finishActivity();
    }

    private void finishActivity() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

//    public void btnDeleteClick(View view) {
//        Model.deleteCurrentProduct();
//        finishActivity();
//    }


    TextView getValueTextView(String text, int weight){

        TextView tv = new TextView(this);
        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        lp1.weight = weight ;
        tv.setLayoutParams(lp1);
        tv.setMaxWidth(150);
        //tv.setHeight(40);
        //tv.setMaxHeight(50);
        tv.setGravity(Gravity.LEFT);
        tv.setText(text);
        tv.setTextSize(myview.getFontSize());
        tv.setTextColor(getResources().getColor(R.color.forecolor_values));
        tv.setPadding(15, 5, 15, 5);

        //tv.setBackgroundResource(R.drawable.box);
        //tv.setLayoutParams(lp);
        return tv;
    }

    TextView getValueTextViewCenter(String text, int weight){

        TextView tv = new TextView(this);
        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);


        lp1.weight = weight ;
        tv.setLayoutParams(lp1);
        tv.setGravity(Gravity.CENTER);
//        tv.setMaxWidth(150);
//        tv.setHeight(50);
//        tv.setMaxHeight(50);
        tv.setText(text);
        tv.setTextSize(myview.getFontSize());
        tv.setTextColor(getResources().getColor(R.color.forecolor_values));
        tv.setPadding(15, 5, 15, 5);

        //tv.setBackgroundResource(R.drawable.box);
        //tv.setLayoutParams(lp);
        return tv;
    }


}




