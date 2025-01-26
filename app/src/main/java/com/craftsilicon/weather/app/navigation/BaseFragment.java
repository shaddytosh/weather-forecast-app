package com.craftsilicon.weather.app.navigation;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.craftsilicon.weather.R;


public abstract class BaseFragment extends Fragment {

    private NavController navController;

    protected final void setToolbar(Toolbar toolbar) {
        NavigationUI.setupWithNavController(toolbar, getNavController());
    }

    protected final void navigate(NavDirections navDirections) {
        getNavController().navigate(navDirections);
    }

    protected final void navigateUp() {
        getNavController().navigateUp();
    }

    protected final void navigateBack() {
        getNavController().popBackStack();
    }

    protected final NavController getNavController() {
        if (navController == null) {
            navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        }
        return navController;
    }

}
