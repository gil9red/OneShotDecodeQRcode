package com.example.ipetrash.oneshotdecodeqrcode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;


public class MainActivity extends ActionBarActivity {
    private ImageView imageView;
    private TextView textView;
    private Button buttonToClipboard;

    private ClipboardManager clipboard;

    private static final int CAMERA_REQUEST = 1888;
    private String TAG = "com.ipetrash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);

        Button buttonPhoto = (Button) findViewById(R.id.buttonPhoto);
        buttonPhoto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Photo click");

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
        );

        buttonToClipboard = (Button) findViewById(R.id.buttonToClipboard);
        buttonToClipboard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "To Clipboard");

                        clipboard.setText(textView.getText());
                        showMessage("Text successfully saved to the clipboard!");
                    }
                }
        );

        Button buttonQuit = (Button) findViewById(R.id.buttonQuit);
        buttonQuit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Quit click");
                        finish();
                    }
                }
        );
    }

    private void showMessage(CharSequence text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            String text = decode(photo);
            Log.d(TAG, text != null ? String.format("decode text = '%s'", text) : "text is null");

            // Показываем кнопку при удачном разборе qr-кода
            buttonToClipboard.setVisibility(text != null ? View.VISIBLE : View.GONE);

            text = text != null ? text : "ERROR: Failed to read the QR code";
            textView.setText(text);
        }
    }

    public String decode(Bitmap image) {
        Log.d(TAG, "decode start");

        int width = image.getWidth(), height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        Log.d(TAG, "pixels=" + pixels);

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();

        try {
            String result = reader.decode(bBitmap).getText();
            Log.d(TAG, "result=" + result);

            return result;

        } catch (NotFoundException e) {
            Log.e(TAG, "decode exception", e);
            return null;
        } finally {
            Log.d(TAG, "decode end");
        }
    }
}
