package com.henallux.bepway.features.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.henallux.bepway.R;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class LoadImage extends AsyncTask<Void,Void,Bitmap> {
    private String URL;
    private ImageView image;
    private Context context;
    public LoadImage(Context context, String URL, ImageView image){
        super();
        this.context = context;
        this.URL = URL;
        this.image = image;
    }
    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = null;
        try{
            InputStream in = new java.net.URL(URL).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        }
        catch (MalformedURLException exception){

        }
        catch (IOException exception){
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.company_example);

        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}

