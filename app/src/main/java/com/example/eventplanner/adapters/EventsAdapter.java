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
import com.example.eventplanner.EventDetailsActivity;
import com.example.eventplanner.R;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.models.Event;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    ArrayList<Event> events;

    public EventsAdapter(ArrayList<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.setView(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView eventAddress;
        TextView eventDate;
        ImageView eventImage;

        public ViewHolder(View view) {
            super(view);
            this.eventName = (TextView) view.findViewById(R.id.tv_event_name);

            this.eventAddress = (TextView) view.findViewById(R.id.tv_event_address);

            this.eventDate = (TextView) view.findViewById(R.id.tv_event_date);

            this.eventImage = (ImageView) view.findViewById(R.id.imageview_event);


            // Define click listener for the ViewHolder's View
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle click event here
                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        Event event = events[position];
                    Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
                    intent.putExtra("eventId", position);
                    view.getContext().startActivity(intent);
                    Toast.makeText(view.getContext(), "click on item " + position, Toast.LENGTH_SHORT).show();
                    // Do something with the clicked event
//                    }
                }
            });
        }

        public void setView(Event event) {
            this.eventName.setText(event.getName());
            this.eventAddress.setText(event.getAddress());
            this.eventDate.setText(event.getDate());
            String imageUrl = (AppConfig.SERVER_URL + event.getImage()).trim();
            Glide.with(this.eventImage.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.enent_image)
                    .into(this.eventImage);
//           Glide.with(this.eventImage.getContext()).load("https://4723-182-183-12-134.ngrok-free.app/events/1724264306923.jpg").placeholder(R.drawable.enent_image).into(this.eventImage);
//            Glide.with(this.eventImage.getContext()).load(AppConfig.SERVER_URL + event.getImage()).placeholder(R.drawable.enent_image).into(this.eventImage);
//            this.eventImage.setImageResource(R.drawable.enent_image);
        }
    }

}
