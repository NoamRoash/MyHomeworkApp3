package com.example.homeworkplanner;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "task_id";

    private TextView tvTitle, tvType, tvSubject, tvPriority, tvDueDate, tvAmount, tvStatus, tvPoints;
    private Button btnToggleDone, btnDelete, btnBack;

    private TaskStorage storage;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        tvTitle = findViewById(R.id.tvTitle);
        tvType = findViewById(R.id.tvType);
        tvSubject = findViewById(R.id.tvSubject);
        tvPriority = findViewById(R.id.tvPriority);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvAmount = findViewById(R.id.tvAmount);
        tvStatus = findViewById(R.id.tvStatus);
        tvPoints = findViewById(R.id.tvPoints);
        btnToggleDone = findViewById(R.id.btnToggleDone);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        storage = new TaskStorage(this);
        int id = getIntent().getIntExtra(EXTRA_TASK_ID, -1);
        task = storage.findById(id);
        if (task == null) {
            Toast.makeText(this, "המשימה לא נמצאה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showDetails();

        btnToggleDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setDone(!task.isDone());
                storage.updateTask(task);
                if (task.isDone()) {
                    Toast.makeText(TaskDetailsActivity.this,
                            "קיבלת " + task.getPoints() + " נקודות!", Toast.LENGTH_SHORT).show();
                }
                showDetails();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDetails() {
        tvTitle.setText(task.getTitle());
        tvType.setText("סוג: " + task.getTypeName());
        tvSubject.setText("מקצוע: " + task.getSubject());
        tvPriority.setText("עדיפות: " + task.getPriority());
        tvDueDate.setText("תאריך הגשה: " + task.getDueDate());

        if (task instanceof HomeworkTask) {
            tvAmount.setText("מספר תרגילים: " + ((HomeworkTask) task).getExercises());
        } else if (task instanceof ExamTask) {
            tvAmount.setText("מספר נושאים: " + ((ExamTask) task).getTopics());
        }

        if (task.isDone()) {
            tvStatus.setText("סטטוס: בוצע ✔");
            tvPoints.setText("נקודות: " + task.getPoints() + " (נספרו)");
            btnToggleDone.setText("בטל סימון כבוצע");
        } else {
            tvStatus.setText("סטטוס: טרם בוצע");
            tvPoints.setText("נקודות: " + task.getPoints() + " (יתווספו אחרי הביצוע)");
            btnToggleDone.setText("סמן כבוצע");
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת משימה")
                .setMessage("למחוק את \"" + task.getTitle() + "\"?")
                .setPositiveButton("מחק", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storage.deleteById(task.getId());
                        Toast.makeText(TaskDetailsActivity.this, "המשימה נמחקה", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("ביטול", null)
                .show();
    }
}
