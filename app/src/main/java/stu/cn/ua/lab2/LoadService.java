package stu.cn.ua.lab2;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LoadService extends Service {

    private LoadBinder binder = new LoadBinder();
    private SharedPreferences preferences;
    public static final String GAME_SETTINGS = "GAME_SETTINGS";
    public static final String DIFFICULTY = "DIFFICULTY";

    public static final String TIMER_MINUTES = "TIMER_MINUTES";

    public static final String HARD_WORDS = "HARD_WORDS";
    public static final String NORMAL_WORDS = "NORMAL_WORDS";

    private Set<SettingsListener> listeners = new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = getSharedPreferences(GAME_SETTINGS,MODE_PRIVATE);

        ArrayList<String> normalWords = new ArrayList<>(Arrays.asList("мама", "тато", "сонце", "радіо", "фото", "риба", "ложка", "хліб", "миша", "кінь", "плащ"));
        ArrayList<String> hardWords = new ArrayList<>(Arrays.asList("горизонт", "завдання", "календар", "магістр", "розповідь", "сімейство", "функція", "характер", "швидкість", "чернігів", "фотограф"));

        Set<String> normalWordsSet = new HashSet<>(normalWords);
        Set<String> hardWordsSet = new HashSet<>(hardWords);
        preferences.edit()
                .putStringSet(NORMAL_WORDS, normalWordsSet)
                .putStringSet(HARD_WORDS, hardWordsSet)
                .apply();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listeners.clear();
    }

    public void saveSettings(int difficulty, int timer_minutes) {
        preferences.edit()
                .putInt(DIFFICULTY,difficulty)
                .putInt(TIMER_MINUTES,timer_minutes)
                .apply();
    }

    public ArrayList<Integer> getSettings() {
        ArrayList<Integer> settings = new ArrayList<>();
        settings.add(preferences.getInt(DIFFICULTY,0));
        settings.add(preferences.getInt(TIMER_MINUTES,2));
        return settings;
    }

    public ArrayList<String> getNormalWords() {
        Set<String> normalWordsSet = preferences.getStringSet(NORMAL_WORDS, null);
        return new ArrayList<>(normalWordsSet);
    }

    public ArrayList<String> getHardWords() {
        Set<String> hardWordsSet = preferences.getStringSet(HARD_WORDS, null);
        return new ArrayList<>(hardWordsSet);
    }

    public void addSettingsListener(SettingsListener listener) {
        this.listeners.add(listener);
        listener.onGotSettings(getSettings(),getNormalWords(),getHardWords());
    }
    public void removeSettingsListener(SettingsListener listener) {
        this.listeners.remove(listener);
    }

    interface SettingsListener {
        void onGotSettings(ArrayList<Integer> settings,ArrayList<String> normal_words,ArrayList<String> hard_words);
    }

    class LoadBinder extends Binder {
        public LoadService getService() {
            return LoadService.this;
        }
    }
}
