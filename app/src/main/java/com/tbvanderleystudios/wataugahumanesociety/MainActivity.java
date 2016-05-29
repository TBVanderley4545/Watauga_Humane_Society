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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNavigationDrawer();

        // Check if network is available.
        final ValidationChecker vCheck = new ValidationChecker(this);

        if(vCheck.isNetworkAvailable()) {

            if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
                new GetAnimalsTask("all animals").execute();
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
                    new GetAnimalsTask("all animals").execute();
                } else {
                    Toast.makeText(MainActivity.this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
                    if(mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    private void createNavigationDrawer() {
        mNavigationDrawer = (ListView) findViewById(R.id.navigationDrawer);
        addDrawerItems();
        mNavigationDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = mDrawerItems[position];
                new GetAnimalsTask(selectedItem).execute();
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(mNavigationDrawer);
            }
        });
    }

    private void addDrawerItems() {
        mDrawerItems = getResources().getStringArray(R.array.drawer_items);
        mNavDrawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDrawerItems);
        mNavigationDrawer.setAdapter(mNavDrawerAdapter);
    }


    private class GetAnimalsTask extends AsyncTask<Void, Void, Void> {

        private String mURLAddress;
        private String mSelectedItem;
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

        public GetAnimalsTask(String selectedItem) {
            mSelectedItem = selectedItem;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            lockScreenOrientation();

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
            mProgressDialog.setCancelable(false);
            if(mSwipeRefreshLayout == null || !mURLAddress.equalsIgnoreCase("http://wataugahumane.org/animals")) {
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
            int iterator = 0;
            for(Element element : bitmapImageSelector) {
                String imgSrc = element.attr("src");
                InputStream input = new java.net.URL(imgSrc).openStream();
                bitmap = BitmapFactory.decodeStream(input);
                mBitmapImageArray[iterator] = bitmap;
                iterator++;
            }
        }

        private void ParseData(Elements selector, String[] array) {
            int iterator = 0;
            for(Element element : selector) {
                array[iterator] = element.text();
                iterator++;
            }
        }

        private void ParseURLAddress(Elements nameSelector) {
            int iterator = 0;
            for (Element link : nameSelector) {
                mScrapedAddressArray[iterator] = link.absUrl("href");
                iterator++;
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
