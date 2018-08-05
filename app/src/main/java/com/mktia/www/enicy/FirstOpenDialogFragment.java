package com.mktia.www.enicy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class FirstOpenDialogFragment extends DialogFragment {

    public FirstOpenDialogFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.what_is_new)
                .setMessage(R.string.contents_of_what_is_new)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyAccountsActivity.mFirstOpen = false;
                    }
                });

        return builder.create();
    }
}
