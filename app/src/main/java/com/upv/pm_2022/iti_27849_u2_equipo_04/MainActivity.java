package com.upv.pm_2022.iti_27849_u2_equipo_04;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnFlecha, btnEliminar;
    Spinner spnrExportar;
    //DragAndDropView canvas;
    MySurfaceView canvas;

    ArrayList<String> opciones = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFlecha = findViewById(R.id.btnFlecha);
        btnEliminar = findViewById(R.id.btnEliminar);
        spnrExportar = findViewById(R.id.opExportar);
        canvas = findViewById(R.id.canvas);

        opciones.add("Exportar como PNG");
        opciones.add("Exportar como PDF");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, opciones);

        spnrExportar.setAdapter(adapter);

        spnrExportar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){

                    case 0:{
                        //Aquí es para quien le tocó lo de exportar como PNG, mandas llamar tus métodos

                        break;
                    }

                    case 1:{
                        //Aquí es para quien le tocó lo de exportar como PDF, mandas llamar tus métodos

                        break;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Aquí es para la opción de eliminar que supuestamente quería el profe
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Aquí es para quien le tocó agregarle las flechas
        btnFlecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}