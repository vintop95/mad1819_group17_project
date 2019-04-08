package it.polito.mad1819.group17.lab02.profile;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import it.polito.mad1819.group17.lab02.R;
import it.polito.mad1819.group17.lab02.utils.PrefHelper;


public class ProfileFragment extends Fragment {

    private ImageView image_user_photo;

    private void locateViews(View view) {
        image_user_photo = view.findViewById(R.id.image_user_photo);
    }

    private void feedViews(){
        String stringUserPhoto = PrefHelper.getInstance().getString("image_user_photo", null);
        if (stringUserPhoto != null) {
            image_user_photo.setImageBitmap(PrefHelper.stringToBitMap(stringUserPhoto));
            image_user_photo.setPadding(8, 8, 8, 8);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        locateViews(view);

        feedViews();

        return view;
    }

}
