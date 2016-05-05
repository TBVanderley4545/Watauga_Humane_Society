package layout;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbvanderleystudios.wataugahumanesociety.Animal;
import com.tbvanderleystudios.wataugahumanesociety.AnimalAdapter;
import com.tbvanderleystudios.wataugahumanesociety.AnimalDetailActivity;
import com.tbvanderleystudios.wataugahumanesociety.MainActivity;
import com.tbvanderleystudios.wataugahumanesociety.R;

public class AnimalRecyclerFragment extends Fragment {

    private Animal[] mAnimals;

    public AnimalRecyclerFragment() {
        // Required empty public constructor
    }

    public static AnimalRecyclerFragment newInstance(Parcelable[] animals) {
        AnimalRecyclerFragment fragment = new AnimalRecyclerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(MainActivity.ANIMALS, animals);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView animalRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_animal_recycler,
                container, false);

        mAnimals = (Animal[]) getArguments().getParcelableArray(MainActivity.ANIMALS);

        AnimalAdapter adapter = new AnimalAdapter(mAnimals);
        animalRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        animalRecycler.setLayoutManager(layoutManager);

        adapter.setListener(new AnimalAdapter.Listener(){
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), AnimalDetailActivity.class);
                intent.putExtra(AnimalDetailActivity.EXTRA_ANIMAL_POSITION_NO, mAnimals[position]);
                getActivity().startActivity(intent);
            }
        });


        setRetainInstance(true);

        // Inflate the layout for this fragment
        return animalRecycler;
    }
}
