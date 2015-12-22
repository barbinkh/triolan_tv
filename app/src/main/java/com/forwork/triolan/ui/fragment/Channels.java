package com.forwork.triolan.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.compdigitec.libvlcandroidsample.VideoActivity;
import com.forwork.triolan.R;
import com.forwork.triolan.helper.CheckConnection;
import com.forwork.triolan.model.CustomData;
import com.forwork.triolan.soap.GetCustomerPlaylistData;
import com.forwork.triolan.ui.MainActivity;
import com.forwork.triolan.ui.adapter.CustomList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.triolan.android.api.ApiCore;
import com.triolan.android.api.Backend;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Channels extends Fragment {
    private static final String TAG = "MainActivity";
    private static final String DATA_CHANNELS = "DataChannels";
    private static final String FAVORITE_CHANNELS = "FavoriteChannels";
    public GetCustomerPlaylistData getCustomerPlaylistDataList;
    public ProgressDialog progressBar;
    //  public static ArrayList<CustomData> objects = new ArrayList<CustomData>();
    public ArrayList<CustomData> favoriteChannels = new ArrayList<CustomData>();
    ImageLoader imageLoader;
    View rootView;
    CustomList adapter;
    final private SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

        public boolean onQueryTextChange(String text_new) {
            try {
                Log.d("QUERY", "New text is " + text_new);
                adapter.getFilter().filter(text_new);

            } catch (Exception e) {
                Log.d("ERROR", e.toString());
            }
            return true;
        }

        public boolean onQueryTextSubmit(String text) {
            Log.d("QUERY", "Search text is " + text);
            return true;
        }

    };
    private GridView grid;
    private SharedPreferences sPref;
    private ImageView imageConnectError;
    private TextView textConnectError;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.channels_all, container, false);

        ApiCore.DEBUG = true;
        Log.i("onCreate", "onCreate!!!!");


        grid = (GridView) rootView.findViewById(R.id.grid);

        imageConnectError = (ImageButton) rootView.findViewById(R.id.imageConnectError);

        textConnectError = (TextView) rootView.findViewById(R.id.textConnectError);

        setHasOptionsMenu(true);
        CheckInternet();  // проверка на наличие интернет соеденения


        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                intent.putExtra("LOCATION", CustomList.objects.get(position).getStream());
                Channels.this.startActivity(intent);
            }
        });


        grid.setLongClickable(true);

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                loadFavoriteChannels();
                clearFavoriteChannels();
                int id_pressed = (int) id;
                Log.d("FAVORITE_CHANNELS_SIZE_NULL", "  " + FavoriteChannels.favoriteChannels.size());
                if (FavoriteChannels.favoriteChannels.size() == 0) {

                    FavoriteChannels.favoriteChannels.add(CustomList.objects.get(id_pressed));
                    ((MainActivity) getActivity()).adapter.notifyDataSetChanged();
                    saveFavoriteChannels(FavoriteChannels.favoriteChannels);
                    Log.d("FAVORITE_CHANNELS_SIZE_all_ravnoO", "  " + FavoriteChannels.favoriteChannels.size());
                    Toast.makeText(getActivity(), "Канал добавлен в избранные", Toast.LENGTH_SHORT).show();

                } else {
                    boolean test_tv = false;

                    for (int i = 0; i < FavoriteChannels.favoriteChannels.size(); i++)
                        if (((CustomList.objects.get(id_pressed).getID()) == FavoriteChannels.favoriteChannels.get(i).getID())) {
                            Log.d("FAVORITE_CHANNELS_sravnenie", "  " + CustomList.objects.get(id_pressed).getID() + "-----" + FavoriteChannels.favoriteChannels.get(i).getID());
                            Toast.makeText(getActivity(), "Канал уже в избранных", Toast.LENGTH_SHORT).show();

                            test_tv = true;
                        }
                    if (!test_tv) {

                        FavoriteChannels.favoriteChannels.add(CustomList.objects.get(id_pressed));
                        ((MainActivity) getActivity()).adapter.notifyDataSetChanged();
                        saveFavoriteChannels(FavoriteChannels.favoriteChannels);
                        Log.d("FAVORITE_CHANNELS_SIZE_all", "  " + FavoriteChannels.favoriteChannels.size());
                        Toast.makeText(getActivity(), "Канал добавлен в избранные", Toast.LENGTH_SHORT).show();

                    }
                }
                return true;
            }
        });


        return rootView;

    }
