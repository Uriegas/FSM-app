package com.upv.pm_2022.iti_27849_u2_equipo_04;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Features:
 * On double click in canvas;           create a new state and focus on its name, highlight in blue.
 * On double click inside a state;      change state type.
 * On single click inside a state;      change state name,                        highlight in blue.
 * On pressed click and drag;           Create arrow to another state,            highlight in blue.
 */
public class MainActivity extends AppCompatActivity {

    DragAndDropView vista;
    FrameLayout pantalla;
    RelativeLayout botones;
    Dialog dialog_export, dialog_menu;


    private static final String TAG = "Main_Activity";
    private Bitmap bitmap;
    private Canvas canvas;
    private HashMap<State, List<State>> adjacency_list;
    private static String START_REGION = "\\documentclass[12pt]{article}\n\\usepackage{tikz}\n" +
                                         "\n\\begin{document}\n\n\\begin{center}\n\\begin{tikz" +
                                         "picture}[scale=0.2]\n\\tikzstyle{every node}+=[inner" +
                                         " sep=0pt]";
    private static String END_REGION   = "\\end{tikzpicture}\n\\end{center}\n\n\\end{document}\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjacency_list = new HashMap<>();

        vista = new DragAndDropView(this);
        pantalla = new FrameLayout(this);
        botones = new RelativeLayout(this);
        dialog_export = new Dialog(MainActivity.this);
        dialog_export.setContentView(R.layout.dialog_export);
        dialog_export.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog_menu = new Dialog(MainActivity.this);
        dialog_menu.setContentView(R.layout.dialog_menu);
        dialog_menu.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        //Creación del botón
        Button btnExportar = new Button(this);
        btnExportar.setText("Export");

        //Parametros del botón
        RelativeLayout.LayoutParams lyt = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);

        //Se le agregan los parametos al botón
        botones.setLayoutParams(params);

        // Se agrega el botón
        botones.addView(btnExportar);

        //Se le pone el alineamiento al botón
        lyt.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        lyt.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        lyt.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        btnExportar.setLayoutParams(lyt);


        //clic listener al botón
        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("Funciona");
                dialog_export.show();
            }
        });

        Button expLatex = dialog_export.findViewById(R.id.btnExpLatex);
        Button expPNG = dialog_export.findViewById(R.id.btnExpPNG);

        //listener para LATEX
        expLatex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Las acciones para exportar en LATEX
            }
        });

        //Listener para PNG
        expPNG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Las acciones para exportar en PNG
            }
        });


//        canvas = new Canvas(bitmap);
        // want fullscreen, we hide Activity's title and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Se agregan tanto el surface view como los botones a una misma pantalla
        pantalla.addView(vista);
        pantalla.addView(botones);

        //Se muestra
        setContentView(pantalla);

        //setContentView(new DragAndDropView(this));
    }
    /**
     * Get the latex representation of the graph
     * @param adjacency_list a list representation nodes and their connections
     */
    private static String toLatex(HashMap<State, List<State>> adjacency_list) {
        // Example:
        // Input :=
        //          S_1 -> S_2, S_3
        //          S_2 -> S_1, S_3
        //          S_3 -> S_4
        // Output :=
        //          \begin{tikzpicture}
        //          \tikzstyle{node}
        String latex_output = START_REGION;
        for(Map.Entry<State, List<State>> entry : adjacency_list.entrySet()) { // For each node...
            State node = entry.getKey();
            List<State> adjacency = entry.getValue();

            latex_output += "\n";
        }
        return latex_output + END_REGION;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //TODO: Cambiar switch por un if ya que solo se tiene un caso
        switch(item.getItemId()){
            case R.id.acerca_de:
                //Equipo
                dialog_menu.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}