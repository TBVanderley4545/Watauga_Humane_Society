package com.tbvanderleystudios.wataugahumanesociety;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import layout.AnimalRecyclerFragment;

public class MainActivity extends Activity {

    public static final String ANIMALS = "animals";
    private Animal[] mAnimals;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mNavigationDrawer;
    private ArrayAdapter<String> mNavDrawerAdapter;
    private String[] mDrawerItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private GetAnimalsTask mTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNavigationDrawer();

        // Check if network is available.
        final ValidationChecker vCheck = new ValidationChecker(this);

        if(vCheck.isNetworkAvailable()) {

            if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
                mTask = new GetAnimalsTask("all animals", 1);
                mTask.execute();
            }

        } else {
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }

        // Set swipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (vCheck.isNetworkAvailable()) {
                    mTask = new GetAnimalsTask("all animals", 0);
                    mTask.execute();
                } else {
                    Toast.makeText(MainActivity.this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
                    if(mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void createNavigationDrawer() {
        mNavigationDrawer = (ListView) findViewById(R.id.navigationDrawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        addDrawerItems();

        mNavigationDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = mDrawerItems[position];
                mTask = new GetAnimalsTask(selectedItem, 1);
                mTask.execute();

                mDrawerLayout.closeDrawer(mNavigationDrawer);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return false;
        }
    }

    private void addDrawerItems() {
        mDrawerItems = getResources().getStringArray(R.array.drawer_items);
        mNavDrawerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDrawerItems);
        mNavigationDrawer.setAdapter(mNavDrawerAdapter);
    }



    private class GetAnimalsTask extends AsyncTask<Void, Void, Void> {

        private String mURLAddress;
        private String mSelectedItem;
        private int mShowDialogFlag;
        private int mAnimalCount;
        private Bitmap bitmap;
        private ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
        private String[] mNamesArray;
        private Bitmap[] mBitmapImageArray;
        private String[] mStatusArray;
        private String[] mGenderArray;
        private String[] mAgeArray;
        private String[] mBreedArray;
        private String[] mScrapedAddressArray;

        public GetAnimalsTask(String selectedItem, int showDialogFlag) {
            mSelectedItem = selectedItem;
            mShowDialogFlag = showDialogFlag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            lockScreenOrientation();

            // Determine which item was selected and then pull from the proper URL.
            switch (mSelectedItem.toLowerCase()) {
                case "all animals":
                    mURLAddress = "http://wataugahumane.org/animals";
                    break;
                case "dogs":
                    mURLAddress = "http://wataugahumane.org/types/dogs";
                    break;
                case "cats":
                    mURLAddress = "http://wataugahumane.org/types/cats";
                    break;
                case "puppies":
                    mURLAddress = "http://wataugahumane.org/types/puppies";
                    break;
                case "kittens":
                    mURLAddress = "http://wataugahumane.org/types/kittens";
                    break;
                case "other animals":
                    mURLAddress = "http://wataugahumane.org/types/other-animals";
                    break;
                default:
                    mURLAddress = "http://wataugahumane.org/animals";
                    break;
            }

            mProgressDialog.setTitle("Looking For Animals Now!");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);

            // Prevent cancelling the AsyncTask with the back key.
            mProgressDialog.setCancelable(false);

            // Don't show ProgressDialog when refreshing as indicated by the flag.
            if(mShowDialogFlag == 1) {
                mProgressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                // Connect to the Humane Society Website
                Document document = Jsoup.connect(mURLAddress).get();

                // Create all of the element selectors.
                Elements nameSelector = document.select(".animal-listing__title > a");
                Elements bitmapImageSelector = document.select(".animal-listing__image > img");
                Elements statusSelector = document.select(".animal-listing__text > h5:nth-child(2)");
                Elements genderSelector = document.select(".animal-listing__text > h5:nth-child(3)");
                Elements ageSelector = document.select(".animal-listing__text > h5:nth-child(4)");
                Elements breedSelector = document.select(".animal-listing__text > h5:nth-child(5)");

                // This is done to get the number of animals so that arrays can be sized properly
                mAnimalCount = nameSelector.size();
                mNamesArray = new String[mAnimalCount];
                mBitmapImageArray = new Bitmap[mAnimalCount];
                mStatusArray = new String[mAnimalCount];
                mGenderArray = new String[mAnimalCount];
                mAgeArray = new String[mAnimalCount];
                mBreedArray = new String[mAnimalCount];
                mAnimals = new Animal[mAnimalCount];
                mScrapedAddressArray = new String[mAnimalCount];

                // All arrays are having data parsed in
                ParseData(nameSelector, mNamesArray);
                ParseBitmapImages(bitmapImageSelector);
                ParseData(statusSelector, mStatusArray);
                ParseData(genderSelector, mGenderArray);
                ParseData(ageSelector, mAgeArray);
                ParseData(breedSelector, mBreedArray);
                ParseURLAddress(nameSelector);

                // Pass all elements into the Animal[] called mAnimals.
                for (int i = 0; i < mAnimalCount; i++) {
                    mAnimals[i] = new Animal(mNamesArray[i],
                            mBitmapImageArray[i],
                            mStatusArray[i],
                            mGenderArray[i],
                            mAgeArray[i],
                            mBreedArray[i],
                            mScrapedAddressArray[i]);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();

            if(mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            mProgressDialog.dismiss();

            unlockScreenOrientation();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Bundle bundle = new Bundle();
            bundle.putParcelableArray(ANIMALS, mAnimals);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment animalRecyclerFragment = AnimalRecyclerFragment.newInstance(mAnimals);
            ft.replace(R.id.content_frame, animalRecyclerFragment, "animal_fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            if(mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            mProgressDialog.dismiss();

            unlockScreenOrientation();
        }

        private void ParseBitmapImages(Elements bitmapImageSelector) throws IOException {
            for(int i = 0; i < bitmapImageSelector.size(); i++) {
                String imgSrc = bitmapImageSelector.get(i).attr("src");
                // Use Picasso to pull the bitmap images.
                bitmap = Picasso.with(MainActivity.this).load(imgSrc).get();
                // Add the new bitmap image to the array
                mBitmapImageArray[i] = bitmap;
            }
        }

        private void ParseData(Elements selector, String[] array) {
            for (int i = 0; i < selector.size(); i++) {
                array[i] = selector.get(i).text();
            }
        }

        private void ParseURLAddress(Elements nameSelector) {
            for(int i = 0; i < nameSelector.size(); i++) {
                mScrapedAddressArray[i] = nameSelector.get(i).absUrl("href");
            }
        }

        private void lockScreenOrientation() {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        private void unlockScreenOrientation() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
}
