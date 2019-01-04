package com.henallux.bepway.features.recyclerView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class PaginationScrollListener extends RecyclerView.OnScrollListener {
    private RecyclerView.LayoutManager  layoutManager;

    public PaginationScrollListener(RecyclerView.LayoutManager layoutManager){
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy){
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getItemCount();
        int totalItemsCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

        //if(!isLoading)
    }
}
