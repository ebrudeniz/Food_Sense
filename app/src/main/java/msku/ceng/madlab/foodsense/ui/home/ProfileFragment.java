package msku.ceng.madlab.foodsense.ui.home;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.Calendar;
import java.util.concurrent.Executors;

import msku.ceng.madlab.foodsense.R;
import msku.ceng.madlab.foodsense.database.AppDatabase;
import msku.ceng.madlab.foodsense.databinding.FragmentProfileBinding;
import msku.ceng.madlab.foodsense.models.User;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AppDatabase database;
    private int userId;
    private String selectedBirthDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
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

        setupGenderSpinner();
        setupBirthDatePicker();
        loadUserData();

        // Update Restricted Ingredients link
        binding.tvUpdateIngredients.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profile_to_updateAllergens);
        });

        // Update button
        binding.btnUpdate.setOnClickListener(v -> updateUserData());
    }

    private void setupGenderSpinner() {
        String[] genders = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGender.setAdapter(adapter);
    }

    private void setupBirthDatePicker() {
        binding.etBirthDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedBirthDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        binding.etBirthDate.setText(selectedBirthDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void loadUserData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = database.userDao().getUserById(userId);

            requireActivity().runOnUiThread(() -> {
                if (user != null) {
                    binding.etName.setText(user.getName());
                    binding.etSurname.setText(user.getSurname());
                    binding.etEmail.setText(user.getEmail());
                    selectedBirthDate = user.getBirthDate();
                    binding.etBirthDate.setText(selectedBirthDate);

                    // Set gender spinner
                    String[] genders = {"Male", "Female", "Other"};
                    for (int i = 0; i < genders.length; i++) {
                        if (genders[i].equals(user.getGender())) {
                            binding.spinnerGender.setSelection(i);
                            break;
                        }
                    }
                }
            });
        });
    }

    private void updateUserData() {
        String name = binding.etName.getText().toString().trim();
        String surname = binding.etSurname.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String gender = binding.spinnerGender.getSelectedItem().toString();

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            User user = database.userDao().getUserById(userId);

            if (user != null) {
                user.setName(name);
                user.setSurname(surname);
                user.setEmail(email);
                if (!password.isEmpty()) {
                    user.setPassword(password);
                }
                user.setBirthDate(selectedBirthDate);
                user.setGender(gender);

                // Update in database
                database.userDao().update(user);

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigate(R.id.action_profile_to_home);
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}