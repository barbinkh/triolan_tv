package com.forwork.triolan.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.forwork.triolan.R;
import com.forwork.triolan.main.TriolanAPI;
import com.forwork.triolan.ui.adapter.CustomListFavChannels;
import com.forwork.triolan.ui.adapter.PagerAdapter;
import com.forwork.triolan.ui.fragment.Channels;
import com.forwork.triolan.ui.fragment.FavoriteChannels;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

//import android.support.v7.widget.SearchView;


/**
 * The <code>TabsViewPagerFragmentActivity</code> class implements the Fragment activity that maintains a TabHost using a ViewPager.
 *
 * @author mwho
 */
public class MainActivity extends ActionBarActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    public CustomListFavChannels adapter;
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, MainActivity.TabInfo>();
    private PagerAdapter mPagerAdapter;
    private int countResume;
    private InterstitialAd interstitial;
    private AdRequest adRequest;
    private Handler mHandler;
    private Runnable displayAd;

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.d("MENU", "Cliced MenuItem is " + item.getTitle());
//        return super.onOptionsItemSelected(item);
//    }
//
//    public boolean onQueryTextChange(String text_new) {
//        Log.d("QUERY", "New text is " + text_new);
//        return true;
//    }
//
//    public boolean onQueryTextSubmit(String text) {
//        Log.d("QUERY", "Search text is " + text);
//        return true;
//    }

    private static void AddTab(MainActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);

        int pos = activity.mTabHost.getCurrentTab();
        tabHost.getTabWidget().getChildAt(pos).setBackgroundResource(R.drawable.tab_selector);

    }

    /**
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        // Inflate the layout
        setContentView(R.layout.main_activity);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        ((TriolanAPI) getApplication()).trackScreenView("Main menu");
        // Initialise the TabHost
        this.initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {

            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
        // Intialise ViewPager
        this.intialiseViewPager();
        countResume = 0;
        AdBuddiz.cacheAds(this);
        new Thread(() -> {

        });
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-1473557112924943/6024683312");
        adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("3D27A64B2700ABB4EFAEEB78AB757D94")
                .build();
        interstitial.loadAd(adRequest);

    }


    @Override
    protected void onResume() {
        super.onResume();
        countResume++;
        if (!interstitial.isLoaded() && !interstitial.isLoading()) {
            interstitial.loadAd(adRequest);
        }
        if (countResume > 5) {
            countResume = 0;
            if (interstitial.isLoaded()) {
                interstitial.show();
                //  displayInterstitial();
            }
        }
    }

    //        @Override
//        public boolean onCreateOptionsMenu(Menu menu) {
//            getMenuInflater().inflate(R.menu.main_activity_action, menu);
//
//            MenuItem searchItem = menu.findItem(R.id.action_search);
//            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//            searchView.setQueryHint("Поиск");
//            searchView.setOnQueryTextListener(this);
//            return true;
//        }

    /**
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

    /**
     * Initialise ViewPager
     */
    private void intialiseViewPager() {

        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, Channels.class.getName()));
        fragments.add(Fragment.instantiate(this, FavoriteChannels.class.getName()));

        this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        //
        this.mViewPager = (ViewPager) super.findViewById(R.id.viewpager);
        this.mViewPager.setAdapter(this.mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);
    }

    /**
     * Initialise the Tab Host
     */
    private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        MainActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Все каналы").setIndicator("Все каналы"), (tabInfo = new TabInfo("Все каналы", Channels.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        MainActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Избраннные каналы").setIndicator("Избраннные каналы"), (tabInfo = new TabInfo("Избраннные каналы", FavoriteChannels.class, args)));
        MainActivity.
                this.mapTabInfo.put(tabInfo.tag, tabInfo);

        // Default to first tab
        //this.onTabChanged("Tab1");
        //
        mTabHost.setOnTabChangedListener(this);
    }

    /**
     * (non-Javadoc)
     *
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    public void onTabChanged(String tag) {

        //TabInfo newTab = this.mapTabInfo.get(tag);
        try {
            int pos = this.mTabHost.getCurrentTab();
            mTabHost.getTabWidget().getChildAt(pos).setBackgroundResource(R.drawable.tab_selector);
            this.mViewPager.setCurrentItem(pos);
        } catch (Exception e) {
            Log.e("ERROR_TABS", e.toString());
        }

    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
     */
    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
     */
    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        this.mTabHost.setCurrentTab(position);
    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        // TODO Auto-generated method stub

    }

    public void moveToFireBounce(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.dad_team.firebounce2d")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.dad_team.firebounce2d")));
        }
    }

    public void removeFireBounce(View view) {
        findViewById(R.id.activity_main_fire_bounce_view).setVisibility(View.GONE);
//        AdView adView = (AdView) this.findViewById(R.id.adView);//Todo add banner
//
//        // Request for Ads
//        AdRequest adRequest = new AdRequest.Builder()
//                .build();
//
//        // Load ads into Banner Ads
//        adView.loadAd(adRequest);
//        adView.setVisibility(View.VISIBLE );
    }

    /**
     * @author mwho
     *         Maintains extrinsic info of a tab's construct
     */
    private class TabInfo {
        private String tag;
        private Class<?> clss;
        private Bundle args;
        private Fragment fragment;

        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }

    }

    /**
     * A simple factory that returns dummy views to the Tabhost
     *
     * @author mwho
     */
    class TabFactory implements TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }

        /**
         * (non-Javadoc)
         *
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }
}