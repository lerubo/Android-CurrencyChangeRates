package com.example.lerubo.conversordivisas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import android.widget.AdapterView.OnItemSelectedListener;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    //------Declaramos objetos a utilizar de forma global-------
    ImageButton flechas;
    Boolean directo = true;
    EditText etIn;
    Spinner sFrom, sTo;
    TextView tvOut, tvError;
    ImageButton btDespejar;
    String[] codigos;
    Tasa[] listaTasas = new Tasa[11];
    double[] listaR = new double[listaTasas.length];
    ProgressDialog progreso;
    Descarga d;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-----actualizar las tasas-------
        c=this;
        d = new Descarga();
        d.execute("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");

        //---Crear objeto tasa para recivir valor del xml externo
        listaTasas[0]= new Tasa("EUR",1);
        listaTasas[1]= new Tasa("GBP",1);
        listaTasas[2]= new Tasa("USD",1);
        listaTasas[3]= new Tasa("INR",1);
        listaTasas[4]= new Tasa("KRW",1);
        listaTasas[5]= new Tasa("CNY",1);
        listaTasas[6]= new Tasa("BRL",1);
        listaTasas[7]= new Tasa("ISK",1);
        listaTasas[8]= new Tasa("RUB",1);
        listaTasas[9]= new Tasa("JPY",1);
        listaTasas[10]= new Tasa("MXN",1);

        //-------iniciar toolbar-------
        Toolbar barra =(Toolbar)findViewById(R.id.barra);
        setSupportActionBar(barra);

        //------array valores del XML--------
        codigos = getResources().getStringArray(R.array.siglas);

        //-------Identificar Objetos con el ID de los comandos basicos---------
        etIn =(EditText) findViewById(R.id.etIntro);
        sFrom = (Spinner)findViewById(R.id.spFrom);
        sTo = (Spinner)findViewById(R.id.spTo);
        tvOut = (TextView)findViewById(R.id.tvResultado);
        btDespejar = (ImageButton) findViewById(R.id.btnClear);
        flechas = (ImageButton)findViewById(R.id.ib_arrows);
        tvError = (TextView)findViewById(R.id.tv_Error);

        //------popular los spinners----------
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.valores, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sFrom.setAdapter(adapter);
        sTo.setAdapter(adapter);
        sTo.setSelection(1);
        tvOut.setText("0.00"+ codigos[1]);

        //-----boton despejar el valor introducido-------
        btDespejar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pTo = sTo.getSelectedItemPosition();
                etIn.setText("");
                etIn.setTextSize(50);
                String s = "0.00 "+codigos[pTo];
                tvOut.setText(s);
                tvOut.setTextSize(50);
            }
        });



        //---------Calcular nuevo valor cuando cambie el valor de el EditText--------
        etIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etIn.getText().length() > 0) {
                    int pTo, pFrom;
                    if(directo) {
                        pTo = sTo.getSelectedItemPosition();
                        pFrom = sFrom.getSelectedItemPosition();
                    }
                    else{
                        pTo = sFrom.getSelectedItemPosition();
                        pFrom = sTo.getSelectedItemPosition();
                    }

                    double cantidad = 0.0;
                    try {
                        cantidad = Double.parseDouble(etIn.getText().toString());
                    }catch (Exception ex){
                        tvOut.setText("ERROR");
                        return;
                    }

                    double nuevoResultado = convertir(cantidad,pFrom, pTo);
                    String s = String.format("%,.2f",nuevoResultado);
                    s += " " + codigos[pTo];
                    //ajuste tamaño introducido
                    if(etIn.getText().length()> 9){
                        etIn.setTextSize(40);
                        if (etIn.getText().length() > 12){
                            etIn.setTextSize(30);
                        }}
                    else{
                        etIn.setTextSize(50);
                    }

                    //ajuste tamaño salida
                    if(s.length()> 9){
                        tvOut.setTextSize(40);
                        if (s.length() > 12){
                            tvOut.setTextSize(30);
                            if (s.length() > 15){
                                tvOut.setTextSize(20);
                            }}}
                    else{
                        tvOut.setTextSize(50);
                    }

                    tvOut.setText(s);
                }}

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //------Calcular nuevo valor cuando cambie el valor del Spinner------
        sFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pTo, pFrom;
                if(directo) {
                    pTo = sTo.getSelectedItemPosition();
                    pFrom = sFrom.getSelectedItemPosition();
                }
                else{
                    pTo = sFrom.getSelectedItemPosition();
                    pFrom = sTo.getSelectedItemPosition();
                }
                if((pTo==pFrom)&&(pFrom==0)){
                    pTo+=1;
                    sTo.setSelection(pTo);
                }
                if((pTo==pFrom)&&(pFrom!=0)){
                    pTo-=1;
                    sTo.setSelection(pTo);
                }
                //No realizar operacion si no hay valor en el editText
                if(etIn.getText().length() > 0) {

                    double cantidad = 0.0;
                    try {
                        cantidad = Double.parseDouble(etIn.getText().toString());
                    }catch (Exception ex){
                        tvOut.setText("ERROR");
                        return;
                    }
                    double nuevoResultado = convertir(cantidad,pFrom, pTo);
                    String s = String.format("%,.2f",nuevoResultado);
                    s += " " + codigos[pTo];

                    //ajuste tamaño salida
                    if(s.length()> 9){
                        tvOut.setTextSize(40);
                        if (s.length() > 12){
                            tvOut.setTextSize(30);
                            if (s.length() > 15){
                                tvOut.setTextSize(20);
                            }}}
                    else{
                        tvOut.setTextSize(50);
                    }

                    tvOut.setText(s);
                }}

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //-------Calcular nuevo valor cuando cambie el valor del Spinner---------
        sTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int pTo, pFrom;
                if(directo) {
                    pTo = sTo.getSelectedItemPosition();
                    pFrom = sFrom.getSelectedItemPosition();
                }
                else{
                    pTo = sFrom.getSelectedItemPosition();
                    pFrom = sTo.getSelectedItemPosition();
                }
                if((pTo==pFrom)&&(pTo==0)){
                    pFrom+=1;
                    sFrom.setSelection(pFrom);
                }
                if((pTo==pFrom)&&(pTo!=0)){
                    pFrom-=1;
                    sFrom.setSelection(pFrom);
                }
                //No realizar conversion si no hay valor en el editText
                if(etIn.getText().length() > 0) {


                    double cantidad = 0.0;
                    try {
                        cantidad = Double.parseDouble(etIn.getText().toString());
                    }catch (Exception ex){
                        tvOut.setText("ERROR");
                        return;
                    }
                    double nuevoResultado = convertir(cantidad, pFrom, pTo);
                    String s = String.format("%,.2f",nuevoResultado);
                    s += " " + codigos[pTo];

                    //ajuste tamaño salida
                    if(s.length()> 9){
                        tvOut.setTextSize(40);
                        if (s.length() > 12){
                            tvOut.setTextSize(30);
                            if (s.length() > 15){
                                tvOut.setTextSize(20);
                            }}}
                    else{
                        tvOut.setTextSize(50);
                    }
                    tvOut.setText(s);
                }}

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //------Cambio sentido de las flechas--------
        flechas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cambio de imagen al pulsar el botonImage de las flechas
                if(directo)
                    flechas.setImageResource(R.drawable.blue_arrow2);
                else
                    flechas.setImageResource(R.drawable.blue_arrow);
                directo=!directo;
                //Codigo de recalcular el cambio de divisa
                if(etIn.getText().length() > 0) {
                    int pTo, pFrom;
                    if(directo) {
                        pTo = sTo.getSelectedItemPosition();
                        pFrom = sFrom.getSelectedItemPosition();
                    }
                    else{
                        pTo = sFrom.getSelectedItemPosition();
                        pFrom = sTo.getSelectedItemPosition();
                    }
                    double cantidad = 0.0;
                    try {
                        cantidad = Double.parseDouble(etIn.getText().toString());
                    }catch (Exception ex){
                        tvOut.setText("ERROR");
                        return;
                    }
                    double nuevoResultado = convertir(cantidad,pFrom, pTo);
                    String s = String.format("%,.2f",nuevoResultado);
                    s += " " + codigos[pTo];
                    //ajuste tamaño introducido
                    if(etIn.getText().length()> 9){
                        etIn.setTextSize(40);
                        if (etIn.getText().length() > 12){
                            etIn.setTextSize(30);
                        }}
                    else{
                        etIn.setTextSize(50);
                    }
                    //ajuste tamaño salida
                    if(s.length()> 9){
                        tvOut.setTextSize(40);
                        if (s.length() > 12){
                            tvOut.setTextSize(30);
                            if (s.length() > 15){
                                tvOut.setTextSize(20);
                            }}}
                    else{
                        tvOut.setTextSize(50);
                    }
                    tvOut.setText(s);
                }
            }
        });
    }
    // ----barra herramientas creacion----
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menuprincipal,menu);
        return true;
    }
    // ----barra herramientas item seleccionado---
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.idEditar){
            //Abrir la activity de la edicion de
            Intent i = new Intent(getApplicationContext(),EditorTasas.class);
            for(int idx=0;idx<listaTasas.length;idx+=1){
                listaR[idx] = listaTasas[idx].getValor();
            }
            i.putExtra("listaRatios",listaR);
            startActivityForResult(i,23);

        }
        d = null;
        if(item.getItemId() == R.id.idActualizar){
            //Tarea Descarga
            d = new Descarga();
            d.execute("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
        }
        return true;
    }

    //------Convertidor-----
    private double convertir(double cant,int orig, int dest){
        return cant*listaTasas[dest].getValor()/listaTasas[orig].getValor();
    }

    //-----Array de tasas xml-----
    private double[] obtenerTasas(){
        String[] strTasas=getResources().getStringArray(R.array.tasas);
        double[] t = new double[strTasas.length];
        int i=0;
        for (String tasa: strTasas) {
            t[i++]=Double.parseDouble(tasa);
        }
        return t;
    }

    //---------------Tarea asyncrona para la descarga de datos XML de las tasas--------
    private class Descarga extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                progreso = new ProgressDialog(c);
                progreso.setTitle(R.string.str_tit_actualizar);
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setIndeterminate(true);
                progreso.show();

        }

        @Override
        protected String doInBackground(String... urls) {

            //Proceso de descarga de datos
            String resultado = "";
            HttpsURLConnection canal = null;
            try{
                Thread.sleep(3000);
                URL u = new URL(urls[0]);
                canal = (HttpsURLConnection) u.openConnection();
                canal.setRequestMethod("GET");
                canal.setDoOutput(true);
                canal.connect();
                InputStreamReader entrada = new InputStreamReader(canal.getInputStream());
                BufferedReader lectura = new BufferedReader(entrada);
                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = lectura.readLine()) != null){
                    sb.append(linea).append("\n");
                }
                lectura.close();
                resultado=sb.toString();
            }catch(Exception e){
                resultado = "ERROR"+ e.getMessage();
            }finally {
                canal.disconnect();
                return resultado;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            progreso.dismiss();
            if(s.startsWith("ERROR")){
                String msg=s.split(":")[1];
                if (msg.equals("http")){
                    tvError.setText("Error de actualización");
                    tvError.setVisibility(View.VISIBLE);
                }
                else{
                    tvError.setText(msg);
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }}
            //Tasas aXML
            try{
                tvError.setVisibility(View.INVISIBLE);
                for(int i=0; i < listaTasas.length; i++){
                    listaTasas[i] = parseoXML(s,listaTasas[i]);
                }

            }catch(Exception e){
                Toast.makeText(c,"Error 2",Toast.LENGTH_LONG).show();
            }


        }
    }

    //------Funcion de parseo XML---------
    private Tasa parseoXML(String datos, Tasa ts){
        XmlPullParser parser = Xml.newPullParser();
        try{
            parser.setInput(new StringReader(datos));
            int evento;
            while((evento=parser.next())!= XmlPullParser.END_DOCUMENT){
                if(evento==XmlPullParser.START_TAG){
                    String sim = parser.getName();
                    if(sim.equals("Cube")){
                        String qsim = parser.getAttributeValue(null,"currency");
                        if(ts.getSimbolo().equals(qsim)){
                            String valor = parser.getAttributeValue(null,"rate");
                            ts.setValor(valor);
                        }
                            continue;
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(c,"error 2",Toast.LENGTH_LONG).show();
            return null;
        }
        return ts;
    }


    //-----Recogida de resultado de lanzar una actividad
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==23){
            //recoger extras
            if(resultCode == RESULT_OK){
              double[] listaTemporal = new double[listaTasas.length];
              listaTemporal= data.getDoubleArrayExtra("listaRatios");
              for(int i=0;i<listaTasas.length;i+=1){
                  listaTasas[i].setValor(String.valueOf(listaTemporal[i]));
              }
                //Codigo de recalcular el cambio de divisa
                if(etIn.getText().length() > 0) {
                    int pTo, pFrom;
                    if(directo) {
                        pTo = sTo.getSelectedItemPosition();
                        pFrom = sFrom.getSelectedItemPosition();
                    }
                    else{
                        pTo = sFrom.getSelectedItemPosition();
                        pFrom = sTo.getSelectedItemPosition();
                    }
                    double cantidad = 0.0;
                    try {
                        cantidad = Double.parseDouble(etIn.getText().toString());
                    }catch (Exception ex){
                        tvOut.setText("ERROR");
                        return;
                    }
                    double nuevoResultado = convertir(cantidad,pFrom, pTo);
                    String s = String.format("%,.2f",nuevoResultado);
                    s += " " + codigos[pTo];
                    //ajuste tamaño introducido
                    if(etIn.getText().length()> 9){
                        etIn.setTextSize(40);
                        if (etIn.getText().length() > 12){
                            etIn.setTextSize(30);
                        }}
                    else{
                        etIn.setTextSize(50);
                    }
                    //ajuste tamaño salida
                    if(s.length()> 9){
                        tvOut.setTextSize(40);
                        if (s.length() > 12){
                            tvOut.setTextSize(30);
                            if (s.length() > 15){
                                tvOut.setTextSize(20);
                            }}}
                    else{
                        tvOut.setTextSize(50);
                    }
                    tvOut.setText(s);
                }
            }
        }
        if(resultCode ==  RESULT_CANCELED){
            Toast.makeText(c, R.string.str_cancelar, Toast.LENGTH_SHORT).show();
        }
    }
}
