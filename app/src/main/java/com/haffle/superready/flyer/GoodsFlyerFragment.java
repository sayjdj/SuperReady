package com.haffle.superready.flyer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.haffle.superready.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class GoodsFlyerFragment extends Fragment {

    String url;
    ImageView image;
    Context context;
    PhotoViewAttacher photoViewAttacher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.flyer_fragment_one, container, false);
        context= getActivity().getApplicationContext();

        url = getArguments().getString("IMAGE");
        image = (ImageView)view.findViewById(R.id.flyerFragmentOne_image);

        Glide.with(context)
                .load(url)
                .into(image);

        photoViewAttacher = new PhotoViewAttacher(image);

        return view;
    }
}
