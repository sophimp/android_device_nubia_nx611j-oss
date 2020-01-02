package org.ifaa.android.manager;

import android.content.Context;
import android.util.Log;

public class IFAAManagerFactory {
    private static final String TAG = "FpServiceExt";
    private static IFAAManagerAPI mIFAAManager;

    public static IFAAManagerAPI getIFAAManager(Context context, int authType) {
        if (mIFAAManager == null) {
            mIFAAManager = new IFAAManagerAPI(context, authType);
        }
        Log.d(TAG, "[IFAAManagerFactory] GetIFAAManager Success!");
        return mIFAAManager;
    }
}
