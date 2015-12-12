package com.forwork.triolan.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by barbinkh on 16.12.2014.
 */
public class TabInfo {
    private String tag;
    private Class<?> clss;
    private Bundle args;
    private Fragment fragment;
    TabInfo(String tag, Class<?> clazz, Bundle args) {
        this.tag = tag;
        this.clss = clazz;
        this.args = args;
    }
}
