package msku.ceng.madlab.foodsense;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import msku.ceng.madlab.foodsense.models.User;

/**
 * Unit tests for User model
 * Tests basic getter/setter functionality and data validation
 *
 * @author Ebru (Student)
 */
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        // Her test öncesi çalışır
        user = new User("John", "Doe", "john@test.com", "password123",
                "01/01/1990", "Male", "Gluten,Lactose");
    }

    @Test
    public void testUserCreation() {
        // User doğru oluşturuldu mu?
        assertNotNull("User should not be null", user);
        assertEquals("John", user.getName());
        assertEquals("Doe", user.getSurname());
        assertEquals("john@test.com", user.getEmail());
    }

    @Test
    public void testGettersAndSetters() {
        // Getters ve Setters çalışıyor mu?
        user.setName("Jane");
        assertEquals("Jane", user.getName());

        user.setEmail("jane@test.com");
        assertEquals("jane@test.com", user.getEmail());
    }

    @Test
    public void testRestrictedIngredientsFormat() {
        // Allergen listesi CSV formatında mı?
        String ingredients = user.getRestrictedIngredients();
        assertNotNull(ingredients);
        assertTrue(ingredients.contains(","));

        String[] allergens = ingredients.split(",");
        assertEquals(2, allergens.length);
        assertEquals("Gluten", allergens[0]);
        assertEquals("Lactose", allergens[1]);
    }

    @Test
    public void testEmptyRestrictedIngredients() {
        // Boş allergen listesi
        User userNoAllergens = new User("Test", "User", "test@test.com",
                "pass", "01/01/2000", "Male", "");
        assertEquals("", userNoAllergens.getRestrictedIngredients());
    }

    @Test
    public void testEmailValidation() {
        // Email formatı (basit test)
        String email = user.getEmail();
        assertTrue("Email should contain @", email.contains("@"));
        assertTrue("Email should contain .", email.contains("."));
    }

    @Test
    public void testPasswordNotEmpty() {
        // Password boş olmamalı
        String password = user.getPassword();
        assertNotNull(password);
        assertFalse("Password should not be empty", password.isEmpty());
        assertTrue("Password should be at least 6 characters",
                password.length() >= 6);
    }
}