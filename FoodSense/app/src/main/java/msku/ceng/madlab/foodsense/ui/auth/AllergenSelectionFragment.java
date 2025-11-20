package msku.ceng.madlab.foodsense.ui.auth;

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
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import msku.ceng.madlab.foodsense.R;
import msku.ceng.madlab.foodsense.database.AppDatabase;
import msku.ceng.madlab.foodsense.databinding.FragmentAllergenSelectionBinding;
import msku.ceng.madlab.foodsense.models.User;

public class AllergenSelectionFragment extends Fragment {

    private FragmentAllergenSelectionBinding binding;
    private AppDatabase database;
    private List<String> selectedAllergens = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllergenSelectionBinding.inflate(inflater, container, false);
        database = AppDatabase.getInstance(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCheckboxListeners();

        // Info button - modal aç
        binding.btnInfo.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_allergen_to_info);
        });

        // Back button
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Sign Up button
        binding.btnSignUp.setOnClickListener(v -> saveUserData());
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
                    selectedAllergens.add(allergenNames[index]);
                } else {
                    selectedAllergens.remove(allergenNames[index]);
                }
            });
        }
    }

    private void saveUserData() {
        if (selectedAllergens.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one allergen", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(requireContext(), "Error: User data not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = args.getString("name");
        String surname = args.getString("surname");
        String email = args.getString("email");
        String password = args.getString("password");
        String birthDate = args.getString("birthDate", "");
        String gender = args.getString("gender", "");

        String allergensJson = String.join(",", selectedAllergens);

        User newUser = new User(name, surname, email, password, birthDate, gender, allergensJson);

        Executors.newSingleThreadExecutor().execute(() -> {
            // Check if email already exists
            User existingUser = database.userDao().getUserByEmail(email);

            requireActivity().runOnUiThread(() -> {
                if (existingUser != null) {
                    Toast.makeText(requireContext(), "Email already registered", Toast.LENGTH_SHORT).show();
                    return;
                }

                Executors.newSingleThreadExecutor().execute(() -> {
                    database.userDao().insert(newUser);
                    User savedUser = database.userDao().getUserByEmail(email);

                    requireActivity().runOnUiThread(() -> {
                        if (savedUser != null) {
                            SharedPreferences prefs = requireContext().getSharedPreferences("FoodSensePrefs", Context.MODE_PRIVATE);
                            prefs.edit().putInt("userId", savedUser.getId()).apply();

                            Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigate(R.id.action_allergen_to_home);
                        } else {
                            Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}