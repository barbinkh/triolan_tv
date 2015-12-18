package com.forwork.triolan.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.compdigitec.libvlcandroidsample.VideoActivity;
import com.forwork.triolan.R;
import com.forwork.triolan.model.CustomData;
import com.forwork.triolan.ui.MainActivity;
import com.forwork.triolan.ui.adapter.CustomListFavChannels;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class FavoriteChannels extends Fragment {
    private static final String FAVORITE_CHANNELS = "FavoriteChannels";
    public static ArrayList<CustomData> favoriteChannels = new ArrayList<CustomData>();
    private View rootView;
    private GridView gridFavoriteChannels;
    private SharedPreferences sPref;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.favorite_channels, container, false);

        loadFavoriteChannels();
        Log.d("FavoriteChannels", "size" + favoriteChannels.size());
        gridFavoriteChannels = (GridView) rootView.findViewById(R.id.grid_favorite);

        ((MainActivity) getActivity()).adapter = new CustomListFavChannels(getActivity(), favoriteChannels);
        ((MainActivity) getActivity()).adapter.initImageLoader();
        gridFavoriteChannels.setAdapter(((MainActivity) getActivity()).adapter);

        gridFavoriteChannels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                intent.putExtra("LOCATION", favoriteChannels.get(position).getStream());
                FavoriteChannels.this.startActivity(intent);
            }
        });

        gridFavoriteChannels.setLongClickable(true);

        gridFavoriteChannels.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                clearFavoriteChannels();
                int id_pressed = (int) id;
                favoriteChannels.remove(id_pressed);
                saveFavoriteChannels(favoriteChannels);

                // adapter.updateList(favoriteChannels);
                ((MainActivity) getActivity()).adapter.notifyDataSetChanged();

                Log.d("FAVORITE_CHANNELS_SIZE_fav", "  " + favoriteChannels.size());
                Toast.makeText(getActivity(), "Канал удален из избранных", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        Log.d(FAVORITE_CHANNELS, "Fragment2 onActivityCreatedView");
        return rootView;

    }


    private void loadFavoriteChannels() {
        sPref = getActivity().getApplication().getSharedPreferences(
                FAVORITE_CHANNELS, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sPref.getString(
                FAVORITE_CHANNELS, null);
        if (json != null)

        {
            Type type = new TypeToken<ArrayList<CustomData>>() {
            }.getType();
            favoriteChannels = gson.fromJson(json, type);
        }


    }

    public void saveFavoriteChannels(ArrayList<CustomData> favoriteChannels) {
        sPref = getActivity().getApplication().getSharedPreferences(
                FAVORITE_CHANNELS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favoriteChannels);
        ed.putString(FAVORITE_CHANNELS, json);
        ed.commit();
    }

    public void clearFavoriteChannels() {
        getActivity().getSharedPreferences(FAVORITE_CHANNELS,
                Activity.MODE_PRIVATE).edit().clear().commit();
    }

}
