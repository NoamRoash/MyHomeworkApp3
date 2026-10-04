package com.example.homeworkplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private Button btnEnter, btnReset;
    private TextView tvWelcome;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        btnEnter = findViewById(R.id.btnEnter);
        btnReset = findViewById(R.id.btnReset);
        tvWelcome = findViewById(R.id.tvWelcome);
        prefs = getSharedPreferences(TaskStorage.PREFS_NAME, MODE_PRIVATE);

        String savedName = prefs.getString(TaskStorage.KEY_NAME, "");
        if (!savedName.isEmpty()) {
            tvWelcome.setText("ברוך שובך, " + savedName);
            tvWelcome.setVisibility(View.VISIBLE);
            etName.setText(savedName);
        }

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmReset();
            }
        });
    }

    private void enter() {
        String name = etName.getText().toString().trim();
        if (name.length() < 2) {
            Toast.makeText(this, "השם חייב להכיל לפחות 2 תווים", Toast.LENGTH_SHORT).show();
            return;
        }
        prefs.edit().putString(TaskStorage.KEY_NAME, name).apply();
        tvWelcome.setText("ברוך שובך, " + name);
        tvWelcome.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, TasksActivity.class);
        startActivity(intent);
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
                .setTitle("איפוס כל הנתונים")
                .setMessage("כל המשימות והשם יימחקו. להמשיך?")
                .setPositiveButton("כן, למחוק הכול", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefs.edit().clear().apply();
                        etName.setText("");
                        tvWelcome.setText("");
                        tvWelcome.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "כל הנתונים נמחקו", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("ביטול", null)
                .show();
    }
}
