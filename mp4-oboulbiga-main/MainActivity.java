package com.example.myapplicationmp4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements TextWatcher, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener{


    private static final int REQUEST_MSG_PHONE = 1;

    private EditText editTextBillAmount;
    private TextView textViewBillAmount;
    private TextView textViewseekBarLabel;
    private TextView textViewTipLabel;
    private TextView textViewTipAmount;
    private TextView textViewTotalLabel;
    private TextView textViewTotalAmount;
    private SeekBar seekBarTip;
    private TextView textViewPerPersonTotal;
    private double tip;
    private double total;
    private double perPersonTotal;



    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private int numPeople = 1;


    private RadioGroup roundOptions;
    private RadioButton roundNo;
    private RadioButton roundTip;
    private RadioButton roundTotal;



    private double billAmount = 0.0;
    private double percent = .15;

    private static final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat =
            NumberFormat.getPercentInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextBillAmount = (EditText)findViewById(R.id.editText_BillAmount);
        editTextBillAmount.addTextChangedListener((TextWatcher)this);



        textViewBillAmount = (TextView)findViewById(R.id.textView_BillAmount);
        textViewseekBarLabel = (TextView)findViewById(R.id.textView_seekBarLabel);
        textViewTipLabel =  (TextView)findViewById(R.id.textView_tipLabel);
        textViewTotalLabel = (TextView)findViewById(R.id.textView_totalLabel);
        textViewTipAmount = (TextView)findViewById(R.id.textView_Tip);
        textViewTotalAmount = (TextView)findViewById(R.id.textView_totalAmount);
        seekBarTip = (SeekBar)findViewById(R.id.tip_seekBar);
        spinner = (Spinner)findViewById(R.id.spinner_numPeople);
        adapter = ArrayAdapter.createFromResource(this,R.array.spinner_labels,R.layout.support_simple_spinner_dropdown_item);
        roundOptions = (RadioGroup)findViewById(R.id.radio_group);
        roundNo = (RadioButton)findViewById(R.id.radio_no);
        roundTip = (RadioButton)findViewById(R.id.radio_tip);
        roundTotal = (RadioButton)findViewById(R.id.radio_total);
        textViewPerPersonTotal = (TextView)findViewById(R.id.textView_perPersonTotal);


        seekBarTip.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener)this);


        textViewTipAmount.setText(currencyFormat.format(0));
        textViewTotalAmount.setText(currencyFormat.format(0));



        if(spinner != null){
            spinner.setOnItemSelectedListener(this);
            spinner.setAdapter(adapter);
        }



    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }


    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        billAmount = Double.parseDouble(charSequence.toString()) / 100;
        textViewBillAmount.setText(currencyFormat.format(billAmount));
        calculate();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        percent = progress / 100.0;
        calculate();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private void calculate() {

        if(roundNo.isChecked()) {
            tip = billAmount * percent;
            total = tip + billAmount;
            perPersonTotal = total / numPeople;
        }


        else if(roundTip.isChecked()){
            tip = billAmount * percent;
            tip = Math.ceil(tip);
            total = tip + billAmount;
            perPersonTotal = total / numPeople;
        }


        else if (roundTotal.isChecked()){
            tip = billAmount * percent;
            total = tip + billAmount;
            total = Math.ceil(total);
            perPersonTotal = total / numPeople;
        }


        textViewseekBarLabel.setText(percentFormat.format(percent));


        textViewTipAmount.setText(currencyFormat.format(tip));

        textViewTotalAmount.setText(currencyFormat.format(total));

   
        textViewPerPersonTotal.setText(currencyFormat.format(perPersonTotal));
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        numPeople = Integer.parseInt(parent.getItemAtPosition(position).toString());
        calculate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void checkButton(View v) {
        calculate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.info_icon) {
            AlertDialog.Builder alertDialog =
                    new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle(R.string.info_title);
            alertDialog.setMessage(R.string.info_txt);
            alertDialog.show();
            return true;
        }


        else if (id == R.id.share_icon){
            String msg = "Bill = " + currencyFormat.format(billAmount) + "\nTip = " + currencyFormat.format(tip)
                    + "\nTotal = " + currencyFormat.format(total) + "\n\nTotal Per Person = " + currencyFormat.format(perPersonTotal);

            Intent msgIntent = new Intent(Intent.ACTION_VIEW);
            msgIntent.setData(Uri.parse("sms:"));
            msgIntent.putExtra("sms_body", msg);
            startActivity(msgIntent);

            if (msgIntent.resolveActivity(getPackageManager()) != null);
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.SEND_SMS}, REQUEST_MSG_PHONE);
                } else {
                    startActivity(msgIntent);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }
}