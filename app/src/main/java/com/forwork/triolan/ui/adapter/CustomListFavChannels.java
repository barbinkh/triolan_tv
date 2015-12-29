package com.forwork.triolan.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.forwork.triolan.R;
import com.forwork.triolan.model.CustomData;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;

public class CustomListFavChannels extends ArrayAdapter<CustomData> {
    public static ArrayList<CustomData> favoriteChannels;
    public static String streamUrl;
    private final Activity context;
    private ImageView streamView;
    private ImageLoader imageLoader;


    public CustomListFavChannels(Activity context, ArrayList<CustomData> favoriteChannels) {
        super(context, R.layout.data_view, favoriteChannels);
        this.context = context;
        this.favoriteChannels = favoriteChannels;
    }

    public void initImageLoader() {

        File cacheDir = StorageUtils.getCacheDirectory(context, Boolean.parseBoolean("UniversalImageLoader/Cache"));

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCache(new UnlimitedDiscCache(cacheDir))
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        //       .writeDebugLogs ()
                .build();
        // Initialize ImageLoader with configuration.
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .cacheInMemory()
                .cacheOnDisc()
                .build();


        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.data_view, null, true);
//        String fontPath = "fonts/DidactGothic.ttf";
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
//        // Font Face
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
//        // Applying font
//        txtTitle.setTypeface(typeface);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        if (favoriteChannels.get(position).getWeb().length() > 10) {
            txtTitle.setText(position + 1 + ": " + favoriteChannels.get(position).getWeb().substring(0, 7) + "...");
        } else {
            txtTitle.setText(position + 1 + ": " + favoriteChannels.get(position).getWeb());
        }
        streamUrl = favoriteChannels.get(position).getStream();
        imageLoader.displayImage(favoriteChannels.get(position).getPicture(), imageView, options);
        return rowView;
    }

    public void updateList(ArrayList<CustomData> newlist) {
        favoriteChannels.clear();
        favoriteChannels.addAll(newlist);
        this.notifyDataSetChanged();
    }
}
