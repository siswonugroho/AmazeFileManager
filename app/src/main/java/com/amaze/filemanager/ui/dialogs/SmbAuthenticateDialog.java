package com.amaze.filemanager.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amaze.filemanager.R;
import com.amaze.filemanager.utils.SimpleTextWatcher;
import com.amaze.filemanager.utils.SmbUtil;
import com.amaze.filemanager.utils.color.ColorUsage;
import com.amaze.filemanager.utils.provider.UtilitiesProviderInterface;

/**
 * Created by vishal on 8/28/2017.
 *
 * Dialog created to authenticate a SMB connection in case user didn't choose to remember password
 */

public class SmbAuthenticateDialog extends DialogFragment {

    public static final String SMB_PATH = "path";   // full connection path
    public static final String SMB_VERSION = "smb_version"; // smb version for the connection
    public static final String TAG = "fragment_dialog_smb_authenticate";

    private UtilitiesProviderInterface utilitiesProviderInterface;
    private String smbPath, emptyPassword;
    private SmbAuthenticationCallbacks smbAuthenticationCallbacks;
    private View rootView;
    private TextView mUsernameTextView, mIpTextView;
    private AppCompatEditText mPasswordEditText;
    private AppCompatCheckBox mRememberPasswordCheckBox;
    private TextInputEditText mPasswordTextInputEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utilitiesProviderInterface = (UtilitiesProviderInterface) getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        rootView = View.inflate(getActivity(), R.layout.dialog_smb_authenticate, null);
        mUsernameTextView = (TextView) rootView.findViewById(R.id.username);
        mIpTextView = (TextView) rootView.findViewById(R.id.ip_address);
        mPasswordEditText = (AppCompatEditText) rootView.findViewById(R.id.edit_text_password);
        mRememberPasswordCheckBox = (AppCompatCheckBox) rootView.findViewById(R.id.checkbox_remember_password);
        mPasswordTextInputEditText = (TextInputEditText) rootView.findViewById(R.id.text_input_layout_password);

        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());

        smbPath = getArguments().getString(SMB_PATH);

        String ipAddress = SmbUtil.getSmbIpAddress(smbPath);
        emptyPassword = String.format(getString(R.string.cantbeempty),getString(R.string.password));
        materialDialogBuilder.customView(rootView, true);

        if (getActivity() instanceof SmbAuthenticationCallbacks) {
            smbAuthenticationCallbacks = (SmbAuthenticationCallbacks) getActivity();
        }

        materialDialogBuilder.title(getResources().getString(R.string.authenticate_password));

        mUsernameTextView.setText(SmbUtil.getSmbUsername(smbPath));
        mIpTextView.setText(ipAddress);
        materialDialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {

                if (TextUtils.isEmpty(mPasswordEditText.getText())) {
                    mPasswordTextInputEditText.setError(String.format(getResources().getString(R.string.cantbeempty),
                            getResources().getString(R.string.password)));
                    mPasswordEditText.requestFocus();
                }

                boolean rememberPassword = false;
                if (mRememberPasswordCheckBox.isChecked())
                    rememberPassword = true;

                String originalPath = SmbUtil.buildSmbPath(smbPath, mPasswordEditText.getText().toString());
                smbAuthenticationCallbacks.authenticateSmb(originalPath,
                        rememberPassword);
            }
        });

        materialDialogBuilder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                smbAuthenticationCallbacks.cancelSmbAuthentication();
            }
        });

        materialDialogBuilder.theme(utilitiesProviderInterface.getAppTheme().getMaterialDialogTheme());
        materialDialogBuilder.negativeText(R.string.cancel);
        materialDialogBuilder.positiveText(R.string.go);

        int accentColor = utilitiesProviderInterface.getColorPreference().getColor(ColorUsage.ACCENT);
        materialDialogBuilder.positiveColor(accentColor).negativeColor(accentColor);

        return materialDialogBuilder.build();
    }

    public interface SmbAuthenticationCallbacks {

        /**
         *
         * Called after user has entered the password and want to proceed, actually authentication
         * will be done by API later, this just denotes to proceed with the process
         *
         * @param path  the path with correct smb password in it
         * @param rememberPassword whether remember password is checked or not, in which case we'll modify
         *                         database entry to include the encrypted password
         */
        void authenticateSmb(String path, boolean rememberPassword);

        /**
         * Called after user has cancelled the authentication dialog, user don't wish to proceed
         * and we'll return from all the drawer callbacks after closing the drawer.
         */
        void cancelSmbAuthentication();
    }
}
