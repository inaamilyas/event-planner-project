package com.example.eventplanner.VenueManager.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.VenueManager.EditVenueActivity;
import com.example.eventplanner.VenueManager.VenueManagerVenDetailsActivity;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.models.Venue;

import java.util.ArrayList;

public class MangerVenueAdapter extends RecyclerView.Adapter<MangerVenueAdapter.ViewHolder> {
    private ArrayList<Venue> venuesList;

    public MangerVenueAdapter(ArrayList<Venue> venuesList) {
        this.venuesList = venuesList;
    }

    @NonNull
    @Override
    public MangerVenueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.manager_venue_item, viewGroup, false);

        return new MangerVenueAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MangerVenueAdapter.ViewHolder holder, int position) {
        Venue venue = venuesList.get(position);
        holder.setView(venue);
    }

    @Override
    public int getItemCount() {
        return venuesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView venueName;
        TextView venueAddress;
        //        TextView event;
        ImageView venueImage;

        public ViewHolder(View view) {
            super(view);
            this.venueName = (TextView) view.findViewById(R.id.venue_list_item_title);

            this.venueAddress = (TextView) view.findViewById(R.id.venue_list_item_address);

            this.venueImage = (ImageView) view.findViewById(R.id.venue_list_item_image);


            // Define click listener for the ViewHolder's View
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Venue selectedVenue = venuesList.get(position);

                    Intent intent = new Intent(view.getContext(), VenueManagerVenDetailsActivity.class);
                    intent.putExtra("selectedVenue", selectedVenue);
                    view.getContext().startActivity(intent);
                }
            });

//            edit venue details
            view.findViewById(R.id.venue_list_item_edit_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Venue selectedVenue = venuesList.get(position);

                    Intent intent = new Intent(view.getContext(), EditVenueActivity.class);
                    intent.putExtra("selectedVenue", selectedVenue);
                    view.getContext().startActivity(intent);
                }
            });

            view.findViewById(R.id.venue_list_item_view_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Venue selectedVenue = venuesList.get(position);

                    Intent intent = new Intent(view.getContext(), VenueManagerVenDetailsActivity.class);
                    intent.putExtra("selectedVenue", selectedVenue);
                    view.getContext().startActivity(intent);
                }
            });
        }

        public void setView(Venue venue) {
            this.venueName.setText(venue.getName());
            this.venueAddress.setText(venue.getAddress());
            Glide.with(this.venueImage.getContext()).load(AppConfig.SERVER_URL + venue.getPicture()).placeholder(R.drawable.enent_image).into(this.venueImage);
//            this.eventImage.setImageResource(R.drawable.enent_image);
        }
    }
}
