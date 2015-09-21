package org.zarroboogs.maps.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapStatus;

import org.zarroboogs.maps.R;
import org.zarroboogs.maps.ui.offlinemaps.OfflineMapActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;

public class OfflineMapDownloadService extends Service implements
        OfflineMapManager.OfflineMapDownloadListener {
    final public static int OFFLINEMAP_DOWNLOAD_NOTIFICATION_ID = 99;
    final public static String TYPE_PROVINCE = "province";
    final public static String TYPE_CITY = "city";

    private OfflineMapDownloadServiceBinder mBinder;
    private NotificationManager mNotificationManager;
    private OfflineMapManager mOfflineMapManager;
    private Notification.Builder mNotificationBuilder;
    private Handler mHandler = new ServiceHandler(this);
    LinkedList<Intent> intentLinkedList = new LinkedList<>();
    private boolean isCurDownloading = false;

    public class OfflineMapDownloadServiceBinder extends Binder {
        public int groupPosition = -1;
        public int childPosition = -1;

        public OfflineMapDownloadService getService() {
            return OfflineMapDownloadService.this;
        }
    }

    public OfflineMapDownloadService() {
    }

    @Override
    public void onCreate() {
        mBinder = new OfflineMapDownloadServiceBinder();
        mOfflineMapManager = OfflineMapManagerWrapper.getOfflineMapManager(this);
        OfflineMapManagerWrapper.addOfflineMapDownloadListener(this);
        Intent intent = new Intent(this, OfflineMapActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_file_download)
                .setContentTitle("正在下载离线地图")
                .setContentText("...")
                .setContentIntent(pendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intentLinkedList.add(intent);
        //如果当前没有正在下载的任务，则开始下载
        if (!isCurDownloading)
            mHandler.sendEmptyMessage(OfflineMapStatus.SUCCESS);
        return START_NOT_STICKY;
    }

    /*高德地图只支持单任务*/
    protected void onHandleIntent() {
        Intent intent = intentLinkedList.poll();
        if (intent != null) {
            Bundle intentExtras = intent.getExtras();
            String name = intentExtras.getString("name");
            String type = intentExtras.getString("type");
            Log.d("SSS", "[OfflineMapDownloadService] start downloading name: " + name + " type: " + type);
            if (type != null && name != null) {
                try {
                    if (type.equals(TYPE_CITY)) {
                        isCurDownloading = mOfflineMapManager.downloadByCityName(name);
                    } else if (type.equals(TYPE_PROVINCE)) {
                        isCurDownloading = mOfflineMapManager.downloadByProvinceName(name);
                    }
                    mBinder.groupPosition = intentExtras.getInt("groupPosition");
                    mBinder.childPosition = intentExtras.getInt("childPosition");
                    //如果正在下载则设通知为ongoing
                    if (isCurDownloading) {
                        mNotificationBuilder.setOngoing(true);
                    }
                } catch (AMapException e) {
                    isCurDownloading = false;
                    Log.e("SSS", "[OfflineMapDownloadService] download " + name + " AMapException!");
                }
            }
        } else {
            //如果没有任务，则退出
            stopSelf();
        }
    }

    @Override
    public void onDownload(int status, int completeCode, String name) {
        StringBuilder sb = new StringBuilder("");

        if (status == OfflineMapStatus.SUCCESS) {
            isCurDownloading = false;
            sb.append(name).append(" 下载完成");
            mNotificationBuilder.setOngoing(false);
            mHandler.sendEmptyMessage(OfflineMapStatus.SUCCESS);
        } else if (status == OfflineMapStatus.ERROR) {
            isCurDownloading = false;
            sb.append("下载失败");
            mNotificationBuilder.setOngoing(false);
        } else if (status == OfflineMapStatus.LOADING) {
            sb.append("正在下载 ").append(name).append(" ").append(completeCode).append("%");
        } else if (status == OfflineMapStatus.UNZIP) {
            sb.append("正在解压 ").append(name).append(" ").append(completeCode).append("%");
        }

        mNotificationBuilder.setContentText(sb);
        mNotificationManager.notify(OFFLINEMAP_DOWNLOAD_NOTIFICATION_ID, mNotificationBuilder.build());
    }

    private static class ServiceHandler extends Handler {
        private WeakReference<OfflineMapDownloadService> a;

        ServiceHandler(OfflineMapDownloadService a) {
            this.a = new WeakReference<>(a);
        }

        @Override
        public void handleMessage(Message msg) {
            OfflineMapDownloadService b = a.get();
            if (b != null) {
                if (msg.what == OfflineMapStatus.SUCCESS) {
                    b.onHandleIntent();
                }
            }
            super.handleMessage(msg);
        }
    }

    /*包裹离线地图下载管理器，用于共享使用*/
    public static class OfflineMapManagerWrapper
            implements OfflineMapManager.OfflineMapDownloadListener {
        private static OfflineMapManagerWrapper mOfflineMapManagerWrapper;
        private OfflineMapManager mOfflineMapManager;
        private ArrayList<WeakReference<OfflineMapManager.OfflineMapDownloadListener>>
                mOfflineMapDownloadListeners = new ArrayList<>();

        public OfflineMapManagerWrapper(Context context) {
            mOfflineMapManager = new OfflineMapManager(context, this);
        }

        public static OfflineMapManager getOfflineMapManager(Context context) {
            if (mOfflineMapManagerWrapper == null) {
                mOfflineMapManagerWrapper = new OfflineMapManagerWrapper(context);
            }
            return mOfflineMapManagerWrapper.mOfflineMapManager;
        }

        public static synchronized void addOfflineMapDownloadListener(OfflineMapManager.OfflineMapDownloadListener l) {
            if (mOfflineMapManagerWrapper != null) {
                mOfflineMapManagerWrapper.mOfflineMapDownloadListeners.add(new WeakReference<>(l));
            }
        }

        /*离线地图下载时会回调，利用它来更新ui的多个已注册的listeners*/
        @Override
        public void onDownload(int status, int completeCode, String name) {
            for (WeakReference<OfflineMapManager.OfflineMapDownloadListener> l : mOfflineMapDownloadListeners) {
                OfflineMapManager.OfflineMapDownloadListener listener = l.get();
                if (listener != null) {
                    listener.onDownload(status, completeCode, name);
                }
            }
        }
    }
}