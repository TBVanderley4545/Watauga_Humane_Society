package com.tbvanderleystudios.wataugahumanesociety;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ValidationChecker vCheck = new ValidationChecker(this);

        if(vCheck.isNetworkAvailable()) {

            if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
                new GetAnimalsTask().execute();
            }

        } else {
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }

        TextView whsNameTextView = (TextView) findViewById(R.id.whsNameTextView);
        Typeface fishFingersFont = Typeface.createFromAsset(getAssets(), "Fishfingers.ttf");
        whsNameTextView.setTypeface(fishFingersFont);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (vCheck.isNetworkAvailable()) {
                    new GetAnimalsTask().execute();
                } else {
                    Toast.makeText(MainActivity.this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
                    if(mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }
    */

    private class GetAnimalsTask extends AsyncTask<Void, Void, Void> {

        private String mURLAddress = "http://wataugahumane.org/animals";
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            lockScreenOrientation();

            mProgressDialog.setTitle("Looking For Animals Now!");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            if(mSwipeRefreshLayout == null) {
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
