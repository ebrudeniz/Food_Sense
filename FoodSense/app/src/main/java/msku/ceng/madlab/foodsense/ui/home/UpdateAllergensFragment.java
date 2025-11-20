package msku.ceng.madlab.foodsense.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import msku.ceng.madlab.foodsense.R;
import msku.ceng.madlab.foodsense.database.AppDatabase;
import msku.ceng.madlab.foodsense.databinding.FragmentUpdateAllergensBinding;
import msku.ceng.madlab.foodsense.models.User;

public class UpdateAllergensFragment extends Fragment {

    private FragmentUpdateAllergensBinding binding;
    private AppDatabase database;
    private List<String> selectedAllergens = new ArrayList<>();
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUpdateAllergensBinding.inflate(inflater, container, false);
        database = AppDatabase.getInstance(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current user ID
        SharedPreferences prefs = requireContext().getSharedPreferences("FoodSensePrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        loadCurrentAllergens();
        setupCheckboxListeners();

        // Back button
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private void loadCurrentAllergens() {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = database.userDao().getUserById(userId);

            requireActivity().runOnUiThread(() -> {
                if (user != null && user.getRestrictedIngredients() != null) {
                    String[] allergens = user.getRestrictedIngredients().split(",");

                    for (String allergen : allergens) {
                        allergen = allergen.trim();
                        selectedAllergens.add(allergen);

                        // Check corresponding checkboxes
                        if (allergen.equalsIgnoreCase("Gluten")) binding.cbGluten.setChecked(true);
                        else if (allergen.equalsIgnoreCase("Seafood")) binding.cbSeafood.setChecked(true);
                        else if (allergen.equalsIgnoreCase("Peanut")) binding.cbPeanut.setChecked(true);
                        else if (allergen.equalsIgnoreCase("Lactose")) binding.cbLactose.setChecked(true);
                        else if (allergen.equalsIgnoreCase("Egg")) binding.cbEgg.setChecked(true);
                        else if (allergen.equalsIgnoreCase("Soy")) binding.cbSoy.setChecked(true);
                        else if (allergen.equalsIgnoreCase("Trans Fats")) binding.cbTransFats.setChecked(true);
                        else if (allergen.equalsIgnoreCase("Sugar")) binding.cbSugar.setChecked(true);
                    }
                }
            });
        });
    }

    private void setupCheckboxListeners() {
        CheckBox[] checkboxes = {
                binding.cbGluten,
                binding.cbSeafood,
                binding.cbPeanut,
                binding.cbLactose,
                binding.cbEgg,
                binding.cbSoy,
                binding.cbTransFats,
                binding.cbSugar
        };

        String[] allergenNames = {
                "Gluten", "Seafood", "Peanut", "Lactose", "Egg", "Soy", "Trans Fats", "Sugar"
        };

        for (int i = 0; i < checkboxes.length; i++) {
            int index = i;
            checkboxes[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedAllergens.contains(allergenNames[index])) {
                        selectedAllergens.add(allergenNames[index]);
                    }
                } else {
                    selectedAllergens.remove(allergenNames[index]);
                }

                // Auto-save on change
                updateAllergens();
            });
        }
    }

    private void updateAllergens() {
        String allergensJson = String.join(",", selectedAllergens);

        Executors.newSingleThreadExecutor().execute(() -> {
            database.userDao().updateRestrictedIngredients(userId, allergensJson);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Allergens updated!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}