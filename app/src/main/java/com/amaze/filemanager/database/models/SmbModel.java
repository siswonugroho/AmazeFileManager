package com.amaze.filemanager.database.models;

import com.amaze.filemanager.utils.SmbUtil;

/**
 * Created by ghuser on 8/28/2017.
 */

public class SmbModel {

    private String name, path;
    private SmbUtil.SMB_VERSION smb_version;

    public SmbModel(String name, String path, int smb_version) {
        this.name = name;
        this.path = path;
        setSmbVersion(smb_version);
    }

    public SmbModel(String name, String path, SmbUtil.SMB_VERSION smb_version) {
        this.name = name;
        this.path = path;
        this.smb_version = smb_version;
    }

    public SmbModel() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSmbVersion(int smbVersion) {
        switch (smbVersion) {
            case 1:
                this.smb_version = SmbUtil.SMB_VERSION.V1;
                break;
            case 2:
                this.smb_version = SmbUtil.SMB_VERSION.V2;
                break;
            default:
                this.smb_version = SmbUtil.SMB_VERSION.V1;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    public SmbUtil.SMB_VERSION getSmbVersion() {
        return this.smb_version;
    }
}
