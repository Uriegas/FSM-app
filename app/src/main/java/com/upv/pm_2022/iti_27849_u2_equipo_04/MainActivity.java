package com.upv.pm_2022.iti_27849_u2_equipo_04;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.upv.pm_2022.iti_27849_u2_equipo_04.DeleteDialog;

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
    DeleteDialog deleteDialog;

    private static final String TAG = "Main_Activity";
    private Bitmap bitmap;
    private Canvas canvas;
    private final static String STR_REGION="\\documentclass[12pt]{article}\n\\usepackage{tikz}\n" +
                                           "\n\\begin{document}\n\n\\begin{center}\n\\begin{tikz" +
                                           "picture}[scale=0.2]\n\\tikzstyle{every node}+=[inner" +
                                           " sep=0pt]\n";
    private final static String END_REGION="\\end{tikzpicture}\n\\end{center}\n\n\\end{document}\n";
    private final static String FILE_NAME    = "fsm_output";
    private final static float RESIZE_FACTOR = (float)0.04;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteDialog = new DeleteDialog(this);

        vista = new DragAndDropView(this);
        pantalla = new FrameLayout(this);
        botones = new RelativeLayout(this);
        dialog_export = new Dialog(MainActivity.this);
        dialog_export.setContentView(R.layout.dialog_export);
        dialog_export.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog_menu = new Dialog(MainActivity.this);
        dialog_menu.setContentView(R.layout.dialog_menu);
        dialog_menu.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                                          ViewGroup.LayoutParams.WRAP_CONTENT);

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

        // export canvas to latex
        expLatex.setOnClickListener(view -> {
            File file = new File(Environment.getExternalStorageDirectory().toString() +
                                 '/' + FILE_NAME + ".tex");
            try{
                file.delete(); file.createNewFile();
                FileWriter out = new FileWriter(Environment.getExternalStorageDirectory()
                                                .toString() + '/' + FILE_NAME + ".tex");
                out.append(toLatex(vista.getAllFigures())); out.flush(); out.close();
                Toast.makeText(getBaseContext(), "Tex file exported into root folder",
                               Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "An error occurred", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        //Listener para PNG
        expPNG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = viewToBitmap(vista);
                File file = new File(Environment.getExternalStorageDirectory().toString() +
                        '/' + FILE_NAME + ".jpg");
                try {
                    file.delete(); file.createNewFile();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                    Toast.makeText(getBaseContext(), "jpg file exported into root folder",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "An error occurred", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
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
     * Get the bitmap of the graph
     * @param view
     * @return the bitmap of the diagram
     */
    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * Get the latex representation of the graph
     * @param figures a list containing nodes and arrows
     * @return string with latex representation of the diagram
     */
    private static String toLatex(ArrayList<Figure> figures) {
        String latex_output = STR_REGION;
        for(Figure figure : figures)
            latex_output += figure.toLatex(RESIZE_FACTOR);
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