package com.example.lerubo.conversordivisas;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Adaptador extends BaseAdapter {
    Moneda[] data;
    Context c;

    public Adaptador(Moneda[] monedas, Context c) {
        this.data = monedas;
        this.c = c;
    }

    public Moneda[] getData() {
        return data;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int i) {
        return data[i];
    }

    @Override
    public long getItemId(int i) {
        return data[i].getId();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflador = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vista_elemento = inflador.inflate(R.layout.elementos, viewGroup, false);

        ImageView icono = (ImageView) vista_elemento.findViewById(R.id.iv_moneda);
        TextView nombre = (TextView) vista_elemento.findViewById((R.id.tv_Nombre));
        final EditText cantidad = (EditText) vista_elemento.findViewById((R.id.et_cantidad));

        icono.setImageResource(data[i].getImagen());
        nombre.setText(data[i].getNombre());
        cantidad.setText(String.valueOf(data[i].getCantidad()));

        cantidad.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(cantidad.getText().length()>0){

                    try{
                        String valorNuevo = cantidad.getText().toString();
                        double doubleNuevo = Double.parseDouble(valorNuevo);
                        if (doubleNuevo != 0) {
                            data[i].setCantidad(doubleNuevo);
                        }else{
                            Toast.makeText(c,R.string.str_avisoCero,Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return vista_elemento;
    }
}