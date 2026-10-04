package com.example.homeworkplanner;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private Spinner spType, spSubject, spPriority;
    private EditText etTitle, etDueDate, etAmount;
    private Button btnSave, btnCancel;
    private TaskStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        spType = findViewById(R.id.spType);
        spSubject = findViewById(R.id.spSubject);
        spPriority = findViewById(R.id.spPriority);
        etTitle = findViewById(R.id.etTitle);
        etDueDate = findViewById(R.id.etDueDate);
        etAmount = findViewById(R.id.etAmount);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        storage = new TaskStorage(this);

        String[] types = {Task.TYPE_HOMEWORK, Task.TYPE_EXAM};
        setSpinner(spType, types);
        setSpinner(spSubject, Task.SUBJECTS);
        setSpinner(spPriority, Task.PRIORITIES);

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    etAmount.setHint("מספר תרגילים");
                } else {
                    etAmount.setHint("מספר נושאים");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> a = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(a);
    }

    private void save() {
        String title = etTitle.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();
        String amountText = etAmount.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "יש להזין כותרת", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidDate(dueDate)) {
            Toast.makeText(this, "תאריך לא תקין (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "יש להזין כמות (מספר)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amount < 1 || amount > 100) {
            Toast.makeText(this, "הכמות חייבת להיות בין 1 ל-100", Toast.LENGTH_SHORT).show();
            return;
        }

        String subject = spSubject.getSelectedItem().toString();
        String priority = spPriority.getSelectedItem().toString();
        int id = storage.nextId();

        Task task;
        if (spType.getSelectedItemPosition() == 0) {
            task = new HomeworkTask(id, title, subject, priority, dueDate, amount);
        } else {
            task = new ExamTask(id, title, subject, priority, dueDate, amount);
        }
        storage.addTask(task);

        Toast.makeText(this, "המשימה נשמרה", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean isValidDate(String text) {
        if (!text.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        format.setLenient(false);
        try {
            format.parse(text);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
