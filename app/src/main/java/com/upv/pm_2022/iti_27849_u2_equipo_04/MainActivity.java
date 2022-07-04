package com.upv.pm_2022.iti_27849_u2_equipo_04;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

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
    private Bitmap bitmap;
    private Canvas canvas;
    private HashMap<Figure, List<Figure>> adjacency_list;
    private static String START_REGION = "\\documentclass[12pt]{article}\n\\usepackage{tikz}\n" +
                                         "\n\\begin{document}\n\n\\begin{center}\n\\begin{tikz" +
                                         "picture}[scale=0.2]\n\\tikzstyle{every node}+=[inner" +
                                         " sep=0pt]";
    private static String END_REGION   = "\\end{tikzpicture}\n\\end{center}\n\n\\end{document}\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjacency_list = new HashMap<>();
//        canvas = new Canvas(bitmap);
        // want fullscreen, we hide Activity's title and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(new DragAndDropView(this));
    }
    /**
     * Get the latex representation of the graph
     * @param adjacency_list a list representation nodes and their connections
     */
    private static String toLatex(HashMap<Figure, List<Figure>> adjacency_list) {
        String latex_output = START_REGION;
        for(Map.Entry<Figure, List<Figure>> entry : adjacency_list.entrySet()) {
            Figure node = entry.getKey();
            List<Figure> adjacency = entry.getValue();

            latex_output += "\n";
        }
        return latex_output + END_REGION;
    }
}