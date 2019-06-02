package com.example.a67024.serviceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;

    private ImageView mImageview;

    private MyService.DownloadBinder mBinder;

    private static final String TAG = "MainActivity";

    private String[] data = {"apple", "orange", "banana", "Cherry"};

    private List<Fruit> fruitList= new ArrayList<>();

    private ListView mFruitListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.small_progressbar);
        mImageview = findViewById(R.id.image_view);
        mImageview.setImageResource(R.mipmap.ic_launcher_round);
        mImageview.setScaleType(ScaleType.FIT_XY);


        initFruitList();
        FruitAdapter adapter = new FruitAdapter(MainActivity.this, R.layout.fruit_layout,
                fruitList);
        mFruitListView = findViewById(R.id.list_view);
        View view = getLayoutInflater().inflate(R.layout.header_layout, null);
        mFruitListView.addHeaderView(view);
        mFruitListView.addFooterView(view);
        mFruitListView.setAdapter(adapter);
        mFruitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "click item = " + position,
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "view = " + view + " view.id=" + view.getId() + ", id=" + id
                + ", parent = " + parent);
            }
        });
        //startDownloadTast();
        //setListItem();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                Toast.makeText(MainActivity.this, "add item click.", Toast.LENGTH_SHORT)
                        .show();

                break;
            case R.id.start_secondActivity:
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivityForResult(intent, 1);
                break;

            case R.id.del_item:
                Toast.makeText(MainActivity.this, "del item click.", Toast.LENGTH_SHORT)
                        .show();
                break;

            case R.id.create_dialog:
                createDialog();
                break;
            case R.id.create_serf_dialog:
                createSelfDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("This is a dialog")
                .setMessage("Something important")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
    }

    private void createSelfDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        //dialog.setView(R.layout.dialog_layout);
        View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView title = view.findViewById(R.id.dialog_title);
        title.setText("Dialog Title");
        dialog.setView(view);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    String responseData = data.getStringExtra("data_result");
                    Log.d(TAG, "receive secondActivity result = " + responseData);
                }
                break;
            default:
                break;
        }
    }

    public void onItemClick(MenuItem item) {
        Toast.makeText(MainActivity.this, "item = " + item.getItemId(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",
                            Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }


    private void setListItem() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            MainActivity.this, android.R.layout.simple_list_item_1, data
        );
        mFruitListView.setAdapter(adapter);
    }

    private void initFruitList() {
        for (int i = 0; i < data.length * 5; i++) {
            fruitList.add(new Fruit("apple", R.mipmap.ic_launcher));
        }
    }

    private void startDownloadTast() {
        new DownloadTast().execute();
    }

    class DownloadTast extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                int downloadPercent = 0;
                while(true) {
                    downloadPercent += 10;
                    Thread.sleep(2000);
                    publishProgress(downloadPercent);
                    if (downloadPercent >= 100) {
                        break;
                    }
                }
            } catch(Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mProgressBar.setVisibility(View.GONE);
            if (aBoolean) {
                Toast.makeText(MainActivity.this, "Download succeeded",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Download failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
