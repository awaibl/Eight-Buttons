package com.example.eightpuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "MainActivity";
    protected ImageButton[] buttons = new ImageButton[3]; //dann 9

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttons[0] = findViewById(R.id.image_3);
        buttons[1] = findViewById(R.id.image_4);
        buttons[2] = findViewById(R.id.image_5);

        prepareGrid();

    }

    public void openCamera(View view){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void prepareGrid() {
        File picture = new File(getFilesDir(), "my_picture,jpg");

        if(picture.isFile()){
            try {

                Bitmap source = BitmapFactory.decodeStream(new FileInputStream(picture));
                Bitmap[] grid = cut(crop(source));

                buttons[0].setImageBitmap(grid[3]);
                buttons[1].setImageBitmap(grid[4]);
                buttons[2].setImageBitmap(grid[5]);

            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    public Bitmap[] cut(Bitmap source){

        Bitmap[] grid = new Bitmap[9];

        int size = source.getHeight()/3;

        int i = 0;

        for(int row = 0; row < 3; row++){

            for(int col = 0; col < 3; col++){

                grid[i] = Bitmap.createBitmap(source, col * size, row * size,size, size);
                i++;

            }
        }

        return grid;

    }

    public Bitmap crop(Bitmap source){
        int x = 0;
        int y = 0;
        int size;

        Matrix matrix = new Matrix();
        matrix.postRotate(90); //Bild drehen

        if(source.getHeight() > source.getWidth()){ //portrait

            size = source.getWidth();
            y = (source.getHeight() - size) / 2;

        }else{ //landscape

            size = source.getHeight();
            x = (source.getWidth() - size) / 2;
        }

        return Bitmap.createBitmap(source, x, y, size, size, matrix, true);

    }

}