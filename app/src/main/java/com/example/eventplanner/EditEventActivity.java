package com.example.eventplanner;

import static com.example.eventplanner.fragments.HomeFragment.eventList;
import static com.example.eventplanner.fragments.HomeFragment.homeEventsAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.ActivityEditEventBinding;
import com.example.eventplanner.models.Event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEventActivity extends AppCompatActivity {
    private ActivityEditEventBinding binding;
    private static final int PICK_IMAGE_REQUEST = 15;
    private static final int REQUEST_STORAGE_PERMISSION = 1003;
    private Uri imageUri;
    int position;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check for storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }

        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("position"));
        event = eventList.get(position);
        Toast.makeText(this, event.getName(), Toast.LENGTH_SHORT).show();

        setupUI();
        setListeners();
    }

    private void setupUI() {
        binding.eventName.setText(event.getName());
        binding.eventDate.setText(event.getDate());
        binding.eventTime.setText(event.getTime());
        binding.eventBudget.setText(String.valueOf(event.getBudget()));
        binding.eventGuestsNumber.setText(String.valueOf(event.getNoOfGuests()));
        binding.etVenueAbout.setText(event.getAbout());
        String imageUrl = event.getImage() != null ? (AppConfig.SERVER_URL + event.getImage()).trim() : null;
        Glide.with(this).load(imageUrl).placeholder(R.drawable.enent_image).error(R.drawable.enent_image).into(binding.ivSelectedImage);
    }

    private void setListeners() {
        binding.saveEventButton.setOnClickListener(v -> {
            try {
                saveEvent();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        binding.eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        binding.eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        binding.btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Use Glide to load the selected image into the ImageView
            Glide.with(this).load(imageUri).placeholder(R.drawable.event_image_1) // Set placeholder if needed
                    .error(R.drawable.event_image_1).into(binding.ivSelectedImage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            month1 = month1 + 1;
            String date = dayOfMonth + "/" + month1 + "/" + year1;
            binding.eventDate.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String time = hourOfDay + ":" + minute1;
            binding.eventTime.setText(time);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private File getFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("image", ".jpg", getCacheDir());
        OutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outputStream.close();

        return tempFile;
    }


    private void saveEvent() throws IOException {
        binding.saveEventButton.setText("Loading...");
        binding.saveEventButton.setEnabled(false);

        // Get input from EditText fields
        String eventName = binding.eventName.getText().toString().trim();
        String eventDate = binding.eventDate.getText().toString().trim();
        String eventTime = binding.eventTime.getText().toString().trim();
        String eventBudget = binding.eventBudget.getText().toString().trim();
        String eventGuestsNumber = binding.eventGuestsNumber.getText().toString().trim();
        String eventAbout = binding.etVenueAbout.getText().toString().trim();

        if (imageUri != null) {
            File imageFile = getFileFromUri(imageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestFile);

            RequestBody eventNamePart = RequestBody.create(MediaType.parse("multipart/form-data"), eventName);
            RequestBody eventDatePart = RequestBody.create(MediaType.parse("multipart/form-data"), eventDate);
            RequestBody eventTimePart = RequestBody.create(MediaType.parse("multipart/form-data"), eventTime);
            RequestBody eventBudgetPart = RequestBody.create(MediaType.parse("multipart/form-data"), eventBudget);
            RequestBody eventGuestNumberPart = RequestBody.create(MediaType.parse("multipart/form-data"), eventGuestsNumber);
            RequestBody eventAboutPart = RequestBody.create(MediaType.parse("multipart/form-data"), eventAbout);


            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse<String>> call = apiService.updateEvent(String.valueOf(event.getId()), body, eventNamePart, eventDatePart, eventTimePart, eventBudgetPart, eventGuestNumberPart, eventAboutPart);
            call.enqueue(new Callback<ApiResponse<String>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                        String imageUrl = response.body().getData();
                        Toast.makeText(EditEventActivity.this, imageUrl, Toast.LENGTH_SHORT).show();
                        event.setImage(imageUrl);
                        event.setName(eventName);
                        event.setDate(eventDate);
                        event.setTime(eventTime);
                        event.setBudget(Integer.parseInt(eventBudget));
                        event.setNoOfGuests(Integer.parseInt(eventGuestsNumber));
                        event.setAbout(eventAbout);
                        eventList.set(position, event);
                        homeEventsAdapter.notifyDataSetChanged();

                        Intent intent = new Intent(EditEventActivity.this, EventDetailsActivity.class);
                        intent.putExtra("position", position);
                        Toast.makeText(EditEventActivity.this, "Position"+position, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(EditEventActivity.this, "Failed to update event", Toast.LENGTH_SHORT).show();
                        binding.saveEventButton.setText("Update");
                        binding.saveEventButton.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Log.d("Error", "onFailure: " + t.getMessage());
                    Toast.makeText(EditEventActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    binding.saveEventButton.setText("Update");
                    binding.saveEventButton.setEnabled(true);
                }
            });


        } else {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse<String>> call;
            call = apiService.updateEventWithoutImage(String.valueOf(event.getId()), eventName, eventDate, eventTime, eventBudget, eventGuestsNumber, eventAbout);

            call.enqueue(new Callback<ApiResponse<String>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                        assert response.body() != null;
                        String imageUrl = response.body().getData();
                        Toast.makeText(EditEventActivity.this, imageUrl, Toast.LENGTH_SHORT).show();
                        event.setImage(imageUrl);
                        event.setName(eventName);
                        event.setDate(eventDate);
                        event.setTime(eventTime);
                        event.setBudget(Integer.parseInt(eventBudget));
                        event.setNoOfGuests(Integer.parseInt(eventGuestsNumber));
                        event.setAbout(eventAbout);
                        eventList.set(position, event);
                        homeEventsAdapter.notifyDataSetChanged();

                        Intent intent = new Intent(EditEventActivity.this, EventDetailsActivity.class);
                        intent.putExtra("position", position);
                        Toast.makeText(EditEventActivity.this, "Position"+position, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(EditEventActivity.this, "Failed to update event", Toast.LENGTH_SHORT).show();
                        binding.saveEventButton.setText("Update");
                        binding.saveEventButton.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Log.d("inaamilyas", "onFailure: " + t);
                    Toast.makeText(EditEventActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
