package com.example.nemo1.saf;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int NEW_REQUEST_CODE = 40;
    private static final int OPEN_REQUEST_CODE = 41;
    private static final int SAVE_REQUEST_CODE = 42;
    private Intent intent;
    private Uri uri;
    @BindView(R.id.new_button) Button create;
    @BindView(R.id.open_button) Button open;
    @BindView(R.id.save_button) Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initEvent();
    }

    public void initEvent(){
        create.setOnClickListener(this);
        open.setOnClickListener(this);
        save.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(create.getId() == v.getId()){
            newFile();
        }
        if(open.getId() == v.getId()){
            openFile();
        }
        if(save.getId() == v.getId()){
            saveFile();
        }
    }

    //Saving to a Storage File
    private void saveFile() {
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent,SAVE_REQUEST_CODE);
    }

    //Opening and Reading a Storage File
    private void openFile() {
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent,OPEN_REQUEST_CODE);
    }

    //Creating a New Storage File
    public void newFile(){
        intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE,"new file.txt");
        startActivityForResult(intent,NEW_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == NEW_REQUEST_CODE){
                if(data != null){
                    Toast.makeText(this,"Created",Toast.LENGTH_SHORT).show();
                }
            }
            else if (requestCode == OPEN_REQUEST_CODE){
                if(data != null){
                    uri = data.getData();
                    try {
                        String a = readFileContent(uri);
                        Toast.makeText(this,a,Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (requestCode == SAVE_REQUEST_CODE){
                if(data != null){
                    uri = data.getData();
                    try {
                        writeFileContent(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void writeFileContent(Uri uri) throws FileNotFoundException {
        ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri,"w");
        FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());

        try {
            fileOutputStream.write("Hello Word".getBytes());
            fileOutputStream.close();
            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFileContent(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentline;

        while ((currentline = bufferedReader.readLine()) != null ){
            stringBuilder.append(currentline+ "\n");
        }
        inputStream.close();
        return stringBuilder.toString();
    }
}
