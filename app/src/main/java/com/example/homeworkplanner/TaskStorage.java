package com.example.homeworkplanner;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class TaskStorage {

    public static final String PREFS_NAME = "planner_prefs";
    public static final String KEY_NAME = "student_name";
    private static final String KEY_TASKS = "tasks_json";

    private SharedPreferences prefs;
    private Gson gson;

    public TaskStorage(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public ArrayList<Task> loadAll() {
        ArrayList<Task> tasks = new ArrayList<>();
        String json = prefs.getString(KEY_TASKS, null);
        if (json == null) {
            return tasks;
        }
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();
            String type = obj.get("type").getAsString();
            if (type.equals(Task.TYPE_EXAM)) {
                tasks.add(gson.fromJson(obj, ExamTask.class));
            } else {
                tasks.add(gson.fromJson(obj, HomeworkTask.class));
            }
        }
        return tasks;
    }

    public void saveAll(ArrayList<Task> tasks) {
        JsonArray array = new JsonArray();
        for (Task t : tasks) {
            JsonObject obj = gson.toJsonTree(t).getAsJsonObject();
            obj.addProperty("type", t.getTypeName());
            array.add(obj);
        }
        prefs.edit().putString(KEY_TASKS, gson.toJson(array)).apply();
    }

    public void addTask(Task task) {
        ArrayList<Task> tasks = loadAll();
        tasks.add(task);
        saveAll(tasks);
    }

    public Task findById(int id) {
        for (Task t : loadAll()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public void updateTask(Task task) {
        ArrayList<Task> tasks = loadAll();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                break;
            }
        }
        saveAll(tasks);
    }

    public void deleteById(int id) {
        ArrayList<Task> tasks = loadAll();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == id) {
                tasks.remove(i);
                break;
            }
        }
        saveAll(tasks);
    }

    public int nextId() {
        int max = 0;
        for (Task t : loadAll()) {
            if (t.getId() > max) {
                max = t.getId();
            }
        }
        return max + 1;
    }
}
