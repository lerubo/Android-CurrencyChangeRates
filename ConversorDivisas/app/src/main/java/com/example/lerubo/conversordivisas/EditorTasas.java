package com.example.lerubo.conversordivisas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


public class EditorTasas extends AppCompatActivity {
    ListView miListView;
    Moneda[] monedas = new Moneda[11];
    double[] listaR = new double[monedas.length];
    Context c;
    Adaptador adap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_tasas);
        c=this;
        //iniciar toolbar
        Toolbar barra =(Toolbar)findViewById(R.id.barraEditorTasas);
        setSupportActionBar(barra);

        //recepcion de lista de objetos de la Main Activity
        listaR =  getIntent().getExtras().getDoubleArray("listaRatios");

        //Creacion de la lista
        miListView = (ListView)findViewById(R.id.lv_l2_lista);
        monedas[0]=new Moneda("EUR",listaR[0], R.drawable.m1,0);
        monedas[1]=new Moneda("GBP",listaR[1],R.drawable.m2,1);
        monedas[2]=new Moneda("USD",listaR[2],R.drawable.m3,2);
        monedas[3]=new Moneda("INR",listaR[3],R.drawable.m4,3);
        monedas[4]=new Moneda("KRW",listaR[4],R.drawable.m5,4);
        monedas[5]=new Moneda("CNY",listaR[5],R.drawable.m6,5);
        monedas[6]=new Moneda("BRL",listaR[6],R.drawable.m7,6);
        monedas[7]=new Moneda("ISK",listaR[7],R.drawable.m8,7);
        monedas[8]=new Moneda("RUB",listaR[8],R.drawable.m9,8);
        monedas[9]=new Moneda("JPY",listaR[9],R.drawable.m10,9);
        monedas[10]=new Moneda("MXN",listaR[10],R.drawable.m11,10);
        adap = new Adaptador(monedas,c);
        miListView.setAdapter(adap);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menueditor,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.idGuardar){
            //Codigo al pinchar un item del toolbar
            //quiero un toast "Cambios guardados" y actualizar el string-array
            for(int i=0;i<monedas.length;i+=1){
                listaR[i] = monedas[i].getCantidad();
            }

            Intent i = new Intent();
            i.putExtra("listaRatios",listaR);
            setResult(RESULT_OK,i);
            finish();
            Toast.makeText(c,R.string.str_Guardar,Toast.LENGTH_SHORT).show();
        }
        return true;
    }



}
