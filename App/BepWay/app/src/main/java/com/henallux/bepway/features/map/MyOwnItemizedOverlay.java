package com.henallux.bepway.features.map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.henallux.bepway.R;
import com.henallux.bepway.features.activities.MainActivity;
import com.henallux.bepway.features.activities.OSMActivity;
import com.henallux.bepway.features.util.LoadImage;
import com.henallux.bepway.model.Company;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import java.util.ArrayList;
import java.util.List;

public class MyOwnItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {
    protected Context mContext;
    private ArrayList<Company> companies;
    private OSMActivity osmActivity;
    private Dialog dialog;
    private Company company;
    private ImageView image;

    public MyOwnItemizedOverlay(final Context context, final List<OverlayItem> aList, OnItemGestureListener<OverlayItem> listener, ArrayList<Company> companies, OSMActivity osmActivity) {
        super(context, aList, listener);
        mContext = context;
        this.companies = companies;
        this.osmActivity = osmActivity;
    }

    @Override
    public void removeAllItems() {
        companies.clear();
        super.removeAllItems();
    }


    public boolean addItem(OverlayItem item, Company company) {
        companies.add(company);
        return super.addItem(item);
    }

    @Override
    protected boolean onSingleTapUpHelper(final int index, final OverlayItem item, final MapView mapView) {
         dialog = new Dialog(osmActivity);
         company = companies.get(index);

        if(company.isPremium()){
            dialog.setContentView(R.layout.premium_company_popup);
            TextView description = dialog.findViewById(R.id.companyDescription);
            image = dialog.findViewById(R.id.companyImage);
            ImageView website = dialog.findViewById(R.id.companyWebsite);
            description.setText(company.getDescription() == null? "" : company.getDescription());
            if(company.getImageUrl() != null){
                LoadImage loadImageURL = new LoadImage(mContext,company.getImageUrl(), image);
                loadImageURL.execute();
            }else{
                image.setImageDrawable(mContext.getDrawable(R.drawable.company_example));
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
            image = dialog.findViewById(R.id.companyImage);
            image.setImageDrawable(mContext.getDrawable(R.drawable.company_example));
        }

        TextView textClose = dialog.findViewById(R.id.close_popup_company);
        TextView companyName = dialog.findViewById(R.id.companyName);
        TextView mapTitle = dialog.findViewById(R.id.mapTitle);
        ImageView map = dialog.findViewById(R.id.companyMap);
        companyName.setText(company.getName());
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (osmActivity.getDestination() != null && osmActivity.getDestination().equals(company)) {
            map.setImageResource(R.drawable.ic_check);
            mapTitle.setText(mContext.getString(R.string.arrived));
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(osmActivity, MainActivity.class);
                    osmActivity.startActivity(intent);
                    dialog.dismiss();
                }
            });
        }
        else{
            map.setImageResource(R.drawable.ic_go_to);
            mapTitle.setText(mContext.getString(R.string.go_to));
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    osmActivity.setDestination(company);
                    osmActivity.drawRouteAndRecenterMapView(item);
                    dialog.dismiss();
                }
            });
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        return true;
    }
}