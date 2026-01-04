package msku.ceng.madlab.foodsense;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for allergen matching algorithm
 * Tests the logic used in ResultFragment
 *
 * @author Ebru (Student)
 */
public class AllergenMatchingTest {

    @Before
    public void setUp() {
        // Setup if needed
    }

    @Test
    public void testLactoseDetection_English() {
        // İngilizce süt ürünü tespiti
        String text = "ingredients: milk, sugar, eggs".toLowerCase();
        assertTrue("Should detect milk", checkAllergenInText(text, "lactose"));
    }

    @Test
    public void testLactoseDetection_Turkish() {
        // Türkçe süt ürünü tespiti
        String text = "içindekiler: süt, şeker, yumurta".toLowerCase();
        assertTrue("Should detect süt", checkAllergenInText(text, "lactose"));
    }

    @Test
    public void testGlutenDetection_English() {
        // İngilizce gluten tespiti
        String text = "contains: wheat flour, sugar".toLowerCase();
        assertTrue("Should detect wheat", checkAllergenInText(text, "gluten"));
    }

    @Test
    public void testGlutenDetection_Turkish() {
        // Türkçe gluten tespiti
        String text = "buğday unu, şeker".toLowerCase();
        assertTrue("Should detect buğday", checkAllergenInText(text, "gluten"));
    }

    @Test
    public void testEggDetection() {
        // Yumurta tespiti
        String text = "ingredients: egg powder, milk".toLowerCase();
        assertTrue("Should detect egg", checkAllergenInText(text, "egg"));
    }

    @Test
    public void testNoAllergenFound() {
        // Allergen yok
        String text = "water, salt, pepper".toLowerCase();
        assertFalse("Should not detect lactose in water",
                checkAllergenInText(text, "lactose"));
        assertFalse("Should not detect gluten in salt",
                checkAllergenInText(text, "gluten"));
    }

    @Test
    public void testMixedLanguage() {
        // Karışık dil
        String text = "ingredients: milk, şeker, yumurta".toLowerCase();
        assertTrue("Should detect milk", checkAllergenInText(text, "lactose"));
        assertTrue("Should detect yumurta", checkAllergenInText(text, "egg"));
    }

    @Test
    public void testSeafoodDetection() {
        // Deniz ürünleri
        String text = "contains: tuna, salmon".toLowerCase();
        assertTrue("Should detect fish", checkAllergenInText(text, "seafood"));

        String textTurkish = "ton balığı, somon".toLowerCase();
        assertTrue("Should detect balık", checkAllergenInText(textTurkish, "seafood"));
    }

    @Test
    public void testSugarDetection() {
        // Şeker tespiti
        String text = "glucose, fructose, sugar".toLowerCase();
        assertTrue("Should detect sugar", checkAllergenInText(text, "sugar"));

        String textTurkish = "glikoz, şeker".toLowerCase();
        assertTrue("Should detect şeker", checkAllergenInText(textTurkish, "sugar"));
    }

    @Test
    public void testCaseInsensitivity() {
        // Büyük/küçük harf duyarlılığı
        String text1 = "MILK, SUGAR, EGGS";
        String text2 = "milk, sugar, eggs";
        String text3 = "Milk, Sugar, Eggs";

        assertTrue(checkAllergenInText(text1.toLowerCase(), "lactose"));
        assertTrue(checkAllergenInText(text2.toLowerCase(), "lactose"));
        assertTrue(checkAllergenInText(text3.toLowerCase(), "lactose"));
    }

    // ResultFragment'teki metodu kopyalıyoruz (test için)
    private boolean checkAllergenInText(String text, String allergen) {
        allergen = allergen.toLowerCase().trim();

        switch (allergen) {
            case "lactose":
                return text.contains("milk") ||
                        text.contains("dairy") ||
                        text.contains("lactose") ||
                        text.contains("whey") ||
                        text.contains("casein") ||
                        text.contains("butter") ||
                        text.contains("cream") ||
                        text.contains("cheese") ||
                        text.contains("yogurt") ||
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
                        text.contains("buğday") ||
                        text.contains("arpa") ||
                        text.contains("çavdar") ||
                        text.contains("un");

            case "egg":
                return text.contains("egg") ||
                        text.contains("albumin") ||
                        text.contains("egg powder") ||
                        text.contains("yumurta") ||
                        text.contains("yumurta tozu");

            case "soy":
                return text.contains("soy") ||
                        text.contains("soya") ||
                        text.contains("soybean") ||
                        text.contains("soy sauce") ||
                        text.contains("soya") ||
                        text.contains("soya fasulyesi") ||
                        text.contains("soya sosu");

            case "peanut":
                return text.contains("peanut") ||
                        text.contains("groundnut") ||
                        text.contains("nut") ||
                        text.contains("yer fıstığı") ||
                        text.contains("yerfıstığı") ||
                        text.contains("fıstık");

            case "seafood":
                return text.contains("fish") ||
                        text.contains("seafood") ||
                        text.contains("shrimp") ||
                        text.contains("tuna") ||
                        text.contains("salmon") ||
                        text.contains("balık") ||
                        text.contains("balik") ||
                        text.contains("deniz ürünü") ||
                        text.contains("karides") ||
                        text.contains("ton balığı") ||
                        text.contains("somon");

            case "sugar":
                return text.contains("sugar") ||
                        text.contains("sucrose") ||
                        text.contains("glucose") ||
                        text.contains("fructose") ||
                        text.contains("şeker") ||
                        text.contains("seker") ||
                        text.contains("glikoz") ||
                        text.contains("fruktoz");

            case "trans fats":
                return text.contains("trans fat") ||
                        text.contains("hydrogenated") ||
                        text.contains("trans yağ") ||
                        text.contains("trans yag") ||
                        text.contains("hidrojenize");

            default:
                return text.contains(allergen);
        }
    }
}