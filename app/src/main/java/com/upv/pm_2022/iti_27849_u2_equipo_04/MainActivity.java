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
    private final static String STR_REGION="\\documentclass[12pt]{article}\n\\usepackage{tikz}\n" +
                                           "\n\\begin{document}\n\n\\begin{center}\n\\begin{tikz" +
                                           "picture}[scale=0.2]\n\\tikzstyle{every node}+=[inner" +
                                           " sep=0pt]";
    private final static String END_REGION="\\end{tikzpicture}\n\\end{center}\n\n\\end{document}\n";
    private final static String DRAW_COMMAND="\\draw";
    private final static String FILL_COMMAND="\\fill";
    private final static String COLOR       ="[black]";
    private final static int CONVERSION_RATIO = 27;

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
     * @param figures a list containing nodes and arrows
     */
    private static String toLatex(ArrayList<Figure> figures) {
        String latex_output = STR_REGION;
        for(Figure figure : figures) {
            if(figure instanceof State) {
                State node = (State)figure;
                // Draw circle
                latex_output+=DRAW_COMMAND + ' ' + COLOR + " (" + node.getX() + ',' + node.getY() +
                              " (" + (State.r/CONVERSION_RATIO) + ");\n";
                if(!node.name.isEmpty()) // Draw name
                    latex_output+=DRAW_COMMAND + ' ' + COLOR + " (" + node.getX() + ',' +
                                  node.getY() + "node" + "{$" + node.name + "$};\n";
                if(node.flag) // Draw inner circle (final state)
                    latex_output+=DRAW_COMMAND + ' ' + COLOR + " (" + node.getX()+','+ node.getY() +
                            " (" + ((State.r*(1-State.ratio_percentage))/CONVERSION_RATIO) + ");\n";
            }
            else { // if(figure instanceof Arrow)
                Arrow arrow = (Arrow)figure;
//                if(arrow.isArc())
                    // Draw arched line
//                else
                latex_output += DRAW_COMMAND + COLOR + " (" + arrow.getX() + ',' + arrow.getY() +
                                ") --" + " (" + arrow.endX + ',' + arrow.endY + ");\n";
                // TODO: Draw arrow head, should save this info in Arrow class

            }
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