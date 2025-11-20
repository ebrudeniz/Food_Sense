package msku.ceng.madlab.foodsense.ui.auth;

import android.app.DatePickerDialog;
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

import msku.ceng.madlab.foodsense.R;
import msku.ceng.madlab.foodsense.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private String selectedBirthDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupGenderSpinner();
        setupBirthDatePicker();

        binding.btnNext.setOnClickListener(v -> {
            if (validateInputs()) {
                Bundle bundle = new Bundle();
                bundle.putString("name", binding.etName.getText().toString().trim());
                bundle.putString("surname", binding.etSurname.getText().toString().trim());
                bundle.putString("email", binding.etEmail.getText().toString().trim());
                bundle.putString("password", binding.etPassword.getText().toString().trim());
                bundle.putString("birthDate", selectedBirthDate);
                bundle.putString("gender", binding.spinnerGender.getSelectedItem().toString());

                Navigation.findNavController(v).navigate(R.id.action_signUp_to_allergenSelection, bundle);
            }
        });
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

    private boolean validateInputs() {
        String name = binding.etName.getText().toString().trim();
        String surname = binding.etSurname.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (surname.isEmpty()) {
            Toast.makeText(requireContext(), "Surname is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedBirthDate.isEmpty()) {
            Toast.makeText(requireContext(), "Birth date is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}