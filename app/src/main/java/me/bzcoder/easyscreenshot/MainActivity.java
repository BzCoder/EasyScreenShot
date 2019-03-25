package me.bzcoder.easyscreenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.bzcoder.libscreenshot.QRCodeUtil;
import me.bzcoder.libscreenshot.ScreenShotUtil;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private ImageView imageView;
    private Button makeButton1,makeButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.imageView);
        makeButton1 = findViewById(R.id.make);
        makeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              makeQrCode();
            }
        });
        makeButton2 = findViewById(R.id.make2);
        makeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeScreenShot();
            }
        });
    }

    private void makeScreenShot() {
        Bitmap bitmap = ScreenShotUtil.activityShot(MainActivity.this);
        imageView.setImageBitmap(ScreenShotUtil.addBlackBoard(bitmap));
    }

    private void makeQrCode() {
        final String filePath = getFileRoot(MainActivity.this) + File.separator
                + "qr_" + System.currentTimeMillis() + ".jpg";
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                boolean success = QRCodeUtil.createQRImage(editText.getText().toString().trim(), 400, 400,
                        true ? bitmap: null,
                        filePath);

                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        }
                    });
                }
            }
        };
        fixedThreadPool.submit(runnable);
    }

    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }
        return context.getFilesDir().getAbsolutePath();
    }
}
