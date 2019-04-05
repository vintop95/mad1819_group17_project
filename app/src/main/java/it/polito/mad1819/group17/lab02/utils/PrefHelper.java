/*
 * Copyright 2019 Group 17 Mobile Application Development PoliTO 2018-19
 * s252117 Cavalcanti Piero
 * s253177 Laudani Angelo
 * s251372 Mieuli Valerio
 * s253137 Topazio Vincenzo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.polito.mad1819.group17.lab02.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class PrefHelper {
    private static PrefHelper instance = null;
    private static Context mainContext = null;

    private static final String MY_PREF = "MyPreferences";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String exceptionInfo = "PrefHelper not initialized";

    ////////////////// INITIALIZATION MANAGEMENT
    @SuppressLint("CommitPrefEdits")
    private PrefHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(MY_PREF, 0);
        this.editor = this.sharedPreferences.edit();
    }

    public static boolean isReady(){
        return instance != null && mainContext != null;
    }

    // CALL BEFORE getInstance()
    public static void setMainContext(Context c){
        if(instance == null) instance = new PrefHelper(c);
        mainContext = c;
    }

    public static PrefHelper getInstance(){
        if(!isReady()) throw new RuntimeException("Context not set.");
        return instance;
    }
    ////////////////////////////////////////////////////////////

    public void putString(String key, String value) {
        if (editor == null) throw new RuntimeException(exceptionInfo);
        this.editor.putString(key, value);
        this.editor.commit();
    }

    public String getString(String key) {
        if (sharedPreferences == null) throw new RuntimeException(exceptionInfo);
        return this.sharedPreferences.getString(key, null);
    }

    public String getString(String key, String defValue) {
        if (sharedPreferences == null) throw new RuntimeException(exceptionInfo);
        return this.sharedPreferences.getString(key, defValue);
    }

    ////////////////////////////////////////////////////////////////////////////

    public void putLong(String key, long value) {
        if (editor == null) throw new RuntimeException(exceptionInfo);
        this.editor.putLong(key, value);
        this.editor.commit();
    }

    public long getLong(String key) {
        if (sharedPreferences == null) throw new RuntimeException(exceptionInfo);
        return this.sharedPreferences.getLong(key, 0);
    }

    /////////////////////////////////////////////////////////////////////////////

    public void clear(String key) {
        if (editor == null) throw new RuntimeException(exceptionInfo);
        this.editor.remove(key);
        this.editor.commit();
    }

    public void clear() {
        if (editor == null) throw new RuntimeException(exceptionInfo);
        this.editor.clear();
        this.editor.commit();
    }

    public static Bitmap stringToBitMap(String encodedString) throws IllegalArgumentException{
        byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }

    public static String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 7, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

}
