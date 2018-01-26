package com.tinder.edison.tinder.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinder.edison.tinder.R;

/**
 * Created by Edison on 20/01/2018.
 */

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mMessage;
    public Button mSend;
    public LinearLayout mContainer;


    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMessage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);
        mSend = itemView.findViewById(R.id.send);

    }

    @Override
    public void onClick(View view) {




    }
}
