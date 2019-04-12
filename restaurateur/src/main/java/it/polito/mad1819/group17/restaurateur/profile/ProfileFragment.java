package it.polito.mad1819.group17.restaurateur.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import it.polito.mad1819.group17.restaurateur.R;
import it.polito.mad1819.group17.restaurateur.utils.PrefHelper;


public class ProfileFragment extends Fragment {

    public static final String PHOTO = "restaurant_photo";
    public static final String NAME = "restaurant_name";
    public static final String PHONE = "restaurant_phone";
    public static final String MAIL = "restaurant_mail";
    public static final String ADDRESS = "restaurant_address";
    public static final String RESTAURANT_TYPE = "restaurant_type";
    public static final String FREE_DAY = "restaurant_free_day";
    public static final String TIME_OPENING = "restaurant_time_opening";
    public static final String TIME_CLOSING = "restaurant_time_closing";
    public static final String BIO = "restaurant_bio";

    private ImageView image_user_photo;
    private TextView txt_name;
    private TextView txt_phone;
    private TextView txt_mail;
    private TextView txt_address;
    private TextView txt_restaurant_type;
    private TextView txt_free_day;
    private TextView txt_working_time;
    private TextView txt_bio;

    private void locateViews(View view) {
        image_user_photo = view.findViewById(R.id.image_user_photo);
        txt_name = view.findViewById(R.id.txt_name);
        txt_phone = view.findViewById(R.id.txt_phone);
        txt_mail = view.findViewById(R.id.txt_mail);
        txt_address = view.findViewById(R.id.txt_address);
        txt_restaurant_type = view.findViewById(R.id.txt_restaurant_type);
        txt_free_day = view.findViewById(R.id.txt_free_day);
        txt_working_time = view.findViewById(R.id.txt_working_time);
        txt_bio = view.findViewById(R.id.txt_bio);
    }

    private void feedViews() {
        String stringUserPhoto = PrefHelper.getInstance().getString(PHOTO, null);
        if (stringUserPhoto != null) {
            image_user_photo.setImageBitmap(PrefHelper.stringToBitMap(stringUserPhoto));
            image_user_photo.setPadding(8, 8, 8, 8);
        }

        String name = PrefHelper.getInstance().getString(ProfileFragment.NAME, null);
        if (name != null)
            txt_name.setText(name);

        String phone = PrefHelper.getInstance().getString(ProfileFragment.PHONE, null);
        if (phone != null)
            txt_phone.setText(phone);

        String mail = PrefHelper.getInstance().getString(ProfileFragment.MAIL, null);
        if (mail != null)
            txt_mail.setText(mail);

        String address = PrefHelper.getInstance().getString(ProfileFragment.ADDRESS, null);
        if (address != null)
            txt_address.setText(address);

        String restaurant_type = PrefHelper.getInstance().getString(ProfileFragment.RESTAURANT_TYPE, null);
        if (restaurant_type != null)
            txt_restaurant_type.setText(restaurant_type);

        String free_day = PrefHelper.getInstance().getString(ProfileFragment.FREE_DAY, null);
        if (free_day != null)
            txt_free_day.setText(free_day);

        String time_opening = PrefHelper.getInstance().getString(ProfileFragment.TIME_OPENING, null);
        String time_closing = PrefHelper.getInstance().getString(ProfileFragment.TIME_CLOSING, null);
        if (time_opening != null && time_closing != null)
            txt_working_time.setText(getString(R.string.from) + " " + time_opening + " " + getString(R.string.to) + " " + time_closing);

        String bio = PrefHelper.getInstance().getString(ProfileFragment.BIO, null);
        if (bio != null)
            txt_bio.setText(bio);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        locateViews(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        feedViews();
    }
}
