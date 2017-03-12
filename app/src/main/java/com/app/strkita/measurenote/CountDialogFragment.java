package com.app.strkita.measurenote;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * 目標文字数設定ダイアログ
 * Created by kitada on 2017/03/06.
 */

public class CountDialogFragment extends DialogFragment {

    public CountDialogFragment() {}

    static CountDialogFragment newInstance(int num) {
        CountDialogFragment f = new CountDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.input_count_dialog, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("目標文字数を設定");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = (EditText) view.findViewById(R.id.dialog_edit);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.setView(view);
        Dialog dialog = builder.create();

        // ダイアログ外タップで消えないように設定
        dialog.setCanceledOnTouchOutside(false);

//        InputMethodManager imm = (InputMethodManager) getActivity().
//                getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        return dialog;
    }

}
