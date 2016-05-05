package com.tbvanderleystudios.wataugahumanesociety;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimalDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ANIMAL_POSITION_NO = "animalPositionNumber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

        Animal animal = (Animal) getIntent().getExtras().get(EXTRA_ANIMAL_POSITION_NO);

        ImageView animalDetailPic = (ImageView) findViewById(R.id.animalDetailPic);
        animalDetailPic.setImageBitmap(animal.getBitmapImage());
        animalDetailPic.setContentDescription(animal.getName());

        TextView animalDetailName = (TextView) findViewById(R.id.animalDetailName);
        animalDetailName.setText(animal.getName().toUpperCase());

        TextView animalDetailStatus = (TextView) findViewById(R.id.animalDetailStatus);
        animalDetailStatus.setText(animal.getStatus());

        TextView animalDetailGender = (TextView) findViewById(R.id.animalDetailGender);
        animalDetailGender.setText(animal.getGender());

        TextView animalDetailHoustrained = (TextView) findViewById(R.id.animalDetailHousetrained);

        TextView animalDetailDeclawed = (TextView) findViewById(R.id.animalDetailDeclawed);

        TextView animalDetailAge = (TextView) findViewById(R.id.animalDetailAge);
        animalDetailAge.setText(animal.getAge());

        TextView animalDetailBreed = (TextView) findViewById(R.id.animalDetailBreed);
        animalDetailBreed.setText(animal.getBreed());


        Typeface fishFingersFont = Typeface.createFromAsset(getAssets(), "Fishfingers.ttf");
        if(animalDetailName != null) {
            animalDetailName.setTypeface(fishFingersFont);
        }
    }
}
