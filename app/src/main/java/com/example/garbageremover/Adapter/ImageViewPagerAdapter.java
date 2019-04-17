package com.example.garbageremover.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class ImageViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<Uri> UriImage ;
    public ImageViewPagerAdapter(Context context, List<Uri> UriImage){
        this.context = context;
        this.UriImage = UriImage;
    }

    @Override
    public int getCount() {
        return UriImage.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ImageView imageView = new ImageView(context);
        imageView.setImageURI(UriImage.get(position));
        container.addView(imageView,0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
