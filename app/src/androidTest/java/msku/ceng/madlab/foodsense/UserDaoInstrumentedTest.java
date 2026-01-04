package msku.ceng.madlab.foodsense;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import msku.ceng.madlab.foodsense.database.AppDatabase;
import msku.ceng.madlab.foodsense.database.UserDao;
import msku.ceng.madlab.foodsense.models.User;

/**
 * Instrumented Database tests
 * Runs on Android device/emulator
 *
 * @author Ebru (Student)
 */
@RunWith(AndroidJUnit4.class)
public class UserDaoInstrumentedTest {

    private AppDatabase database;
    private UserDao userDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        userDao = database.userDao();
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void testInsertAndRetrieve() {
        User user = new User("Test", "User", "test@test.com", "pass",
                "01/01/2000", "Male", "Gluten");
        userDao.insert(user);

        User retrieved = userDao.getUserByEmail("test@test.com");
        assertNotNull(retrieved);
        assertEquals("Test", retrieved.getName());
    }

    @Test
    public void testLogin() {
        User user = new User("John", "Doe", "john@test.com", "password123",
                "01/01/1990", "Male", "");
        userDao.insert(user);

        User loggedIn = userDao.login("john@test.com", "password123");
        assertNotNull(loggedIn);

        User failed = userDao.login("john@test.com", "wrong");
        assertNull(failed);
    }
}