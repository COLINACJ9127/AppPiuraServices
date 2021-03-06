package com.piuraservices.piuraservices.views.activitiesenosa;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.piuraservices.piuraservices.R;
import com.piuraservices.piuraservices.models.enosa.InfoReferencialEnosamodel;
import com.piuraservices.piuraservices.models.epsgrau.InfoReferencialEpsgraumodel;
import com.piuraservices.piuraservices.services.enosa.ListaReferencialEnosaclient;
import com.piuraservices.piuraservices.services.epsgrau.ListaReferencialEpsclient;
import com.piuraservices.piuraservices.utils.Config;
import com.piuraservices.piuraservices.views.activitiesepsgrau.EPS_grauActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnosaActivity extends AppCompatActivity {

    ImageView imgtramites;
    ImageView imgreclamos;
    ProgressDialog progreso;
    //declare variables
    public TextView direccion;
    public TextView telefono;
    public TextView correo;
    public TextView horario;
    public TextView page;
    String calltelefono = "";
    String sendemail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enosa);
        getSupportActionBar().setTitle("Entidad Enosa");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgtramites = (ImageView) findViewById(R.id.img_tramitesenosa);
        imgreclamos = (ImageView) findViewById(R.id.img_reclamosenosa);
        direccion = (TextView) findViewById(R.id.tv_direccion_enosa);
        telefono = (TextView) findViewById(R.id.tv_telefono_enosa);
        correo = (TextView) findViewById(R.id.tv_email_enosa);
        horario = (TextView) findViewById(R.id.tv_horario_enosa);
        page = (TextView) findViewById(R.id.tv_pagina_enosa);
        //call funtion para listar informacion referencial
        listainfoEntidad();

    }

    //lista informacion de entidad con http OK
    public void listainfoEntidad() {
        process();
        final String url = Config.URL_SERVER;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        ListaReferencialEnosaclient servicio = retrofit.create(ListaReferencialEnosaclient.class);
        Call<List<InfoReferencialEnosamodel>> call = servicio.getInfoReferencialenosa();
        call.enqueue(new Callback<List<InfoReferencialEnosamodel>>() {
            @Override
            public void onResponse(Call<List<InfoReferencialEnosamodel>> call, Response<List<InfoReferencialEnosamodel>> response) {
                try {
                    for (InfoReferencialEnosamodel info : response.body()) {
                        if (info.getId() == 2) {
                            Log.e("DIRECCION", info.getDireccion() + "\nCORREO:" + info.getCorreo());
                            direccion.setText(info.getDireccion().toString());
                            telefono.setText(info.getTelefono().toString());
                            correo.setText(info.getCorreo().toString());
                            horario.setText(info.getHorario().toString());
                            page.setText(info.getWebentidad().toString());
                            progreso.hide();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<InfoReferencialEnosamodel>> call, Throwable t) {
                Toast.makeText(EnosaActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickedreclamos(View v) {
        imgreclamos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("views.activitiesenosa.InfoReclamosEnosaActivity");
                startActivity(intent);
            }
        });
    }

    public void onClickedtramites(View v) {
        imgtramites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("views.activitiesenosa.InfoTramitesEnosaActivity");
                startActivity(intent);
            }
        });
    }

    public void onClickOpenGoogleMaps(View v) {
        String centralenosa = "ENOSA, Piura";
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + centralenosa);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        Intent chooser = Intent.createChooser(mapIntent, "Abrir Google Maps");
        startActivity(chooser);
    }

    //funcion para enviar email
    public void onClickOpenEmail(View v) {
        final String url = Config.URL_SERVER;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        ListaReferencialEpsclient servicio = retrofit.create(ListaReferencialEpsclient.class);
        Call<List<InfoReferencialEpsgraumodel>> call = servicio.getInfoReferencialeps();
        call.enqueue(new Callback<List<InfoReferencialEpsgraumodel>>() {
            @Override
            public void onResponse(Call<List<InfoReferencialEpsgraumodel>> call, Response<List<InfoReferencialEpsgraumodel>> response) {
                try {
                    for (InfoReferencialEpsgraumodel info : response.body()) {
                        if (info.getId() == 2) {
                            Log.e("DIRECCION", info.getDireccion() + "\nTELEFONO:" + info.getTelefono());
                            sendemail = info.getCorreo().toString();
                            //metodo para  enviar email
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setData(Uri.parse("email"));
                            String[] email = {sendemail};
                            intent.putExtra(Intent.EXTRA_EMAIL, email);
                            intent.putExtra(Intent.EXTRA_SUBJECT, " ");
                            intent.putExtra(Intent.EXTRA_TEXT, " ");
                            intent.setType("message/rfc822");
                            Intent chooser = Intent.createChooser(intent, "Enviar Email");
                            startActivity(chooser);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<InfoReferencialEpsgraumodel>> call, Throwable t) {
                //Toast.makeText(EnosaActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("email"));
                String[] email = {"enosa@distriluz.com.pe"};
                intent.putExtra(Intent.EXTRA_EMAIL, email);
                intent.putExtra(Intent.EXTRA_SUBJECT, " ");
                intent.putExtra(Intent.EXTRA_TEXT, " ");
                intent.setType("message/rfc822");
                Intent chooser = Intent.createChooser(intent, "Enviar Email");
                startActivity(chooser);
            }
        });
    }
    public void onClickOpenWeb(View v) {
        Intent intent = new Intent(EnosaActivity.this, OpenWebEnosaActivity.class);
        startActivity(intent);
    }

    //funcion para acer llamadas
    public void onClickOpenCall(View v) {
        try {
            final String url = Config.URL_SERVER;
            Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
            ListaReferencialEpsclient servicio = retrofit.create(ListaReferencialEpsclient.class);
            Call<List<InfoReferencialEpsgraumodel>> call = servicio.getInfoReferencialeps();
            call.enqueue(new Callback<List<InfoReferencialEpsgraumodel>>() {
                @Override
                public void onResponse(Call<List<InfoReferencialEpsgraumodel>> call, Response<List<InfoReferencialEpsgraumodel>> response) {
                    try {
                        for (InfoReferencialEpsgraumodel info : response.body()) {
                            if (info.getId() == 2) {
                                Log.e("DIRECCION", info.getDireccion() + "\nTELEFONO:" + info.getTelefono());
                                calltelefono = info.getTelefono().toString();
                                //funciona para llamar
                                Intent i = new Intent(Intent.ACTION_DIAL);
                                String enosa = calltelefono;
                                if (enosa.trim().isEmpty()) {
                                    i.setData(Uri.parse("tel:" + enosa));
                                } else {
                                    i.setData(Uri.parse("tel:" + enosa));
                                }
                                if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    Toast.makeText(getApplication(), "Please conceda permisos para llamar", Toast.LENGTH_LONG).show();
                                    requestPermission();
                                } else {
                                    startActivity(i);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<List<InfoReferencialEpsgraumodel>> call, Throwable t) {
                    //Toast.makeText(EnosaActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    String enosa = "(073) 284050";
                    if (enosa.trim().isEmpty()) {
                        i.setData(Uri.parse("tel:" + enosa));
                    } else {
                        i.setData(Uri.parse("tel:" + enosa));
                    }
                    if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplication(), "Please conceda permisos para llamar", Toast.LENGTH_LONG).show();
                        requestPermission();
                    } else {
                        startActivity(i);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //permisos para llamadas
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
    }

    public void process() {
        try {
            //progreso = new ProgressDialog(EpsInfoReclamosActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            progreso = new ProgressDialog(EnosaActivity.this, ProgressDialog.BUTTON_POSITIVE);
            // set indeterminate style
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // set title and message
            progreso.setTitle("Procesando");
            progreso.setMessage("Loading...");
            // and show it
            progreso.show();
            //progreso.setCancelable(false);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void warningmessage() {
        final AlertDialog.Builder alertaDeError2 = new AlertDialog.Builder(EnosaActivity.this);
        alertaDeError2.setTitle("Advertencia");
        alertaDeError2.setMessage("Recargar la información .");
        alertaDeError2.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertaDeError2.create();
        alertaDeError2.show();
    }

}
