package com.amaze.filemanager.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amaze.filemanager.R;
import com.amaze.filemanager.utils.provider.UtilitiesProviderInterface;

/**
 * Created by vishal on 8/28/2017.
 *
 * Dialog created to authenticate a SMB connection in case user didn't choose to remember password
 */

public class SmbAuthenticateDialog extends DialogFragment {

    public static final String SMB_NAME = "name";   // connection name as in drawer
    public static final String SMB_PATH = "path";   // full connection path
    public static final String TAG = "fragment_dialog_smb_authenticate";

    private UtilitiesProviderInterface utilitiesProviderInterface;
    private String smbName, smbPath, emptyPassword;
    private SmbAuthenticationListener smbAuthenticationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utilitiesProviderInterface = (UtilitiesProviderInterface) getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());

        smbName = getArguments().getString(SMB_NAME);
        smbPath = getArguments().getString(SMB_PATH);
        emptyPassword = String.format(getString(R.string.cantbeempty),getString(R.string.password));

        if (getActivity() instanceof SmbAuthenticationListener) {
            smbAuthenticationListener = (SmbAuthenticationListener) getActivity();
        }

        materialDialogBuilder.title(getResources().getString(R.string.authenticate_password));

        return materialDialogBuilder.build();
    }

    public interface SmbAuthenticationListener {

        /**
         * Called after user has entered the password and want to proceed, actually authentication
         * will be done by API later, this just denotes to proceed with the process
         */
        void done();

        /**
         * Called after user has cancelled the authentication dialog, user don't wish to proceed
         * and we'll return from all the drawer callbacks after closing the drawer.
         */
        void cancel();
    }
}
