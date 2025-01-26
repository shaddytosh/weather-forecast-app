package com.craftsilicon.weather.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.craftsilicon.weather.app.navigation.BaseFragment;
import com.craftsilicon.weather.databinding.FragmentLauncherBinding;

public class LauncherFragment extends BaseFragment {

    private FragmentLauncherBinding launcherBinding;

    public LauncherFragment() {
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
        launcherBinding = FragmentLauncherBinding.inflate(inflater, container, false);


        return launcherBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        launcherBinding.getRoot().postDelayed(() -> navigate(LauncherFragmentDirections.actionLauncherToMain()), 2000);

    }

}