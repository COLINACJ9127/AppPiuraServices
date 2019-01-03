package com.piuraservices.piuraservices.views.activitiestelefonia.movistar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.piuraservices.piuraservices.R;
import com.piuraservices.piuraservices.adapters.enosa.ListaInfoContactosEnosaAdapter;
import com.piuraservices.piuraservices.adapters.telefonia.movistar.ListaInfoContactosMovistarAdapter;
import com.piuraservices.piuraservices.models.enosa.InfoContactosEnosamodel;
import com.piuraservices.piuraservices.models.telefonia.movistar.InfoContactosMovistarmodel;
import com.piuraservices.piuraservices.services.http;
import com.piuraservices.piuraservices.views.activitiesenosa.DetalleContactoEnosaActivity;
import com.piuraservices.piuraservices.views.activitiesenosa.InfoContactosEnosaActivity;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class InfoContactosMovistarActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener  {

    //declare variables
    //prograso loading
    ProgressDialog progreso;
    //list view de reclamos
    ListView listacontactos;
    //lista data del modelo de contactos
    List<InfoContactosMovistarmodel> list_contactos;
    //Array list for to http and to converter to gson
    ArrayList<InfoContactosMovistarmodel> lista = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_contactos_movistar);
        getSupportActionBar().setTitle("Información de Contactos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listacontactos=(ListView) findViewById(R.id.id_lista_contactos_movistar);
        listacontactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(InfoReclamosEnosaActivity.this, "Click me reclamos"+i, Toast.LENGTH_SHORT).show();
                final int pos = i;
                Intent intent=new Intent(InfoContactosMovistarActivity.this, DetalleContactoMovistarActivity.class);
                startActivity(intent);
                mostrarDetalle(list_contactos.get(pos));
            }
        });
        listarContactosMovistar();
    }
        //lista contactos enosa con http
        public void listarContactosMovistar(){
            dialog();
            String url="informacion/listacontactos/3";
            http.get(getApplicationContext(), url, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    System.out.println(responseString);
                    Toast.makeText(getApplicationContext(), "Error de Conexion", Toast.LENGTH_SHORT).show();
                    progreso.hide();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    System.out.println(responseString);
                    try {
                        //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        lista=new Gson().fromJson(responseString,new TypeToken<ArrayList<InfoContactosMovistarmodel>>(){}.getType());
                        listacontactos.setAdapter(new ListaInfoContactosMovistarAdapter(getApplicationContext(),lista));
                        listacontactos.setOnItemClickListener(InfoContactosMovistarActivity.this);
                        listacontactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                mostrarDetalle(lista.get(i));
                            }
                        });
                        progreso.hide();
                    }
                    catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            });
        }
        //mostrardetalle lista
        public void mostrarDetalle(final InfoContactosMovistarmodel contactoMovistar){
            //capturar datos
            Bundle bundle=new Bundle();
            bundle.putSerializable("contactoMovistar",contactoMovistar);
            bundle.putString("centerKey",contactoMovistar.getNombreempresa().toString());
            bundle.putString("direccionKey",contactoMovistar.getDireccion().toString());
            bundle.putString("telefonoKey",contactoMovistar.getTelefono().toString());
            bundle.putString("horarioKey",contactoMovistar.getHorario().toString());
            bundle.putString("tiposervicioKey",contactoMovistar.getTipoatencion().toString());
            Intent intent=new Intent(InfoContactosMovistarActivity.this, DetalleContactoMovistarActivity.class);
            Bundle parametros = new Bundle();
            String center = contactoMovistar.getNombreempresa().toString();
            String diretion = contactoMovistar.getDireccion().toString();
            String phone = contactoMovistar.getTelefono().toString();
            String horarioatencion = contactoMovistar.getHorario().toString();
            String type = contactoMovistar.getTipoatencion().toString();
            parametros.putString("centerKey",center);
            parametros.putString("direccionKey",diretion);
            parametros.putString("telefonoKey",phone);
            parametros.putString("horarioKey",horarioatencion);
            parametros.putString("tiposervicioKey",type);
            intent.putExtras(parametros);
            startActivity(intent);
        }
        //progres dialog
        public void dialog() {
            //progreso = new ProgressDialog(EpsInfoReclamosActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            progreso = new ProgressDialog(InfoContactosMovistarActivity.this, ProgressDialog.BUTTON_POSITIVE);
            // set indeterminate style
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // set title and message
            progreso.setTitle("Procesando");
            progreso.setMessage("Loading...");
            // and show it
            progreso.show();
            progreso.setCancelable(false);
        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    }
