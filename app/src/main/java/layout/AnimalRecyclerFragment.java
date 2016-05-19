package layout;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.tbvanderleystudios.wataugahumanesociety.Animal;
import com.tbvanderleystudios.wataugahumanesociety.AnimalAdapter;
import com.tbvanderleystudios.wataugahumanesociety.AnimalDetailActivity;
import com.tbvanderleystudios.wataugahumanesociety.MainActivity;
import com.tbvanderleystudios.wataugahumanesociety.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnimalRecyclerFragment extends Fragment implements SearchView.OnQueryTextListener{

    private RecyclerView mAnimalRecycler;
    private List<Animal> mAnimalList;
    private AnimalAdapter mAnimalAdapter;
    private List<Animal> mFilteredAnimalList;

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
        setHasOptionsMenu(true);

        mAnimalRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_animal_recycler,
                container, false);

        Animal[] animals = (Animal[]) getArguments().getParcelableArray(MainActivity.ANIMALS);
        mAnimalList = Arrays.asList(animals);

        mAnimalAdapter = new AnimalAdapter(getActivity(), mAnimalList);
        mAnimalRecycler.setAdapter(mAnimalAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mAnimalRecycler.setLayoutManager(layoutManager);

        mAnimalAdapter.setListener(new AnimalAdapter.Listener(){
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), AnimalDetailActivity.class);
                if(mFilteredAnimalList == null) {
                    intent.putExtra(AnimalDetailActivity.EXTRA_ANIMAL_POSITION_NO, mAnimalList.get(position).getScrapedURLAddress());
                } else {
                    intent.putExtra(AnimalDetailActivity.EXTRA_ANIMAL_POSITION_NO, mFilteredAnimalList.get(position).getScrapedURLAddress());
                }
                getActivity().startActivity(intent);
            }
        });


        setRetainInstance(true);

        // Inflate the layout for this fragment
        return mAnimalRecycler;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);

        final MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Animal> filteredAnimalList = filter(mAnimalList, newText);
        mAnimalAdapter.animateTo(filteredAnimalList);
        mAnimalRecycler.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Animal> filter(List<Animal> animals, String query) {
        query = query.toLowerCase();

        mFilteredAnimalList = new ArrayList<>();
        for(Animal animal : animals) {
            final String name = animal.getName().toLowerCase();
            if(name.contains(query)) {
                mFilteredAnimalList.add(animal);
            }
        }
        return mFilteredAnimalList;
    }
}
