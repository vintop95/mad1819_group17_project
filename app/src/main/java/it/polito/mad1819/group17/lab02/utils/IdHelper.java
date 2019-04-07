package it.polito.mad1819.group17.lab02.utils;

import java.util.concurrent.atomic.AtomicLong;

public class IdHelper {
    private static PrefHelper prefHelper = PrefHelper.getInstance();

    ///////////////////// ID MGMT ///////////////////////////////////
    // ID IS NECESSARY FOR EACH OBJECT TO BE UNIQUE IN STORAGE
    private final AtomicLong NEXT_ID = new AtomicLong(loadNextIdFromPref());

    private Class cls;

    public IdHelper(Class cls){
        this.cls = cls;
    }

    private String getClassName(){
        return cls.getName();
    }

    private Long loadNextIdFromPref(){
        // returns 0 if not set
        return prefHelper.getLong(getNextIdPrefKey());
    }

    private void saveNextIdToPref(){
        prefHelper.putLong(getNextIdPrefKey(), NEXT_ID.get());
    }

    private String getNextIdPrefKey(){
        return "PREF_NEXT_ID_" + getClassName();
    }

    public String getAndIncrementNextId() {
        long longNextId = NEXT_ID.getAndIncrement();
        saveNextIdToPref();
        return getClassName() + "_" + longNextId;
    }
    //////////////////////////////////////////////////////////////
}
