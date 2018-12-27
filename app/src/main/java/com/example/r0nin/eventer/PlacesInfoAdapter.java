package com.example.r0nin.eventer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class PlacesInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private final View view;
    private Context context;

    public PlacesInfoAdapter(Context context) {
        this.view = LayoutInflater.from(context).inflate(R.layout.information_window, null);
        this.context = context;
    }

    private void setTextToWindow(Marker marker, View view){
        String text = marker.getTitle();
        String snippet = marker.getSnippet();
        TextView textViewTitle = (TextView) view.findViewById(R.id.title);
        TextView textViewSnippet = (TextView) view.findViewById(R.id.snippet);
        TextView textViewInfo = (TextView) view.findViewById(R.id.findMore);
        if(!text.equals("")){
            textViewTitle.setText(text);
        }
        if(!snippet.equals("")){
            textViewSnippet.setText(snippet);
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        setTextToWindow(marker, view);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        setTextToWindow(marker, view);
        return view;
    }
}
