package com.tbvanderleystudios.wataugahumanesociety;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import layout.AnimalRecyclerFragment;

public class MainActivity extends Activity {

    private Animal[] mAnimals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new AnimalRecyclerFragment());
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        new GetAnimalsTask().execute();
    }

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setTitle("Looking For Animals Now!");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
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

                // All arrays are having data parsed in
                ParseData(nameSelector, mNamesArray);
                ParseBitmapImages(bitmapImageSelector);
                ParseData(statusSelector, mStatusArray);
                ParseData(genderSelector, mGenderArray);
                ParseData(ageSelector, mAgeArray);
                ParseData(breedSelector, mBreedArray);

                // Pass all elements into the Animal[] called mAnimals.
                for (int i = 0; i < mAnimalCount; i++) {
                    mAnimals[i] = new Animal(mNamesArray[i],
                            mBitmapImageArray[i],
                            mStatusArray[i],
                            mGenderArray[i],
                            mAgeArray[i],
                            mBreedArray[i]);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mProgressDialog.dismiss();
            Toast.makeText(MainActivity.this, mAnimals[0].getName(), Toast.LENGTH_LONG).show();
        }
    }
}
