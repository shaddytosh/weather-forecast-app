package com.craftsilicon.weather.ui.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.craftsilicon.weather.app.navigation.BaseFragment;
import com.craftsilicon.weather.databinding.FragmentMainBinding;
import com.craftsilicon.weather.ui.home.fragments.HomeFragment;

public class MainFragment extends BaseFragment {

    private FragmentMainBinding binding;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false);

        binding.viewpager.setUserInputEnabled(false);
        MainFragmentAdapter mainFragmentAdapter= new MainFragmentAdapter(this);
        binding.viewpager.setAdapter(mainFragmentAdapter);

        binding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                requireActivity().invalidateOptionsMenu();
            }
        });


        return  binding.getRoot();
    }



    private static class MainFragmentAdapter extends FragmentStateAdapter {

        public MainFragmentAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return HomeFragment.newInstance();
                default:
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

}