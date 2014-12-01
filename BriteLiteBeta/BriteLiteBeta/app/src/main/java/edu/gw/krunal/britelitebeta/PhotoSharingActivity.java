package edu.gw.krunal.britelitebeta;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


public class PhotoSharingActivity extends Activity {
    //to identify request when getting result back.
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 44;
    private Uri mFileUri;
    private ImageView mImageView;
    private String mImageFilePath="";
    private Boolean imageClicked = false;
    private String IMAGE_FILE_PATH_KEY="image_file_path_key";
    private String IMAGE_CLICKED_FLAG_KEY = "image-clicked_flag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_sharing);
        mImageView = (ImageView) findViewById(R.id.imageView);

        if(savedInstanceState!=null){
            mImageFilePath=savedInstanceState.getString(IMAGE_FILE_PATH_KEY);
            imageClicked=savedInstanceState.getBoolean(IMAGE_CLICKED_FLAG_KEY);
        }

        if(imageClicked)
        {
            //render image if already taken.
            File imagesFolder = new File(Environment.getExternalStorageDirectory(),"Camera");
            //create image file reference
            File imageFile = new File(imagesFolder,"image.jpg");
            mFileUri = Uri.fromFile(imageFile);
            setImageView(mFileUri.getPath());
        }
    }

    public void share(View v){
        if(!imageClicked) {
            Toast.makeText(PhotoSharingActivity.this, getResources().getString(R.string.photo_not_found_error), Toast.LENGTH_LONG).show();
            return;
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,mFileUri);
        shareIntent.setType("image/*");
        // Add data to the intent, the receiving app will decide what to do with it.
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.photo_subject_line));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.photo_body_line));
        startActivity(shareIntent);
    }

    public void takePicture(View v){
        //create folder to store image
        File imagesFolder = new File(Environment.getExternalStorageDirectory(),"Camera");
        imagesFolder.mkdirs();

        //create image file reference
        File imageFile = new File(imagesFolder,"image.jpg");

        mFileUri = Uri.fromFile(imageFile);

        //create intent and launch camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,mFileUri);

        startActivityForResult(intent,CAPTURE_IMAGE_REQUEST_CODE);
    }

    //handling rotating screen.
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(IMAGE_FILE_PATH_KEY ,mImageFilePath);
        savedInstanceState.putBoolean(IMAGE_CLICKED_FLAG_KEY,imageClicked );
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        if(requestCode == CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            //saving flag that image is clicked.
            imageClicked=true;
            mImageFilePath = mFileUri.getPath();
            setImageView(mImageFilePath);

        }
    }
    private void setImageView(String filePath)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        mImageView.setImageBitmap(bitmap);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo_sharing, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_back) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
