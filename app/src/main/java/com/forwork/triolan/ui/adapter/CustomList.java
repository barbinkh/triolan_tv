package com.forwork.triolan.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
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

public class CustomList extends ArrayAdapter<CustomData> implements Filterable {
    public static ArrayList<CustomData> objects;
    public static String streamUrl;
    private final Activity context;
    public ArrayList<CustomData> original;
    private ArrayList<CustomData> objects_filter;
    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();
            ArrayList<CustomData> tempList = new ArrayList<CustomData>();
            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if (constraint != null && objects_filter != null) {
                int length = objects_filter.size();
                int i = 0;
                while (i < length) {
                    CustomData item = objects_filter.get(i);
                    //do whatever you wanna do here
                    //adding result                    set output array
                    if (item.getWeb().toLowerCase().contains(constraint))
                        tempList.add(item);

                    i++;
                }
                //following two lines is very important
                //as publish result can only take FilterResults objects
                filterResults.values = tempList;
                filterResults.count = tempList.size();
            } else {
                synchronized (context) {
                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {

            objects = (ArrayList<CustomData>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };
    private ImageLoader imageLoader;

    public CustomList(Activity context, ArrayList<CustomData> objects) {
        super(context, R.layout.data_view, objects);
        this.context = context;
        this.objects_filter.clear();
        this.objects.clear();

        this.objects.addAll(objects);
        this.objects_filter.addAll(objects);
    }

    public void initImageLoader() {

        File cacheDir = StorageUtils.getCacheDirectory(context, Boolean.parseBoolean("UniversalImageLoader/Cache"));

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCache(new UnlimitedDiscCache(cacheDir))
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        //  .writeDebugLogs ()
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
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        try {
            if (objects.get(position).getWeb().length() > 10) {
                txtTitle.setText(position + 1 + ": " + objects.get(position).getWeb().substring(0, 7) + "...");
            } else {
                txtTitle.setText(position + 1 + ": " + objects.get(position).getWeb());
            }                                                                   //+ ". " + objects.get(position).getWeb()
            streamUrl = objects.get(position).getStream();
            imageLoader.displayImage(objects.get(position).getPicture(), imageView, options);
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
            rowView.setVisibility(View.GONE);
        }

        return rowView;
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }


}
