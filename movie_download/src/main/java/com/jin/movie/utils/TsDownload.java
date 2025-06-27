package com.jin.movie.utils;



import android.os.Environment;

import com.jin.movie.bean.PlayBackItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * Ts 多线程下载类。
 */
public class TsDownload implements Runnable {


    private String downloadUrl;
    private int serialNumber;
    private String filename;
    private PlayBackItem playBackItem;

    private static DownloadCallback downloadCallback;


    public TsDownload(int serialNumber, String downloadUrl,PlayBackItem playBackItem) {
        this.downloadUrl = downloadUrl;
        this.serialNumber = serialNumber;
        this.filename = serialNumber + ".ts";
        this.playBackItem = playBackItem;
    }

    @Override
    public void run() {
        URL url = null;
        try {
            url = new URL(this.downloadUrl);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Referer","http://MAfAIOo0E8EMOWPA.black");

            File tsFile = new File(Utils.INSTANCE.getPathForMovieName(playBackItem) + File.separator + filename);
            if (downloadCallback != null) {
                String msg = Thread.currentThread().getName() + " 正在下载：" + this.filename + "  总片段 " + playBackItem.getTsFileTotalCounts() + " ---> " + playBackItem.getVideoTitle();
                downloadCallback.onSuccess(msg);
            }
            RandomAccessFile file = new RandomAccessFile(tsFile, "rw");
            InputStream inputStream = conn.getInputStream();

            byte[] buffer = new byte[1024];
            int hasRead = 0;
            while ((hasRead = inputStream.read(buffer)) != -1) {
                file.write(buffer, 0, hasRead);
            }

            Utils.INSTANCE.getTASK_COUNTS_ITEM().decrementAndGet();
            file.close();
            inputStream.close();
        } catch (Exception e) {
            if (downloadCallback != null) {
                downloadCallback.onError(e.toString());
            }
        }
    }

    public static void setDownloadCallback(DownloadCallback callback) {
        downloadCallback = callback;
    }

    public interface DownloadCallback {
        void onError(String msg);
        void onSuccess(String msg);
    }
}
