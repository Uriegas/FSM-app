package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

public class DeleteDialog extends Dialog {
    public Activity activity;
    private static final String TAG = "FSM_canvas";

    public DeleteDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setContentView(R.layout.dialog_delete);
        setTitle("Eliminar figura");

        findViewById(R.id.saveBtn).setOnClickListener(v -> { // TODO: Delete figure
            Log.d(TAG, "Aquí elimina jiji");
        });

        findViewById(R.id.cancelBtn).setOnClickListener(v ->
                dismiss()
        );
    }
}
