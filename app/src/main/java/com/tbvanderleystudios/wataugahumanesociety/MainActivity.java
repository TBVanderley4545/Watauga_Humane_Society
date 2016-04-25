package com.tbvanderleystudios.wataugahumanesociety;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
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

                // This is done to get the number of animals so that arrays can be sized properly
                Elements nameSelector = document.select(".animal-listing__title > a");
                mAnimalCount = nameSelector.size();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mProgressDialog.dismiss();
            Toast.makeText(MainActivity.this, Integer.toString(mAnimalCount), Toast.LENGTH_LONG).show();
        }
    }
}
