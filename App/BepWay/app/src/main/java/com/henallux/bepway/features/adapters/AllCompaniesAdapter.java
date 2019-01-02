package com.henallux.bepway.features.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.henallux.bepway.R;
import com.henallux.bepway.model.Company;

import java.util.ArrayList;

public class AllCompaniesAdapter extends RecyclerView.Adapter<AllCompaniesAdapter.MyViewHolder>   {
    private ArrayList<Company> companies;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView companyName, companyActivity, companyAddress;
        public ImageView premium;
        public MyViewHolder(View v) {
            super(v);
            companyName = v.findViewById(R.id.companyNameId);
            companyActivity = v.findViewById(R.id.companyActivityId);
            companyAddress = v.findViewById(R.id.companyAddressId);
            premium = v.findViewById(R.id.premiumLogo);
        }
    }

    public void setCompanies(ArrayList<Company> companies){
        this.companies = companies;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AllCompaniesAdapter(ArrayList<Company> companies){
        this.companies = companies;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AllCompaniesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_company_layout,parent,false);
        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(companies.get(position).isPremium()) holder.premium.setImageResource(R.drawable.ic_star);
        holder.companyName.setText(companies.get(position).getName());
        holder.companyActivity.setText(companies.get(position).getSector());
        holder.companyAddress.setText(companies.get(position).getAddress());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return companies.size();
    }
}
