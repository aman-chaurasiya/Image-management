package com.example.activityresultlauncher;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activityresultlauncher.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ActivityResultLauncher<Intent> cameraResultLauncher;

    ActivityResultLauncher<Intent> gallaryResultLauncher;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bundle bundle = result.getData().getExtras();
                            bitmap = (Bitmap) bundle.get("data");
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                            String path = MediaStore.Images.Media.insertImage(getApplicationContext().
                                    getContentResolver(), bitmap, "Camera Image", null);

                            Uri cameraImageUri = Uri.parse(path);
                            binding.ivImage.setImageURI(cameraImageUri);
                        } else {
                            Toast.makeText(MainActivity.this, "No Image Captured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        gallaryResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getData() != null) {

                            Uri imageUri = result.getData().getData();
                            binding.ivImage.setImageURI(imageUri);
                        } else {
                            Toast.makeText(MainActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        binding.btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog, null);
                builder.setCancelable(true);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();

                alertDialog = builder.show();

                TextView tvCamera = dialogView.findViewById(R.id.tvCamera);
                TextView tvGallery = dialogView.findViewById(R.id.tvGallery);
                AlertDialog finalAlertDialog = alertDialog;
                tvCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            cameraResultLauncher.launch(intent);
                            finalAlertDialog.cancel();

                        } else {
                            Toast.makeText(MainActivity.this, "NO data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            gallaryResultLauncher.launch(intent);
                            finalAlertDialog.cancel();

                        } else {
                            Toast.makeText(MainActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}