package msku.ceng.madlab.foodsense.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String surname;
    private String email;
    private String password;
    private String birthDate;
    private String gender;
    private String restrictedIngredients; // JSON string olarak saklanacak

    // Constructor
    public User(String name, String surname, String email, String password,
                String birthDate, String gender, String restrictedIngredients) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.gender = gender;
        this.restrictedIngredients = restrictedIngredients;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRestrictedIngredients() {
        return restrictedIngredients;
    }

    public void setRestrictedIngredients(String restrictedIngredients) {
        this.restrictedIngredients = restrictedIngredients;
    }
}