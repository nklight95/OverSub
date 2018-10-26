package com.nklight.ultsub.Utils;

import java.io.File;

public interface DownloadCallback {
    void onDownload(File file);
    void onFail(Exception e);
    void onProgressUpdate(String percent);
}
