package com.example.eventplanner.VenueManager.MenuItem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.VenueManager.EditMenuItemDialogFragment;
import com.example.eventplanner.VenueManager.OnMenuItemUpdatedListener;
import com.example.eventplanner.VenueManager.adapter.MenuItemAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityAddMenuBinding;
import com.example.eventplanner.models.MenuItem;
import com.example.eventplanner.models.Venue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMenuActivity extends AppCompatActivity implements OnMenuItemUpdatedListener {

    private ActivityAddMenuBinding binding;
    private ArrayList<MenuItem> menuItemsList = new ArrayList<>();
    MenuItemAdapter menuItemAdapter;

    private static final int PICK_IMAGE_REQUEST = 12;
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setSupportActionBar(binding.myToolbar);

        Venue selectedVenue = (Venue) getIntent().getSerializableExtra("selectedVenue");
        assert selectedVenue != null;
        menuItemsList = (ArrayList<MenuItem>) selectedVenue.getFoodMenuItems();
        binding.menuItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        menuItemAdapter = new MenuItemAdapter(menuItemsList);
        binding.menuItemsRecyclerView.setAdapter(menuItemAdapter);

        // Call the method to set the RecyclerView height
        setRecyclerViewHeight(binding.menuItemsRecyclerView);

        // Check for storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }

        binding.btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        binding.saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddMenuActivity.this, "save menu item", Toast.LENGTH_SHORT).show();


                // Convert URI to File and proceed with the API call
                File imageFile = null;
                try {
                    imageFile = getFileFromUri(imageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Get input from EditText fields
                String menuItemName = binding.etItemName.getText().toString().trim();
                String menuItemPrice = binding.etMenuPrice.getText().toString().trim();

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestFile);

                RequestBody menuItemNamePart = RequestBody.create(MediaType.parse("multipart/form-data"), menuItemName);
                RequestBody menuItemPricePart = RequestBody.create(MediaType.parse("multipart/form-data"), menuItemPrice);

                String venueId = String.valueOf(selectedVenue.getId());
                ApiService apiService = ApiClient.getClient().create(ApiService.class);

                Call<ApiResponse<MenuItem>> call = apiService.addMenuItems(venueId, body, menuItemNamePart, menuItemPricePart);

                call.enqueue(new Callback<ApiResponse<MenuItem>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<ApiResponse<MenuItem>> call, Response<ApiResponse<MenuItem>> response) {
                        // Handle success
                        if (response.isSuccessful()) {
                            // Do something with the response
                            Toast.makeText(AddMenuActivity.this, "Menu added successfully", Toast.LENGTH_SHORT).show();
                            assert response.body() != null;
                            MenuItem menuItem = response.body().getData();
                            menuItemsList.add(menuItem);

                            // Notify the adapter that data has changed
                            menuItemAdapter.notifyItemInserted(menuItemsList.size() - 1);

                            // Optionally, scroll to the newly added item
                            binding.menuItemsRecyclerView.scrollToPosition(menuItemsList.size() - 1);
//                            menuItemAdapter.notifyDataSetChanged();
//
                        } else {
                            // Handle error
                            Toast.makeText(AddMenuActivity.this, "Failed to add venue", Toast.LENGTH_SHORT).show();
//                            binding.saveVenueButton.setText("Add Venue");
//                            binding.saveVenueButton.isEnabled();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MenuItem>> call, Throwable t) {
                        // Handle failur
                        Toast.makeText(AddMenuActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
//                        binding.saveVenueButton.setText("Add Venue");
//                        binding.saveVenueButton.isEnabled();
                    }
                });

            }
        });

    }

    private void setRecyclerViewHeight(RecyclerView recyclerView) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter != null) {
            int totalHeight = 0;
            int itemCount = adapter.getItemCount();
            if (itemCount > 0) {
                // Create a view for measuring item height
                View view = adapter.createViewHolder(recyclerView, adapter.getItemViewType(0)).itemView;
                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int itemHeight = view.getMeasuredHeight();
                // Calculate total height by multiplying item height with item count
                totalHeight = (itemHeight * itemCount) + 200;
            }
            // Set the calculated height to RecyclerView
            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
            params.height = totalHeight;
            recyclerView.setLayoutParams(params);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.ivSelectedImage.setImageURI(imageUri);
        }
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


    @Override
    public void onMenuItemUpdated(MenuItem updatedMenuItem, int position) {
        Log.d("inaamilyas", "onMenuItemUpdated: loop ");
        // Update the item in the list
        for (int i = 0; i < menuItemsList.size(); i++) {
            if (menuItemsList.get(i).getId() == updatedMenuItem.getId()) {
                Log.d("inaamilyas", "onMenuItemUpdated: loop " + updatedMenuItem.getName());
                menuItemsList.set(i, updatedMenuItem);
                menuItemAdapter.notifyItemChanged(i); // Notify the adapter of the change
                break;
            }
        }
    }

//    private void showEditMenuItemDialog(MenuItem menuItem) {
//        EditMenuItemDialogFragment dialog = EditMenuItemDialogFragment.newInstance(menuItem);
//        dialog.setOnMenuItemUpdatedListener(this);  // Set the listener
//        dialog.show(dialog.getChildFragmentManager(), "EditMenuItemDialog");
//    }
}