package com.tbvanderleystudios.wataugahumanesociety;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder>{

    private List<Animal> mAnimals;
    private Listener mListener;
    private final LayoutInflater mInflater;

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

    public AnimalAdapter(Context context, List<Animal> animals) {
        mInflater = LayoutInflater.from(context);
        mAnimals = new ArrayList<>(animals);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }


    @Override
    public AnimalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) mInflater.inflate(R.layout.animal_list_card, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(AnimalAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        ImageView animalImageView = (ImageView) cardView.findViewById(R.id.animalImageView);
        animalImageView.setImageBitmap(mAnimals.get(position).getBitmapImage());
        animalImageView.setContentDescription(mAnimals.get(position).getName());

        TextView animalNameTextView = (TextView) cardView.findViewById(R.id.animalNameTextView);
        animalNameTextView.setText(mAnimals.get(position).getName().toUpperCase());

        TextView animalBreedTextView = (TextView) cardView.findViewById(R.id.animalBreedTextView);
        animalBreedTextView.setText(mAnimals.get(position).getBreed());

        TextView animalStatusTextView = (TextView) cardView.findViewById(R.id.animalStatusTextView);
        animalStatusTextView.setText(mAnimals.get(position).getStatus());

        TextView animalGenderTextView = (TextView) cardView.findViewById(R.id.animalGenderTextView);
        animalGenderTextView.setText(mAnimals.get(position).getGender());

        TextView animalAgeTextView = (TextView) cardView.findViewById(R.id.animalAgeTextView);
        animalAgeTextView.setText(mAnimals.get(position).getAge());

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
        return mAnimals.size();
    }

    public void animateTo(List<Animal> animals) {
        applyAndAnimateRemovals(animals);
        applyAndAnimateAdditions(animals);
        applyAndAnimateMovedItems(animals);
    }

    private void applyAndAnimateRemovals(List<Animal> newAnimals) {
        for (int i = mAnimals.size() - 1; i >= 0; i--) {
            final Animal animal = mAnimals.get(i);
            if(!newAnimals.contains(animal)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Animal> newAnimals) {
        for (int i = 0; i < newAnimals.size(); i++) {
            final Animal animal = newAnimals.get(i);
            if(!mAnimals.contains(animal)) {
                addItem(i, animal);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Animal> newAnimals) {
        for (int toPosition = newAnimals.size() - 1; toPosition >= 0; toPosition--) {
            final Animal animal = newAnimals.get(toPosition);
            final int fromPosition = mAnimals.indexOf(animal);
            if(fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Animal removeItem(int position) {
        final Animal animal = mAnimals.remove(position);
        notifyItemRemoved(position);
        return animal;
    }

    public void addItem(int position, Animal animal) {
        mAnimals.add(position, animal);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Animal animal = mAnimals.remove(fromPosition);
        mAnimals.add(toPosition, animal);
        notifyItemMoved(fromPosition, toPosition);
    }
}
