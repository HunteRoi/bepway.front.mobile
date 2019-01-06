package com.henallux.bepway.features.map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.henallux.bepway.R;
import com.henallux.bepway.features.activities.OSMActivity;
import com.henallux.bepway.model.Company;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyOwnItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {
    protected Context mContext;
    private ArrayList<Company> companies;
    private OSMActivity osmActivity;

    public MyOwnItemizedOverlay(final Context context, final List<OverlayItem> aList, OnItemGestureListener<OverlayItem> listener, ArrayList<Company> companies, OSMActivity osmActivity) {
        super(context, aList, listener);
        mContext = context;
        this.companies = companies;
        this.osmActivity = osmActivity;
    }

    @Override
    protected boolean onSingleTapUpHelper(final int index, final OverlayItem item, final MapView mapView) {
        final Dialog dialog = new Dialog(osmActivity);
        final Company company = companies.get(index);
        if(company.isPremium()){
            dialog.setContentView(R.layout.premium_company_popup);
            TextView description = dialog.findViewById(R.id.companyDescription);
            ImageView image = dialog.findViewById(R.id.companyImage);
            ImageView website = dialog.findViewById(R.id.companyWebsite);
            description.setText(company.getDescription() == null? "" : company.getDescription());
            if(company.getImageUrl() != null){
                try{
                    URL url = new URL(company.getImageUrl());
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    image.setImageBitmap(bmp);
                }
                catch (Exception exception){
                    Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(company.getSiteUrl() != null){
                        dialog.dismiss();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(company.getSiteUrl()));
                        mContext.startActivity(i);
                    }
                    else{
                        Toast.makeText(mContext, mContext.getString(R.string.no_url_provided), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            dialog.setContentView(R.layout.company_popup);
        }

        TextView textClose = dialog.findViewById(R.id.close_popup_company);
        TextView companyName = dialog.findViewById(R.id.companyName);
        TextView mapTitle = dialog.findViewById(R.id.mapTitle);
        ImageView map = dialog.findViewById(R.id.companyMap);
        map.setImageResource(R.drawable.ic_go_to);
        companyName.setText(company.getName());
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mapTitle.setText(mContext.getString(R.string.go_to));
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               osmActivity.drawRouteAndRecenterMapView(item);
               dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        return true;
    }
}