package com.tinder.edison.tinder.Cards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tinder.edison.tinder.Cards.cards;
import com.tinder.edison.tinder.R;

import java.util.List;

/**
 * Created by Edison on 18/01/2018.
 */

public class arrayAdapter extends android.widget.ArrayAdapter{

    Context context;

    public arrayAdapter(Context context, int resourceId, List<cards> items){
        super(context, resourceId, items);

    }
    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent){
        cards card_item = (cards) getItem(position);


        if(convertView == null){

            convertView =convertView  = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView image =(ImageView) convertView.findViewById(R.id.image);

        name.setText(card_item.getName());
        //image.setImageResource(R.mipmap.ic_launcher_round);
        switch (card_item.getProfileImageUrl()){
            case "default":

                Glide.with(convertView.getContext()).load(R.raw.default_image).into(image);
                break;

            default:
                Glide.clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;

        }


        return convertView;


    }
}
