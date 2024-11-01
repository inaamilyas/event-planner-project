package com.example.eventplanner.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.models.Feedback;
import com.example.eventplanner.models.MenuItem;

import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    ArrayList<Feedback> feedbacks;


    // Constructor to set the listener
    public FeedbackAdapter(ArrayList<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @NonNull
    @Override
    public FeedbackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_feeback_item, parent, false);
        return new FeedbackAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackAdapter.ViewHolder holder, int position) {
        Feedback feedback = feedbacks.get(position);
        holder.setView(feedback);
    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView feedbackText;
        private TextView userName;
        private ImageView profileImage;

        public ViewHolder(View view) {
            super(view);

            // Initialize views
            feedbackText = view.findViewById(R.id.menu_name);
            userName = view.findViewById(R.id.user_name);
            profileImage = view.findViewById(R.id.profile_image);
        }

        @SuppressLint("SetTextI18n")
        public void setView(Feedback feedback) {
            // Set feedback text
            feedbackText.setText(feedback.getFeedback());

            // Set user name
            userName.setText(feedback.getUsername());

            // Load user profile image, for example with Glide
            Glide.with(itemView.getContext())
                    .load(feedback.getProfile_picture())
                    .placeholder(R.drawable.enent_image) // Optional placeholder
                    .into(profileImage);
        }
    }

}
