package com.example.projektsklep;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Product.class}, version = 3, exportSchema = false)
public abstract class StoreDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ProductDao productDao();

    private static volatile StoreDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static StoreDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (StoreDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StoreDatabase.class, "store_db")
                            .addCallback(sRoomDatabaseCallback)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                UserDao dao = INSTANCE.userDao();

                dao.deleteAll();

                User user = new User("admin@admin.com", "admin", true, "Adminer", "Admin");
                dao.insert(user);
            });
        }
    };
}
