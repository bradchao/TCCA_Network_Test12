package tw.org.tcca.apps.test12;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.werb.pickphotoview.PickPhotoView;
import com.werb.pickphotoview.util.PickConfig;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED

        ){
            requestPermissions(new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, 123);

        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){
        img = findViewById(R.id.img);
    }

    public void fetchPhoto(View view) {
        new PickPhotoView.Builder(this)
                .setPickPhotoSize(3)                  // select image size
                .setClickSelectable(false)             // click one image immediately close and return image
                .setShowCamera(true)                  // is show camera
                .setSpanCount(3)                      // span count
                .setLightStatusBar(true)              // lightStatusBar used in Android M or higher
                .setShowGif(false)                    // is show gif
                .start();
    }

    private String base64String = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 21793
                && data != null){
            ArrayList<String> selectPaths =
                    (ArrayList<String>) data.getSerializableExtra(PickConfig.INSTANCE.getINTENT_IMG_LIST_SELECT());
            Bitmap bmp = BitmapFactory.decodeFile(selectPaths.get(0));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            base64String = Base64.encodeToString(byteArray, Base64.DEFAULT);

            img.setImageBitmap(bmp);
            Log.v("bradlog", base64String);
        }
    }

    public void upload(View view) {
        
    }
}