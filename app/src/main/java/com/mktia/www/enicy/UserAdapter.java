package com.mktia.www.enicy;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dev.niekirk.com.instagram4android.requests.payload.InstagramUserSummary;

public class UserAdapter extends ArrayAdapter<InstagramUserSummary> {

    public UserAdapter(Activity context, ArrayList<InstagramUserSummary> user) {
        super(context, 0, user);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.user, parent, false);
        }

        final InstagramUserSummary currentUser = getItem(position);

        TextView userNameTextView = listItemView.findViewById(R.id.user_name);
        TextView fullNameTextView = listItemView.findViewById(R.id.full_name);
        ImageView userImageView = listItemView.findViewById(R.id.user_icon);

        userNameTextView.setText(getContext().getString(R.string.account_name, currentUser.getUsername()));
        fullNameTextView.setText(currentUser.getFull_name());

        GlideApp.with(getContext())
                .load(currentUser.getProfile_pic_url())
                .error(new ColorDrawable(Color.LTGRAY))
                .circleCrop()
                .into(userImageView);

        return listItemView;
    }
}
