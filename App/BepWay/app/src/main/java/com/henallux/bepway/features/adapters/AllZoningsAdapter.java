package com.henallux.bepway.features.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.henallux.bepway.R;
import com.henallux.bepway.model.Zoning;

import java.util.ArrayList;

public class AllZoningsAdapter extends RecyclerView.Adapter<AllZoningsAdapter.MyViewHolder>{
    private ArrayList<Zoning> zonings;

    public void setZonings(ArrayList<Zoning> zonings){
        this.zonings = zonings;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView zoningName, zoningCity;
        public MyViewHolder(View v) {
            super(v);
            zoningName = v.findViewById(R.id.zoningNameId);
            zoningCity = v.findViewById(R.id.zoningCityId);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AllZoningsAdapter(ArrayList<Zoning> zonings){
        this.zonings = zonings;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AllZoningsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_zoning_layout,parent,false);
        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.zoningName.setText(zonings.get(position).getName());
        holder.zoningCity.setText(zonings.get(position).getCity());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return zonings.size();
    }
}
