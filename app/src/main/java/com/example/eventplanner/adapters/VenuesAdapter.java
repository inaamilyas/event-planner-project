package com.example.eventplanner.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.BookVenueActivity;
import com.example.eventplanner.R;
import com.example.eventplanner.VenueDetailsActivity;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.fragments.BookVenueFragment;
import com.example.eventplanner.models.Venue;

import java.util.ArrayList;

public class VenuesAdapter extends RecyclerView.Adapter<VenuesAdapter.ViewHolder> {
    private final ArrayList<Venue> venuesList;
    private int eventId = -1;

    public VenuesAdapter(ArrayList<Venue> venuesList) {
        this.venuesList = venuesList;
    }

    public VenuesAdapter(ArrayList<Venue> venuesList, int eventId) {
        this.venuesList = venuesList;
        this.eventId = eventId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.venue_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        TextView venueDistance;
        ImageView venueImage;

        public ViewHolder(View view) {
            super(view);
            this.venueName = (TextView) view.findViewById(R.id.venue_list_item_title);
            this.venueAddress = (TextView) view.findViewById(R.id.venue_list_item_address);
            this.venueDistance = (TextView) view.findViewById(R.id.venue_list_item_distance);
            this.venueImage = (ImageView) view.findViewById(R.id.venue_list_item_image);

            // Define click listener for the ViewHolder's View
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
//                    Intent intent = new Intent(view.getContext(), VenueDetailsActivity.class);
                    Venue selectedVenue = venuesList.get(position);
//                    intent.putExtra("selectedVenue", selectedVenue);
//                    view.getContext().startActivity(intent);

                    // Show bottom sheet
//                    BookVenueFragment bookVenueFragment = new BookVenueFragment();
//                    bookVenueFragment.show(fragmentManager, "BookVenueBottomSheet");

                    // Show bottom sheet
                    BookVenueFragment bookVenueFragment = BookVenueFragment.newInstance(selectedVenue, String.valueOf(eventId));
                    FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                    bookVenueFragment.show(fragmentManager, "BookVenueBottomSheet");

                }
            });

//            view venue details
            view.findViewById(R.id.venue_list_item_view_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(view.getContext(), VenueDetailsActivity.class);
                    Venue selectedVenue = venuesList.get(position);
                    intent.putExtra("selectedVenue", selectedVenue);
                    intent.putExtra("eventId", eventId);
                    view.getContext().startActivity(intent);
                }
            });

            view.findViewById(R.id.venue_list_item_book_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(view.getContext(), BookVenueActivity.class);
                    intent.putExtra("selectedVenue", venuesList.get(position));
                    intent.putExtra("eventId", eventId);
                    view.getContext().startActivity(intent);
                    Toast.makeText(view.getContext(), "Clicked on book " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void setView(Venue venue) {
            this.venueName.setText(venue.getName());
            this.venueAddress.setText(venue.getAddress());
            this.venueDistance.setText(venue.getDistance());
            String imageUrl = (AppConfig.SERVER_URL + venue.getPicture()).trim();
            Glide.with(this.venueImage.getContext()).load(imageUrl).placeholder(R.drawable.enent_image).into(this.venueImage);
        }
    }

}
