package com.example.louisa.savemore;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import Models.SharedCost;
import Utilities.Utility;
import Utilities.RoundUpMethod;
import butterknife.Bind;
import butterknife.ButterKnife;


public class AddSharedCost extends BaseActivity {
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.price)
    EditText price;
    @Bind(R.id.friendsInvolve)
    EditText friendsInvolve;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.btn_save)
    Button btn_save;
    @Bind(R.id.btn_capture)
    ImageButton btn_capture;

    DatabaseReference databaseRef;

    String key;
    String friends;
    String otherFriends [];
    String receipients [];

    String senderEmail;
    String productName;
    String receiverEmail;

    int count = 1;
    float amountToShare;
    float totalAmount;

    //Image capture / upload variables
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private String folder_main = "Receipts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shared_cost);

        ButterKnife.bind(this);

        databaseRef = mDatabase.getReference("sharedCost");
        key = databaseRef.push().getKey();

        setClickEvents();
    }

    //Method on click event
    private void setClickEvents() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSave();
            }
        });

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

    }//End of method on click event

    @Override
    //Method onRequestPermissionResult
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }//End of method onRequestPermissionResult

    //Method selectImage
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddSharedCost.this);
        builder.setTitle("Add Your Receipt!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AddSharedCost.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }//End of method selectImage

    //Method galleryIntent
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }//End of method galleryIntent

    //Method cameraIntent
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }//End of method cameraIntent

    @Override
    //Method onActivityResult
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }//End of method onActivityResult

    //Method onCaptureImageResult
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        String path = Environment.getExternalStorageDirectory().toString();

        String filename = path + "/" + folder_main + "/" + String.format("%d.jpg", System.currentTimeMillis());

        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(filename);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        imageView.setImageBitmap(thumbnail);
        imageView.setVisibility(View.VISIBLE);
    }//End of method onCaptureImageResult

    @SuppressWarnings("deprecation")
    //Method onSelectFromGalleryResult
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imageView.setImageBitmap(bm);
        imageView.setVisibility(View.VISIBLE);
    }//End of method onSelectFromGalleryResult

    //Method to save value to Firebase
    private void processSave() {

        if (validateFields()) {
            senderEmail = mAuth.getCurrentUser().getEmail();
            productName = name.getText().toString();
            receiverEmail = email.getText().toString();
            totalAmount = Float.parseFloat(price.getText().toString());
            friends = friendsInvolve.getText().toString();

            saveCosts();
        }
    }//End of save method

    //Method to sort shared cost value
    private void saveCosts() {
        //Receipient split by ","
        receipients = receiverEmail.split(",");
        otherFriends = friends.split(",");

        //Round up method
        Utilities.RoundUpMethod roundUp = new Utilities.RoundUpMethod();

        //Divide according to the size of people added
        amountToShare = totalAmount / (receipients.length + otherFriends.length);
        amountToShare = roundUp.roundUpMethod(amountToShare, 2, BigDecimal.ROUND_HALF_UP);

        SharedCost sharedCost = new SharedCost();
        sharedCost.setEmail(receiverEmail);
        sharedCost.setName(productName);
        sharedCost.setPrice(amountToShare);
        sharedCost.setSender_email(senderEmail);
        sharedCost.setTotal_amount(totalAmount);
        sharedCost.setFriend_involve(friends);

        senderEmail = cleanEmail(senderEmail);

        Map<String, Object> shareValues = sharedCost.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, shareValues);

        //Save into Firebase (Update)
        databaseRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Cost Shared", Toast.LENGTH_LONG).show();
                    databaseRef.child(key).child(senderEmail).setValue(true);

                    for (String receipient : receipients) {
                        receiverEmail = cleanEmail(receipient);
                        databaseRef.child(key).child(receiverEmail).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (count == receipients.length) {
                                }
                            }
                        });
                        count++;
                        finish();
                    }

                    for (final String otherFriend : otherFriends) {
                        databaseRef.child(key).child(friends).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (count == otherFriends.length) {

                                }

                            }
                        });
                        count++;
                        finish();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to save at the moment", Toast.LENGTH_LONG).show();
                }
            }
        });//End of update
        databaseRef.setPriority(ServerValue.TIMESTAMP);
    }//End of sort shared cost value

    //Method to validate user make sure user fill in the fields
    private boolean validateFields() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("Please enter a name");
            return false;
        } else if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Please enter a valid email address");
            return false;
        } else if (TextUtils.isEmpty(price.getText().toString())) {
            price.setError("Please enter a valid price");
            return false;
        } else if (TextUtils.isEmpty(friendsInvolve.getText().toString())){
            friendsInvolve.setError("Remember to enter Your Own Name");
            return false;
        } else {
            return true;
        }
    }//End of validation method
}