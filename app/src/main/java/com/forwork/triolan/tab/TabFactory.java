package com.forwork.triolan.tab;

import android.content.Context;
import android.view.View;

/**
 * Created by barbinkh on 16.12.2014.
 */
public class TabFactory {
    private final Context mContext;

    /**
     * @param context
     */
    public TabFactory(Context context) {
        mContext = context;
    }

    /** (non-Javadoc)
     * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
     */
    public View createTabContent(String tag) {
        View v = new View(mContext);
        v.setMinimumWidth(0);
        v.setMinimumHeight(0);
        return v;
    }
}
