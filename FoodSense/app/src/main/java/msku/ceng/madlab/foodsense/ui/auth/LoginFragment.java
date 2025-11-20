package msku.ceng.madlab.foodsense.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.concurrent.Executors;

import msku.ceng.madlab.foodsense.R;
import msku.ceng.madlab.foodsense.database.AppDatabase;
import msku.ceng.madlab.foodsense.databinding.FragmentLoginBinding;
import msku.ceng.madlab.foodsense.models.User;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AppDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        database = AppDatabase.getInstance(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sign In button
        binding.btnSignIn.setOnClickListener(v -> handleLogin());

        // Sign Up link
        binding.tvSignUp.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_login_to_signUp);
        });

        // Forgot Password
        binding.tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Email is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Password is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            User user = database.userDao().login(email, password);

            requireActivity().runOnUiThread(() -> {
                if (user != null) {
                    SharedPreferences prefs = requireContext().getSharedPreferences("FoodSensePrefs", Context.MODE_PRIVATE);
                    prefs.edit().putInt("userId", user.getId()).apply();

                    Toast.makeText(requireContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigate(R.id.action_login_to_home);
                } else {
                    Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}