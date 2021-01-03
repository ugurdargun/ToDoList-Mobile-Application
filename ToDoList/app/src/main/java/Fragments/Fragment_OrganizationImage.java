package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.msu.todolist.R;
import com.squareup.picasso.Picasso;

public class Fragment_OrganizationImage extends Fragment {

    ImageView org_Img;

    public Fragment_OrganizationImage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organization_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        org_Img = view.findViewById(R.id.fragment_CategoryImg1);

        Picasso.with(getContext())
                .load("https://i.imgur.com/mFsEplQ.png")
                .into(org_Img);
    }


}