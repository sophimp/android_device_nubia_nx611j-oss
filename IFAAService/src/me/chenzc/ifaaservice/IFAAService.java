package me.chenzc.ifaaservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IHwBinder;
import android.os.RemoteException;
import android.util.Log;

import com.fingerprints.service.IIfaaApi;

import java.util.ArrayList;

import vendor.nubia.ifaa.V1_0.IIfaa;

public class IFAAService extends Service {
    private static final String TAG = "SY IFAAService";

    private IIfaa mIIfaaDaemon;

    private IBinder mIBinder = new IIfaaApi.Stub() {
        @Override
        public byte[] processCmd(int cmd, int param1, int param2, byte[] send_buf, int length) throws RemoteException {
            if (getIIfaaDaemon() == null) {
                Log.e(TAG, "processCmd: no iIfaaDaemon!");
                return null;
            }
            ArrayList<Byte> sendList = new ArrayList<Byte>();
            if (send_buf != null) {
                for (byte b : send_buf) {
                    sendList.add(b);
                }
            }
            if (send_buf == null) {
                Log.e(TAG, "send_buf = null");
            }
            ArrayList<Byte> resultList = mIIfaaDaemon.processCmd(cmd, param1, param2, sendList, length);
            if (resultList == null || resultList.size() == 0) {
                Log.e(TAG, "processCmd: failed !");
                return null;
            }
            int resultLength = resultList.size();
            byte[] result = new byte[resultLength];
            for (int i = 0; i < resultLength; i++) {
                result[i] = resultList.get(i);
            }
            return result;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {

        return mIBinder;
    }

    private synchronized IIfaa getIIfaaDaemon() {
        if (mIIfaaDaemon == null) {
            Log.i(TAG, "try to connect IIfaa");
            try {
                mIIfaaDaemon = IIfaa.getService();
            } catch (RemoteException e) {
                Log.d(TAG, "Failed to get IIfaa interface");
            }
            if (mIIfaaDaemon == null) {
                Log.w(TAG, "IIfaa HIDL not available, can\\'t get IIfaaDaemon");
                return null;
            }
            IHwBinder binder = mIIfaaDaemon.asBinder();
            binder.linkToDeath(null, 0);
        }
        Log.d(TAG, "Get IIfaaDaemon success!");
        return mIIfaaDaemon;
    }
}
