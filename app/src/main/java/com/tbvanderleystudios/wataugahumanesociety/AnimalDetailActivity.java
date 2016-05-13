package com.tbvanderleystudios.wataugahumanesociety;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

public class AnimalDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ANIMAL_POSITION_NO = "animalPositionNumber";

    private String scrapedURLAddress;

    private ImageView mAnimalDetailPic;
    private TextView mAnimalDetailName;
    private TextView mAnimalDetailStatus;
    private TextView mAnimalDetailGender;
    private TextView mAnimalDetailHoustrained;
    private TextView mAnimalDetailDeclawed;
    private TextView mAnimalDetailAge;
    private TextView mAnimalDetailBreed;
    private TextView mAnimalDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

        scrapedURLAddress = getIntent().getStringExtra(EXTRA_ANIMAL_POSITION_NO);

        mAnimalDetailPic = (ImageView) findViewById(R.id.animalDetailPic);
        mAnimalDetailName = (TextView) findViewById(R.id.animalDetailName);
        mAnimalDetailStatus = (TextView) findViewById(R.id.animalDetailStatus);
        mAnimalDetailGender = (TextView) findViewById(R.id.animalDetailGender);
        mAnimalDetailHoustrained = (TextView) findViewById(R.id.animalDetailHousetrained);
        mAnimalDetailDeclawed = (TextView) findViewById(R.id.animalDetailDeclawed);
        mAnimalDetailAge = (TextView) findViewById(R.id.animalDetailAge);
        mAnimalDetailBreed = (TextView) findViewById(R.id.animalDetailBreed);
        mAnimalDescription = (TextView) findViewById(R.id.animalDetailDescription);

        ValidationChecker vCheck = new ValidationChecker(this);
        if(vCheck.isNetworkAvailable()) {
            new GetAnimalDetailTask().execute();
        } else {
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }

    }


    private class GetAnimalDetailTask extends AsyncTask<Void, Void, Void> {

        private Animal animal;
        private ProgressDialog mProgressDialog = new ProgressDialog(AnimalDetailActivity.this);
        private String mName;
        private Bitmap mBitmapImage;
        private String mStatus;
        private String mGender;
        private String mHouseTrained;
        private String mDeclawed;
        private String mAge;
        private String mBreed;
        private String mDescription;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            lockScreenOrientation();

            mProgressDialog.setTitle("Gathering Information!");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                // Connect to the Humane Society Website
                Document document = Jsoup.connect(scrapedURLAddress).get();

                // Create all of the element selectors.
                Elements nameSelector = document.select(".animal-listing__title > a");
                Elements bitmapImageSelector = document.select("img:nth-child(1)");
                Elements statusSelector = document.select(".animal-listing__subtitle:nth-child(2)");
                Elements genderSelector = document.select(".animal-listing__subtitle:nth-child(3)");
                Elements housetrainedSelector = document.select(".animal-listing__subtitle:nth-child(4)");
                Elements declawedSelector = document.select(".animal-listing__subtitle:nth-child(5)");
                Elements ageSelector = document.select(".animal-listing__subtitle:nth-child(6)");
                Elements breedSelector = document.select(".animal-listing__breeds");
                Elements descriptionSelector = document.select(".animal-listing__text > p");

                mName = nameSelector.text();
                mStatus = statusSelector.text();
                mGender = genderSelector.text();
                mHouseTrained = housetrainedSelector.text();
                mDeclawed = declawedSelector.text();
                mAge = ageSelector.text();
                mBreed = breedSelector.text();
                mDescription = descriptionSelector.text();

                String imgSrc = bitmapImageSelector.attr("src");
                InputStream input = new java.net.URL(imgSrc).openStream();
                mBitmapImage = BitmapFactory.decodeStream(input);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            animal = new Animal(mName, mBitmapImage, mStatus, mGender,
                    mAge, mBreed, mHouseTrained, mDeclawed, mDescription);

            mAnimalDetailPic.setImageBitmap(animal.getBitmapImage());
            if(animal.getName() != null) {
                mAnimalDetailName.setText(animal.getName().toUpperCase());
            } else {
                mAnimalDetailName.setText("Oops, an error occurred.");
            }
            mAnimalDetailStatus.setText(animal.getStatus());
            mAnimalDetailGender.setText(animal.getGender());
            mAnimalDetailHoustrained.setText(animal.getHousetrained());
            mAnimalDetailDeclawed.setText(animal.getDeclawed());
            mAnimalDetailAge.setText(animal.getAge());
            mAnimalDetailBreed.setText(animal.getBreed());
            mAnimalDescription.setText(animal.getDescription());

            Typeface fishFingersFont = Typeface.createFromAsset(getAssets(), "Fishfingers.ttf");
            if(mAnimalDetailName != null) {
                mAnimalDetailName.setTypeface(fishFingersFont);
            }


            mProgressDialog.dismiss();

            unlockScreenOrientation();
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
