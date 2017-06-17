package com.android.example.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by root on 6/16/17.
 */




public class ListWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}