//
//    private void setupCustomListsLandscape() {
//        // Инициализация адаптера для горизонтального листа
//        CustomList adapter = new CustomList(getActivity(), objects);
//        adapter.initImageLoader();
//        grid.setAdapter(adapter);
//
//    }

    //    private String getScreenOrientation() {
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//            setupCustomListsPortrait();
//        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//            setupCustomListsLandscape();
//
//        return "";
//    }
//
    private void setupGrid() {
        // Инициализация адаптера для горизонтального листа
        adapter = new CustomList(getActivity(), CustomList.objects);
        adapter.initImageLoader();
        grid.setAdapter(adapter);

    }

    private void ChannelsList() {

        progressBar = new ProgressDialog(getActivity());
        progressBar.setMessage("Загрузка каналов...");
        progressBar.show();

        Backend.get(ApiCore.getAlacarteAddress("GetCustomerPlaylists_Free_HTTP"))    // Отправка SOAP запроса на сервайс
                .onComplete(new Backend.BackendMethod.OnCompleteListener<GetCustomerPlaylistData>() {     // Получение JSON объекта в виде структуры класса GetCustomerPlaylistData
                    @Override
                    public void onComplete(
                            GetCustomerPlaylistData getCustomerPlaylistData) {

                        CustomList.objects = new ArrayList<CustomData>();
                        GetCustomerPlaylistData.GetCustomerPlaylistDataD.ChannelInfo logotip = getCustomerPlaylistData.d.Channels
                                .get(0);

                        Log.d("ArrayList Size = ", ""
                                + getCustomerPlaylistData.d.Channels.size());
                        Log.d("Channel Logo =", "" + logotip.Logo);


                        for (int i = 0; i < getCustomerPlaylistData.d.Channels       // Добавление имени, логотипа и ссылки на видео поток для дальнейшего использования
                                .size(); i++) {
                            // при создании адаптера и слайдера отображения каналов
                            CustomList.objects.add(new CustomData(
                                    getCustomerPlaylistData.d.Channels.get(i).ID,
                                    getCustomerPlaylistData.d.Channels.get(i).Name,
                                    getCustomerPlaylistData.d.Channels.get(i).Logo,
                                    getCustomerPlaylistData.d.Channels.get(i).Stream.replace("80.73.2.3:8888", "109.86.150.252:8888")));

                        }
                        CustomList.streamUrl = CustomList.objects.get(0).getStream();

                        //getScreenOrientation();
                        setupGrid();
                        progressBar.dismiss();
                        textConnectError.setVisibility(View.INVISIBLE);
                        imageConnectError.setVisibility(View.INVISIBLE);
                        clearDataChannels();
                        saveDataChannels(CustomList.objects);

                    }


                }).initLoader(this);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            getActivity().getMenuInflater().inflate(R.menu.main_activity_action, menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setQueryHint("Поиск");
            searchView.setOnQueryTextListener(queryListener);

        } catch (Exception e) {
            Log.d("ERROR_ROUND", e.toString());
        }

    }

//    public boolean onQueryTextChange(String text_new) {
//        Log.d("QUERY", "New text is " + text_new);
//        return true;
//    }
//
//    public boolean onQueryTextSubmit(String text) {
//        Log.d("QUERY", "Search text is " + text);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MENU", "Cliced MenuItem is " + item.getTitle());
        return super.onOptionsItemSelected(item);
    }

    private void CheckInternet() {
        CheckConnection inter = new CheckConnection();
        try {
            if (inter.CheckConnect()) {
                textConnectError.setVisibility(View.GONE);
                imageConnectError.setVisibility(View.GONE);
                Log.d(TAG, "Internet connection was find!");
                ChannelsList();
            } else {
                imageConnectError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChannelsList();
                    }
                });

            }

        } catch (Exception e) {
            Log.e("MainActivity", "error: Internet error");

        }
    }

    public void saveDataChannels(ArrayList<CustomData> objects) {
        sPref = getActivity().getApplication().getSharedPreferences(
                DATA_CHANNELS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(objects);
        ed.putString(DATA_CHANNELS, json);
        ed.commit();
    }


    public void clearDataChannels() {
        getActivity().getSharedPreferences(DATA_CHANNELS,
                Activity.MODE_PRIVATE).edit().clear().commit();
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

    private void loadFavoriteChannels() {
        sPref = getActivity().getApplication().getSharedPreferences(
                FAVORITE_CHANNELS, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sPref.getString(
                DATA_CHANNELS, null);
        if (json != null)

        {
            Type type = new TypeToken<ArrayList<CustomData>>() {
            }.getType();
            FavoriteChannels.favoriteChannels = gson.fromJson(json, type);
        }
    }

}

