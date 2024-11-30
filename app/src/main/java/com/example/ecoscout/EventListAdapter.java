package com.example.ecoscout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EventListAdapter extends ArrayAdapter<EventList> {
    public EventListAdapter(@NonNull Context context, ArrayList<EventList> dataArrayList) {
        super(context, R.layout.event_list, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        EventList listData = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_list, parent, false);
        }

        ImageView listImage = view.findViewById(R.id.listImage);
        TextView listName = view.findViewById(R.id.listName);

        listImage.setImageResource(listData.image);
        listName.setText(listData.name);

        return view;
    }
}