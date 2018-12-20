package it.zerozero.belclock;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by David on 29/08/2017.
 */

public class InfoDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String infoStr = "DavidG (2016 - 2017)\r\n\r\nThis software is cool.\r\nAnd of course use is at your own risk. You're welcome.";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("BelClock multifunction clock app").setMessage(infoStr);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("InfoDialog", "OK");
            }
        });

        return builder.create();

    }
}
