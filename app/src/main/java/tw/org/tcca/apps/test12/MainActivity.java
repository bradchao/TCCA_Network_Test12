package tw.org.tcca.apps.test12;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.werb.pickphotoview.PickPhotoView;
import com.werb.pickphotoview.util.PickConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    private MainApp mainApp;

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

    private ProgressDialog progressDialog;

    private void init(){
        mainApp = (MainApp)getApplication();
        img = findViewById(R.id.img);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Waiting......");
        progressDialog.setCancelable(false);

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
    private String uploadFile;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 21793
                && data != null){
            ArrayList<String> selectPaths =
                    (ArrayList<String>) data.getSerializableExtra(PickConfig.INSTANCE.getINTENT_IMG_LIST_SELECT());
            uploadFile = selectPaths.get(0);
            Bitmap bmp = BitmapFactory.decodeFile(selectPaths.get(0));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            base64String = Base64.encodeToString(byteArray, Base64.DEFAULT);

            img.setImageBitmap(bmp);
            //Log.v("bradlog", base64String);
        }
    }

    public void upload(View view) {
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                "http://10.0.100.191/brad03.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.v("bradlog", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.v("bradlog", error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("account","brad" + (int)(Math.random()*100000));
                map.put("passwd","123");
                map.put("realname","123");
                map.put("img",base64String);
                return map;
            }



        };
        mainApp.queue.add(request);
    }

    public void uploadFile(View view) {
        new Thread(){
            @Override
            public void run() {
                upload();
            }
        }.start();


    }

    private void upload(){
        try {
            MultipartUtil mu = new MultipartUtil("http://10.0.100.191/brad04.php");
            mu.readyToConnect();
            mu.addFilePart("upload", new File(uploadFile));
            List<String> ret = mu.finish();
            for (String line : ret){
                Log.v("bradlog", "ret => " + line);
            }
        }catch (Exception e){
            Log.v("bradlog", e.toString());
        }
    }

}