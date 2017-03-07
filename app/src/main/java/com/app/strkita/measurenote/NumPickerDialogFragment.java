package com.app.strkita.measurenote;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * 目標文字数設定ダイアログ
 * Created by kitada on 2017/03/06.
 */

public class NumPickerDialogFragment extends DialogFragment {

    public NumPickerDialogFragment() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.input_count_dialog, null, false);

        NumberPicker np = (NumberPicker) view.findViewById(R.id.numberPicker);
        np.setMaxValue(1000000);
        np.setMinValue(1);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("目標文字数を設定");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.setView(view);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
