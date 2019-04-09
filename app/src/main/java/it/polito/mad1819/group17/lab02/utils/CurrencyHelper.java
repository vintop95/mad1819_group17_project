package it.polito.mad1819.group17.lab02.utils;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyHelper {
    private static PrefHelper prefHelper = PrefHelper.getInstance();
    /////////////////// CURRENCY MGMT ////////////////////////////////
    private static final String PREF_CURRENCY_LANGUAGE = "PREF_CURRENCY_LANGUAGE";
    private static final String PREF_CURRENCY_COUNTRY = "PREF_CURRENCY_COUNTRY";

    private static Locale mCurrentLocale = null;

    public static void setLocaleCurrency(Locale locale) {
        prefHelper.putString(PREF_CURRENCY_LANGUAGE, locale.getLanguage());
        prefHelper.putString(PREF_CURRENCY_COUNTRY, locale.getCountry());
        mCurrentLocale = locale;
    }

    public static Locale getLocaleCurrency() {
        if(mCurrentLocale == null){
            // CHECK PREFHELPER, OTHERWISE SET A DEFAULT CURRENCY
            String language = prefHelper.getString(
                    PREF_CURRENCY_LANGUAGE, Locale.ITALY.getLanguage());
            String country = prefHelper.getString(
                    PREF_CURRENCY_COUNTRY, Locale.ITALY.getCountry());
            setLocaleCurrency(new Locale(language, country));
        }
        return mCurrentLocale;
    }

    public static String getCurrency(double val){
        NumberFormat currencyFormat =
                NumberFormat.getCurrencyInstance(getLocaleCurrency());
        return currencyFormat.format(val);
    }

    public static String getCurrencySymbol(){
        return Currency.getInstance(getLocaleCurrency()).getSymbol();
    }
    /////////////////////////////////////////////////////////////////
}
