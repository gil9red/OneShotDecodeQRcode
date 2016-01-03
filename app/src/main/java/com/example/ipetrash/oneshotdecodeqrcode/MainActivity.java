package com.example.ipetrash.oneshotdecodeqrcode;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageView;

import android.view.View;
import android.widget.TextView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;


public class MainActivity extends ActionBarActivity {

    public Camera camera;
    public SurfaceHolder surfaceHolder;

    public boolean cameraOn = false;

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imageView = (ImageView) this.findViewById(R.id.imageView);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            String text = decode(photo);

            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(text != null ? text : "ERROR: Не получилось разобрать QR code");

//            photo.recycle();
//            photo = null;
        }
    }

    public void doDecode(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public String decode(Bitmap image) {
        int width = image.getWidth(), height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);

        Log.d("qrcode", "pixels=" + pixels);

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);

        Log.d("qrcode", "source=" + source);

        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));

        Log.d("qrcode", "bBitmap=" + bBitmap);

        MultiFormatReader reader = new MultiFormatReader();

        Log.d("qrcode", "reader=" + reader);

        try
        {
            Result result = reader.decode(bBitmap);
            Log.d("qrcode", "result=" + result);
            return result.getText();
        }
        catch (NotFoundException e)
        {
            Log.e("qrcode", "decode exception", e);
            return null;
        }
    }

    public void quit(View view) {
        finish();
    }
}
