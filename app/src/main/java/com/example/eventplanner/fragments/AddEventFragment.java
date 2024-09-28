package com.example.eventplanner.fragments;

import static com.example.eventplanner.fragments.HomeFragment.eventList;
import static com.example.eventplanner.fragments.HomeFragment.user;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventplanner.R;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.FragmentAddEventBinding;
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

public class AddEventFragment extends Fragment {
    private FragmentAddEventBinding binding;

    private static final int PICK_IMAGE_REQUEST = 12;
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private Uri imageUri;


    public AddEventFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentAddEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check for storage permissions
        if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }

        binding.saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveEvent();
                    binding.saveEventButton.setText("Add Event");
                    binding.saveEventButton.isEnabled();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        binding.eventDate.setOnClickListener(v -> showDatePickerDialog());
        binding.eventTime.setOnClickListener(v -> showTimePickerDialog());

        binding.btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.ivSelectedImage.setImageURI(imageUri);
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
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            String time = hourOfDay + ":" + minute1;
            binding.eventTime.setText(time);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private File getFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("image", ".jpg", requireContext().getCacheDir());
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
        Toast.makeText(getContext(), "Saving event", Toast.LENGTH_SHORT).show();
        binding.saveEventButton.setText("Loading...");
//        binding.saveEventButton.d(false);

        // Convert URI to File and proceed with the API call
        File imageFile = getFileFromUri(imageUri);

        // Get input from EditText fields
        String eventName = binding.eventName.getText().toString().trim();
        String eventDate = binding.eventDate.getText().toString().trim();
        String eventTime = binding.eventTime.getText().toString().trim();
        String eventAbout = binding.etVenueAbout.getText().toString().trim();


        // Create RequestBody for the image file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);

        // Create MultipartBody.Part using file request-body, file name, and part name
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestFile);

        // Create other request bodies
        RequestBody eventNamePart = RequestBody.create(MediaType.parse("multipart/form-data"), eventName);
        RequestBody eventDatePart = RequestBody.create(MediaType.parse("multipart/form-data"), eventDate);
        RequestBody eventTimePart = RequestBody.create(MediaType.parse("multipart/form-data"), eventTime);
       RequestBody eventAboutPart = RequestBody.create(MediaType.parse("multipart/form-data"), eventAbout);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Event>> call = apiService.addEvent(user.getId(),body, eventNamePart, eventDatePart, eventTimePart,eventAboutPart);
        call.enqueue(new Callback<ApiResponse<Event>>() {
            @Override
            public void onResponse(Call<ApiResponse<Event>> call, Response<ApiResponse<Event>> response) {
                // Handle success
                if (response.isSuccessful()) {

                    Event event = response.body().getData();
                    eventList.add(event);

                    Toast.makeText(getContext(), "Event created successfully", Toast.LENGTH_SHORT).show();
                    VenuesFragment venuesFragment = new VenuesFragment();

                    // Create a bundle to pass the data
                    Bundle bundle = new Bundle();
                    bundle.putInt("eventId", event.getId());
                    venuesFragment.setArguments(bundle);

                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, venuesFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    // Handle error
                    Toast.makeText(getContext(), "Failed to create venue", Toast.LENGTH_SHORT).show();
                    binding.saveEventButton.setText("Add Event");
                    binding.saveEventButton.isEnabled();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Event>> call, Throwable t) {
                // Handle failure
                Log.d("inaamilyas", "onFailure: " + t);
                Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                binding.saveEventButton.setText("Add Event");
                binding.saveEventButton.isEnabled();
            }
        });

    }

}
