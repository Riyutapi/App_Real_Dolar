package com.example.realparadolar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editTextValue = findViewById(R.id.editTextNumberDecimal);
        textViewResult = findViewById(R.id.dolar);

        editTextValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    double value = Double.parseDouble(s.toString());
                    requestConversion(value);
                } else {
                    textViewResult.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private static final String URL_API = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/";

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void requestConversion(double value) {
        String url = URL_API + "CotacaoDolarDia(dataCotacao=@dataCotacao)?%40dataCotacao=%27" + getCurrentDate() + "%27&%24format=json";
        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        double dolarValue = response
                                .getJSONArray("value")
                                .getJSONObject(0)
                                .getDouble("cotacaoCompra");
                        double convertedValue = value / dolarValue;
                        String formattedValue = String.format(Locale.getDefault(), "%.2f", convertedValue);
                        textViewResult.setText("$ " + formattedValue);
                    } catch ( JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace
        );
        Volley.newRequestQueue(this).add(request);
    }

}
