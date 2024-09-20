package com.example.eventplanner.VenueManager;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.models.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMenuItemDialogFragment extends DialogFragment {

    private OnMenuItemUpdatedListener menuItemUpdateListener;
    private int position; // Store the position of the item

    // Set listener from the adapter
    public void setMenuItemUpdateListener(OnMenuItemUpdatedListener listener, int position) {
        this.menuItemUpdateListener = listener;
        this.position = position;  // Capture the position of the item in the list
    }


    private MenuItem menuItem;
    private static final int PICK_IMAGE_REQUEST = 12;
    private Uri imageUri;

    private EditText itemNameEditText;
    private EditText itemPriceEditText;
    private ImageView imageView;

    public static EditMenuItemDialogFragment newInstance(MenuItem menuItem) {
        EditMenuItemDialogFragment fragment = new EditMenuItemDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("menuItem", menuItem);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout for the dialog
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_edit_menu_item_dialog, null);

        // Get the menu item from arguments
        if (getArguments() != null) {
            menuItem = (MenuItem) getArguments().getSerializable("menuItem");
        }

        // Initialize views
        itemNameEditText = view.findViewById(R.id.et_item_name);
        itemPriceEditText = view.findViewById(R.id.et_menu_price);
        imageView = view.findViewById(R.id.update_iv_selected_image);

        // Set existing values
        if (menuItem != null) {
            itemNameEditText.setText(menuItem.getName());
            itemPriceEditText.setText(String.valueOf(menuItem.getPrice()));
            // Set the image using Glide or any other image loading library
            String imageUrl = (AppConfig.SERVER_URL + menuItem.getImageUrl()).trim();
            Glide.with(view.getContext()).load(imageUrl).placeholder(R.drawable.event_image_1).into(imageView);
        }

        view.findViewById(R.id.btn_select_image).setOnClickListener(v -> openImagePicker());

        // Create the dialog using AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view).setPositiveButton("Update", (dialog, which) -> updateMenuItem())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private boolean validateInput(String name, String price) {
        // Implement validation logic
        return !name.isEmpty() && !price.isEmpty();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void updateMenuItem() {
        // Get the updated values
        String newName = itemNameEditText.getText().toString().trim();
        String newPrice = itemPriceEditText.getText().toString().trim();

        // Validate input
        if (validateInput(newName, newPrice)) {
            // Convert image URI to File
            MultipartBody.Part imagePart = null;
            if (imageUri != null) {
                try {
                    File imageFile = getFileFromUri(imageUri);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                    imagePart = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Create request bodies
            RequestBody namePart = RequestBody.create(MediaType.parse("multipart/form-data"), newName);
            RequestBody pricePart = RequestBody.create(MediaType.parse("multipart/form-data"), newPrice);

            // Call the API
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse<MenuItem>> call = apiService.updateMenuItems(String.valueOf(menuItem.getId()), imagePart, namePart, pricePart);
            call.enqueue(new Callback<ApiResponse<MenuItem>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<MenuItem>> call, @NonNull Response<ApiResponse<MenuItem>> response) {
                    if (getContext() != null) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(getContext(), "Menu item updated successfully", Toast.LENGTH_SHORT).show();
                            MenuItem updatedItem = response.body().getData();

                            // Notify the listener that the item was updated
                            if (menuItemUpdateListener != null) {
                                menuItemUpdateListener.onMenuItemUpdated(updatedItem, position);
                            }
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Failed to update menu item", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<MenuItem>> call, @NonNull Throwable t) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("image", ".jpg", requireActivity().getCacheDir());
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
}
