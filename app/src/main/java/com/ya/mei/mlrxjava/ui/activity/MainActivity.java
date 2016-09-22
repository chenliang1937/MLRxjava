package com.ya.mei.mlrxjava.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ya.mei.mlrxjava.R;
import com.ya.mei.mlrxjava.ui.adapter.MainRecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private int[] titles = {R.string.btn_demo_schedulers, R.string.btn_demo_buffer, R.string.btn_demo_debounce, R.string.btn_demo_retrofit, R.string.btn_demo_double_binding_textview,
        R.string.btn_demo_rxbus, R.string.btn_demo_form_validation_combinel,
        R.string.btn_demo_pseudocache, R.string.btn_demo_pseudocache2, R.string.btn_demo_timing,
        R.string.btn_demo_exponential_backoff, R.string.btn_demo_rotation_persist,  R.string.btn_demo_polling, R.string.btn_demo_click,
        R.string.btn_demo_reactive_UI};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        MainRecyclerAdapter adapter = new MainRecyclerAdapter(this, titles);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MainRecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, ShellActivity.class);
                intent.putExtra("Config", position);
                startActivity(intent);
//                Snackbar.make(view, ""+position, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
