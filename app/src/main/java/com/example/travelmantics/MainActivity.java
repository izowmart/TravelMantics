package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PICTURE_CODE = 5613;
    private DatabaseReference databaseReference;
    private EditText txtTitle, txtPrice, txtDescription;
    private ImageView imageView;
    private Button btnUpload;
    private TravelDeal travelDeal;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Here we just initialize our singleton class to have everything done for us
        FirebaseDatabase firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;

        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDescription);
        imageView = findViewById(R.id.image_upload);
        progressBar = findViewById(R.id.progress_bar);

        btnUpload = findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert picture"), PICTURE_CODE);

            }
        });

        Intent intent = getIntent();
        TravelDeal deal = intent.getParcelableExtra("traveldeal");
        if (deal == null) {
            deal = new TravelDeal();
        }
        travelDeal = deal;
        txtTitle.setText(deal.getTitle());
        txtPrice.setText(deal.getPrice());
        txtDescription.setText(deal.getDescription());
        showImage(deal.getImageUrl());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            final StorageReference reference = FirebaseUtil.storageReference.child(imageUri.getLastPathSegment());
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressBar.setVisibility(View.GONE);
                            String url = String.valueOf(uri);
                            String pictureName = taskSnapshot.getStorage().getPath();
                            travelDeal.setImageUrl(url);
                            travelDeal.setImageName(pictureName);
                            Log.d(TAG, "onSuccess: URL" + url);
                            Log.d(TAG, "onSuccess: Name" + pictureName);
                            showImage(url);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed getting image url", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enabledTexts(true);
            btnUpload.setEnabled(true);
        } else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enabledTexts(false);
            btnUpload.setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                clean();
                backToListActivity();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                backToListActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void clean() {
        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {
        travelDeal.setTitle(txtTitle.getText().toString());
        travelDeal.setDescription(txtDescription.getText().toString());
        travelDeal.setPrice(txtPrice.getText().toString());
        if (travelDeal.getId() == null) {
            //then this is a new entry
            databaseReference.push().setValue(travelDeal);
        } else {
            databaseReference.child(travelDeal.getId()).setValue(travelDeal);
        }

    }

    public void deleteDeal() {
        if (travelDeal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.child(travelDeal.getId()).removeValue();
        Log.d(TAG, "deleteDeal: Image name" + travelDeal.getImageName());
        
        if(travelDeal.getImageName() !=null && !travelDeal.getImageName().isEmpty()){
            StorageReference picRef = FirebaseUtil.firebaseStorage.getReference().child(travelDeal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: Delete");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Delete");
                }
            });
        }
    }

    public void backToListActivity() {
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        startActivity(intent);
    }

    public void enabledTexts(Boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
    }
}
