package msku.ceng.madlab.foodsense.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import msku.ceng.madlab.foodsense.R;
import msku.ceng.madlab.foodsense.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Big Scan Button
        binding.cardScan.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_home_to_scan);
        });

        // Bottom Navigation
        binding.btnHome.setOnClickListener(v -> {
            // Already on home, do nothing or refresh
        });

        binding.btnProfile.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_home_to_profile);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}