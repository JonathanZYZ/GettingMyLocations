package com.myapplicationdev.android.gettingmylocations;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    ListView lv;
    Button btnRefresh, btnFav;
    ArrayList<String> al;
    ArrayAdapter<String> adapter;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        lv = findViewById(R.id.lv);
        textView2 = findViewById(R.id.textView2);
        al = new ArrayList<>();

//        al.add("TEST");
//        al.add("TEST1");
//        al.add("TEST2");
//        al.add("TEST3");

        btnFav = findViewById(R.id.btnFav);
        btnRefresh = findViewById(R.id.btnRefresh);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al);
        lv.setAdapter(adapter);
        check("data.txt");

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al.clear();
                check("data.txt");
            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al.clear();
                check("favorites.txt");
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity2.this);
                dialog.setMessage("Add this location in your favourite list?");
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String folderLocation =
                                    Environment.getExternalStorageDirectory()
                                            .getAbsolutePath() + "/MyFolder";
                            File folder = new File(folderLocation);
                            if (folder.exists() == false) {
                                boolean result = folder.mkdir();
                                if (result == true) {
                                    Log.d("File Read/Write", "Folder created");
                                }
                            }
                            try {
                                folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
                                File targetFile = new File(folderLocation, "favorites.txt");
                                FileWriter writer = new FileWriter(targetFile, true);
                                writer.write(""+al.get(position)+"\n");
                                writer.flush();
                                writer.close();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity2.this, "Failed to write!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity2.this, "Failed to write!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }
    private void check(String text){
        String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
        File targetFile = new File(folderLocation, text);
        if (targetFile.exists() == true){
            String data ="";
            try {
                FileReader reader = new FileReader(targetFile);
                BufferedReader br = new BufferedReader(reader);
                String line = br.readLine();
                while (line != null){
                    data += line + "\n";
                    al.add(line);
                    line = br.readLine();
                    //al.add(line);
                }
                textView2.setText(" Number of records: "+al.size());
                br.close();
                reader.close();
            } catch (Exception e) {
                Toast.makeText(MainActivity2.this, "Failed to read!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            Log.d("Content", data);
            adapter.notifyDataSetChanged();
        }
    }
}