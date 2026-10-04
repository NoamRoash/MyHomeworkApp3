package com.example.homeworkplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {

    private static final String ALL = "הכל";

    private TextView tvHello, tvStats, tvEmpty;
    private Spinner spFilter;
    private ListView lvTasks;
    private Button btnAdd, btnLogout;

    private TaskStorage storage;
    private ArrayList<Task> shownTasks = new ArrayList<>();
    private ArrayAdapter<Task> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        tvHello = findViewById(R.id.tvHello);
        tvStats = findViewById(R.id.tvStats);
        tvEmpty = findViewById(R.id.tvEmpty);
        spFilter = findViewById(R.id.spFilter);
        lvTasks = findViewById(R.id.lvTasks);
        btnAdd = findViewById(R.id.btnAdd);
        btnLogout = findViewById(R.id.btnLogout);

        storage = new TaskStorage(this);

        SharedPreferences prefs = getSharedPreferences(TaskStorage.PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString(TaskStorage.KEY_NAME, "");
        tvHello.setText("שלום " + name + "!");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shownTasks);
        lvTasks.setAdapter(adapter);

        ArrayList<String> filterItems = new ArrayList<>();
        filterItems.add(ALL);
        for (String subject : Task.SUBJECTS) {
            filterItems.add(subject);
        }
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, filterItems);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdapter);

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = shownTasks.get(position);
                Intent intent = new Intent(TasksActivity.this, TaskDetailsActivity.class);
                intent.putExtra(TaskDetailsActivity.EXTRA_TASK_ID, task.getId());
                startActivity(intent);
            }
        });

        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                confirmDelete(shownTasks.get(position));
                return true;
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TasksActivity.this, AddTaskActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        ArrayList<Task> all = storage.loadAll();

        int doneCount = 0;
        int points = 0;
        for (Task t : all) {
            if (t.isDone()) {
                doneCount++;
                points += t.getPoints();
            }
        }
        tvStats.setText("משימות: " + all.size() + " | הושלמו: " + doneCount + " | נקודות: " + points);

        Object selected = spFilter.getSelectedItem();
        String filter = selected == null ? ALL : selected.toString();

        shownTasks.clear();
        for (Task t : all) {
            if (filter.equals(ALL) || t.getSubject().equals(filter)) {
                shownTasks.add(t);
            }
        }
        adapter.notifyDataSetChanged();

        if (shownTasks.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void confirmDelete(final Task task) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת משימה")
                .setMessage("למחוק את \"" + task.getTitle() + "\"?")
                .setPositiveButton("מחק", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storage.deleteById(task.getId());
                        Toast.makeText(TasksActivity.this, "המשימה נמחקה", Toast.LENGTH_SHORT).show();
                        refreshList();
                    }
                })
                .setNegativeButton("ביטול", null)
                .show();
    }
}
