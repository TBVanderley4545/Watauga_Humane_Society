package com.tbvanderleystudios.wataugahumanesociety;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder>{

    private Animal[] mAnimals;
    private Listener mListener;

    public interface Listener {
        void onClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView view) {
            super(view);
            cardView=view;
        }
    }

    public AnimalAdapter(Animal[] animals) {
        mAnimals = animals;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }


    @Override
    public AnimalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_list_card, parent, false);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(AnimalAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        ImageView animalImageView = (ImageView) cardView.findViewById(R.id.animalImageView);
        animalImageView.setImageBitmap(mAnimals[position].getBitmapImage());
        animalImageView.setContentDescription(mAnimals[position].getName());

        TextView animalNameTextView = (TextView) cardView.findViewById(R.id.animalNameTextView);
        animalNameTextView.setText(mAnimals[position].getName());

        TextView animalBreedTextView = (TextView) cardView.findViewById(R.id.animalBreedTextView);
        animalBreedTextView.setText(mAnimals[position].getBreed());

        TextView animalStatusTextView = (TextView) cardView.findViewById(R.id.animalStatusTextView);
        animalStatusTextView.setText(mAnimals[position].getStatus());

        TextView animalGenderTextView = (TextView) cardView.findViewById(R.id.animalGenderTextView);
        animalGenderTextView.setText(mAnimals[position].getGender());

        TextView animalAgeTextView = (TextView) cardView.findViewById(R.id.animalAgeTextView);
        animalAgeTextView.setText(mAnimals[position].getAge());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAnimals.length;
    }
}
