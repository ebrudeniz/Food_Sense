package msku.ceng.madlab.foodsense.ui.scan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import msku.ceng.madlab.foodsense.R;
import msku.ceng.madlab.foodsense.database.AppDatabase;
import msku.ceng.madlab.foodsense.databinding.FragmentResultBinding;
import msku.ceng.madlab.foodsense.models.User;

public class ResultFragment extends Fragment {

    private FragmentResultBinding binding;
    private AppDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        database = AppDatabase.getInstance(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Close button
        binding.btnClose.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_result_to_home);
        });

        // Get scanned text
        Bundle args = getArguments();
        if (args != null) {
            String scannedText = args.getString("scannedText", "");
            analyzeIngredients(scannedText);
        }
    }

    private void analyzeIngredients(String scannedText) {
        SharedPreferences prefs = requireContext().getSharedPreferences("FoodSensePrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            showError();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            User user = database.userDao().getUserById(userId);

            requireActivity().runOnUiThread(() -> {
                if (user != null) {
                    String restrictedIngredients = user.getRestrictedIngredients();

                    if (restrictedIngredients == null || restrictedIngredients.isEmpty()) {
                        showSafe(new ArrayList<>());
                        return;
                    }

                    List<String> restrictedList = Arrays.asList(restrictedIngredients.split(","));
                    List<String> foundAllergens = new ArrayList<>();
                    String scannedLower = scannedText.toLowerCase();

                    // Check each restricted ingredient
                    for (String allergen : restrictedList) {
                        String allergenLower = allergen.trim().toLowerCase();

                        // Check for the allergen name or common derivatives
                        if (checkAllergenInText(scannedLower, allergenLower)) {
                            foundAllergens.add(allergen.trim());
                        }
                    }

                    if (foundAllergens.isEmpty()) {
                        showSafe(foundAllergens);
                    } else {
                        showUnsafe(foundAllergens);
                    }
                } else {
                    showError();
                }
            });
        });
    }

    private boolean checkAllergenInText(String text, String allergen) {
        // Hem Türkçe hem İngilizce içerikleri ara
        allergen = allergen.toLowerCase().trim();

        switch (allergen) {
            case "lactose":
                // İngilizce kelimeler
                return text.contains("milk") ||
                        text.contains("dairy") ||
                        text.contains("lactose") ||
                        text.contains("whey") ||
                        text.contains("casein") ||
                        text.contains("butter") ||
                        text.contains("cream") ||
                        text.contains("cheese") ||
                        text.contains("yogurt") ||
                        // Türkçe kelimeler
                        text.contains("süt") ||
                        text.contains("laktoz") ||
                        text.contains("peynir") ||
                        text.contains("yoğurt") ||
                        text.contains("tereyağ") ||
                        text.contains("tereyağı") ||
                        text.contains("krema") ||
                        text.contains("kaymak") ||
                        text.contains("ayran");

            case "gluten":
                return text.contains("wheat") ||
                        text.contains("barley") ||
                        text.contains("rye") ||
                        text.contains("gluten") ||
                        text.contains("flour") ||
                        // Türkçe kelimeler
                        text.contains("buğday") ||
                        text.contains("arpa") ||
                        text.contains("çavdar") ||
                        text.contains("un");

            case "egg":
                return text.contains("egg") ||
                        text.contains("albumin") ||
                        text.contains("egg powder") ||
                        // Türkçe kelimeler
                        text.contains("yumurta") ||
                        text.contains("yumurta tozu");

            case "soy":
                return text.contains("soy") ||
                        text.contains("soya") ||
                        text.contains("soybean") ||
                        text.contains("soy sauce") ||
                        // Türkçe kelimeler
                        text.contains("soya") ||
                        text.contains("soya fasulyesi") ||
                        text.contains("soya sosu");

            case "peanut":
                return text.contains("peanut") ||
                        text.contains("groundnut") ||
                        text.contains("nut") ||
                        // Türkçe kelimeler
                        text.contains("yer fıstığı") ||
                        text.contains("yerfıstığı") ||
                        text.contains("fıstık") ||
                        text.contains("yer fistigi") || // i harfi olmadan
                        text.contains("yerfistigi");

            case "seafood":
                return text.contains("fish") ||
                        text.contains("seafood") ||
                        text.contains("shrimp") ||
                        text.contains("crab") ||
                        text.contains("lobster") ||
                        text.contains("mussel") ||
                        text.contains("oyster") ||
                        text.contains("squid") ||
                        text.contains("octopus") ||
                        text.contains("tuna") ||
                        text.contains("salmon") ||
                        text.contains("shellfish") ||
                        // Türkçe kelimeler
                        text.contains("balık") ||
                        text.contains("balik") || // ı harfi olmadan
                        text.contains("deniz ürünü") ||
                        text.contains("deniz urunu") || // ü harfi olmadan
                        text.contains("karides") ||
                        text.contains("yengeç") ||
                        text.contains("yengec") ||
                        text.contains("istakoz") ||
                        text.contains("midye") ||
                        text.contains("istiridye") ||
                        text.contains("kalamar") ||
                        text.contains("ahtapot") ||
                        text.contains("ton balığı") ||
                        text.contains("ton baligi") ||
                        text.contains("somon") ||
                        text.contains("hamsi") ||
                        text.contains("levrek") ||
                        text.contains("çipura") ||
                        text.contains("cipura");

            case "sugar":
                return text.contains("sugar") ||
                        text.contains("sucrose") ||
                        text.contains("glucose") ||
                        text.contains("fructose") ||
                        text.contains("dextrose") ||
                        text.contains("corn syrup") ||
                        text.contains("honey") ||
                        // Türkçe kelimeler
                        text.contains("şeker") ||
                        text.contains("seker") || // ş harfi olmadan
                        text.contains("sakkaroz") ||
                        text.contains("glikoz") ||
                        text.contains("fruktoz") ||
                        text.contains("dekstroz") ||
                        text.contains("mısır şurubu") ||
                        text.contains("misir surubu") || // ı, ş, ü harfleri olmadan
                        text.contains("bal");

            case "trans fats":
                return text.contains("trans fat") ||
                        text.contains("hydrogenated") ||
                        text.contains("partially hydrogenated") ||
                        text.contains("vegetable oil") ||
                        text.contains("margarine") ||
                        // Türkçe kelimeler
                        text.contains("trans yağ") ||
                        text.contains("trans yag") || // ğ harfi olmadan
                        text.contains("hidrojenize") ||
                        text.contains("kısmi hidrojenize") ||
                        text.contains("kismi hidrojenize") || // ı harfi olmadan
                        text.contains("bitkisel yağ") ||
                        text.contains("bitkisel yag") ||
                        text.contains("margarin");

            default:
                // Varsayılan: direkt arama
                return text.contains(allergen);
        }
    }

    private void showSafe(List<String> allergens) {
        // Green card
        binding.cardResult.setCardBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));

        // Check icon
        binding.tvIcon.setText("✓");

        // Title
        binding.tvResultTitle.setText("Safe For\nConsumption");

        // Hide message
        binding.tvResultMessage.setVisibility(View.GONE);
    }

    private void showUnsafe(List<String> foundAllergens) {
        // Red card
        binding.cardResult.setCardBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));

        // Warning icon
        binding.tvIcon.setText("!");

        // Title
        binding.tvResultTitle.setText("UNSAFE!!!");

        // Show allergens
        StringBuilder message = new StringBuilder("Contains ");
        for (int i = 0; i < foundAllergens.size(); i++) {
            message.append(foundAllergens.get(i));
            if (i < foundAllergens.size() - 1) {
                message.append(", ");
            }
        }

        binding.tvResultMessage.setText(message.toString());
        binding.tvResultMessage.setVisibility(View.VISIBLE);
    }

    private void showError() {
        binding.cardResult.setCardBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
        binding.tvIcon.setText("?");
        binding.tvResultTitle.setText("Error");
        binding.tvResultMessage.setText("Unable to analyze ingredients");
        binding.tvResultMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}