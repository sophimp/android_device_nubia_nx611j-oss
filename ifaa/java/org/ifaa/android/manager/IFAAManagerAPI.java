package org.ifaa.android.manager;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;

import com.fingerprints.service.IIfaaApi;

public class IFAAManagerAPI extends IFAAManager {

    private static final int BIND_IFAASER_SERVICE_TIMEOUT = 10000;

    private static final int ACTIVITY_START_SUCCESS = 0;
    private static final int ACTIVITY_START_FAILED = -1;

    private static final int AUTHTYPE_FINGER = 1;
    private static final int COMMAND_FAIL = -1;
    private static final int COMMAND_OK = 0;
    private static final String DEVICE_MODEL = "Nubia-";
    private static final int INTERFACE_VERSION = 1;
    private static final String TAG = "FpServiceExt";
    private int mAuthType;
    private Context mContext;
    private IIfaaApi mService;

    static final String IFAA_SERVICE_PACKAGE = "me.chenzc.ifaaservice";
    static final String IFAA_SERVICE_CLASS = "me.chenzc.ifaaservice.IFAAService";

    static final ComponentName IFAA_SERVICE_COMPONENT = new ComponentName(
            IFAA_SERVICE_PACKAGE,
            IFAA_SERVICE_CLASS);

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IIfaaApi.Stub.asInterface(service);
            synchronized (mConnection) {
                notifyAll();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    public IFAAManagerAPI(Context context, int authType) {
        this.mContext = context;
        this.mAuthType = authType;
    }

    public int getSupportBIOTypes(Context context) {
        if (context == null) {
            return COMMAND_FAIL;
        }
        Log.d(TAG, "[IFAAManagerAPI] GetSupportBIOTypes:1");
        return 1;
    }

    public int startBIOManager(Context context, int authType) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("com.android.settings",
                    "com.android.settings.Settings$SecuritySettingsActivity"));
            context.startActivity(intent);
         } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return ACTIVITY_START_FAILED;
         } finally {
            return ACTIVITY_START_SUCCESS;
         }
    }


    public String getDeviceModel() {
        String model = DEVICE_MODEL + Build.DEVICE;
        Log.d(TAG, "[IFAAManagerAPI] GetDeviceModel is:" + model);
        return model;
    }

    public byte[] processCmd(Context context, byte[] param) {
        Log.d(TAG, "[IFAAManagerAPI] Do processCmd!");
        return processCmdApi(context, param);
    }

    public int getVersion() {
        Log.d(TAG, "[IFAAManagerAPI] GetVersion is:1");
        return 1;
    }

    private void initService() {
        if (mService == null) {
            Intent service = new Intent().setComponent(IFAA_SERVICE_COMPONENT);
            mContext.bindService(service, mConnection, Context.BIND_AUTO_CREATE);
            synchronized (mConnection) {
                try {
                    mConnection.wait(BIND_IFAASER_SERVICE_TIMEOUT);
                } catch (InterruptedException e) {
                    Slog.e(TAG, "exception while binding IFAAService: " + e);
                }
            }
        }
    }

    private byte[] processCmdApi(Context context, byte[] bytes) {
        initService();
        try {
        	int length = bytes.length;
        	int cmd = 10001;
        	int param2 = 0;
        	int param3 = 0;
            return mService.processCmd(cmd, param2, param3, bytes, length);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

}
