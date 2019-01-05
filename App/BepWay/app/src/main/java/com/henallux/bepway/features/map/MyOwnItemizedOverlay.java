package com.henallux.bepway.features.map;

import android.app.AlertDialog;
import android.content.Context;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

public class MyOwnItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {
    protected Context mContext;

    public MyOwnItemizedOverlay(final Context context, final List<OverlayItem> aList, OnItemGestureListener<OverlayItem> listener) {
        super(context, aList, listener);
        mContext = context;
    }

    @Override
    protected boolean onSingleTapUpHelper(final int index, final OverlayItem item, final MapView mapView) {
        //Toast.makeText(mContext, "Item " + index + " has been tapped!", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(item.getTitle());
        dialog.setMessage(item.getSnippet());
        dialog.show();
        return true;
    }
}