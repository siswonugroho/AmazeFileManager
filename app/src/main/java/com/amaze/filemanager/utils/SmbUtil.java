package com.amaze.filemanager.utils;

import android.content.Context;
import android.text.TextUtils;

import com.amaze.filemanager.exceptions.CryptException;
import com.amaze.filemanager.utils.files.CryptUtil;

/**
 * Created by Vishal on 30-05-2017.
 *
 * Class provides various utility methods for SMB client
 */

public class SmbUtil {

    // random string so that there is very low chance of it clashing with user set password
    // it denotes no password is applied to the smb connection, this will not be encrypted
    // obvious security reasons
    public static final String SMB_NO_PASSWORD = "Zj#Zo2bqhyXZ3R7%";

    /**
     * Enum class denotes the supported smb versions and returns a compatible int value
     * for the ease of persistence
     */
    public enum SMB_VERSION {

        V1(1),
        V2(2);

        private int version;

        SMB_VERSION(int i) {
            this.version = i;
        }

        public int getVersion() {
            return this.version;
        }
    }

    /**
     * Parse path to decrypt smb password
     * @return
     */
    public static String getSmbDecryptedPath(Context context, String path) throws CryptException {

        if (!(path.contains(":") && path.contains("@"))) {
            // smb path doesn't have any credentials
            return path;
        }

        StringBuffer buffer = new StringBuffer();

        buffer.append(path.substring(0, path.indexOf(":", 4)+1));
        String encryptedPassword = path.substring(path.indexOf(":", 4)+1, path.lastIndexOf("@"));

        if (!TextUtils.isEmpty(encryptedPassword)) {

            String decryptedPassword = CryptUtil.decryptPassword(context, encryptedPassword);

            buffer.append(decryptedPassword);
        }
        buffer.append(path.substring(path.lastIndexOf("@"), path.length()));

        return buffer.toString();
    }

    /**
     * Parse path to encrypt smb password
     * @param context
     * @param path
     * @return
     */
    public static String getSmbEncryptedPath(Context context, String path) throws CryptException {

        if (!validatePath(path)) {
            // smb path doesn't have any credentials
            return path;
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(path.substring(0, path.indexOf(":", 4)+1));
        String decryptedPassword = path.substring(path.indexOf(":", 4)+1, path.lastIndexOf("@"));

        if (!TextUtils.isEmpty(decryptedPassword)) {

            String encryptPassword =  CryptUtil.encryptPassword(context, decryptedPassword);

            buffer.append(encryptPassword);
        }
        buffer.append(path.substring(path.lastIndexOf("@"), path.length()));

        return buffer.toString();
    }

    public static String getNonRememberPath(String path) {

        if (validatePath(path)) {
            return path;
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(path.substring(0, path.indexOf(":", 4)+1));
        String decryptedPassword = path.substring(path.indexOf(":", 4)+1, path.lastIndexOf("@"));

        if (!TextUtils.isEmpty(decryptedPassword)) {

            buffer.append(SMB_NO_PASSWORD);
        }
        buffer.append(path.substring(path.lastIndexOf("@"), path.length()));

        return buffer.toString();
    }

    public static SMB_VERSION getSmbVersion(int smbVersion) {
        switch (smbVersion) {
            case 1:
                return SMB_VERSION.V1;
            case 2:
                return SMB_VERSION.V2;
            default:
                return SMB_VERSION.V1;
        }
    }

    /**
     * Validates whether the smb path has any credentials or not
     * @param path
     * @return true if username and password is set in connection, false if connection is anon
     */
    private static boolean validatePath(String path) {
        if (!(path.contains(":") && path.contains("@"))) {
            return false;
        } else {
            return true;
        }
    }
}
