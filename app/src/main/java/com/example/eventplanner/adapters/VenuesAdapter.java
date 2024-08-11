package com.example.eventplanner.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.BookVenueActivity;
import com.example.eventplanner.R;
import com.example.eventplanner.VenueDetailsActivity;
import com.example.eventplanner.models.Venue;

public class VenuesAdapter extends RecyclerView.Adapter<VenuesAdapter.ViewHolder> {
    private Venue[] venues;

    public VenuesAdapter(Venue[] venues) {
        this.venues = venues;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.venue_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("inaaaaam", "onBindViewHolder: " + position);
        Venue venue = venues[position];
        Log.d("inaaaaam", "onBindViewHolder: " + venue);
        holder.setView(venue);
    }

    @Override
    public int getItemCount() {
        return venues.length;
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
                    Intent intent = new Intent(view.getContext(), VenueDetailsActivity.class);
                    view.getContext().startActivity(intent);
                    Toast.makeText(view.getContext(), "click on item " + position, Toast.LENGTH_SHORT).show();
                    // Do something with the clicked event
//                    }
                }
            });

//            view venue details
            view.findViewById(R.id.venue_list_item_view_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(view.getContext(), VenueDetailsActivity.class);
                    view.getContext().startActivity(intent);
                    Toast.makeText(view.getContext(), "Clicked on view " + position, Toast.LENGTH_SHORT).show();
                }
            });

            view.findViewById(R.id.venue_list_item_book_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(view.getContext(), BookVenueActivity.class);
                    view.getContext().startActivity(intent);
                    Toast.makeText(view.getContext(), "Clicked on book " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setView(Venue venue) {
            this.venueName.setText(venue.getName());
            this.venueAddress.setText(venue.getAddress());
            Glide.with(this.venueImage.getContext()).load(venue.getImage()).placeholder(R.drawable.enent_image).into(this.venueImage);
//            this.eventImage.setImageResource(R.drawable.enent_image);
        }
    }

}
