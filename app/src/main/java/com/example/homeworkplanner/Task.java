package com.example.homeworkplanner;

public class Task implements Rewardable {

    public static final String TYPE_HOMEWORK = "שיעורי בית";
    public static final String TYPE_EXAM = "מבחן";

    public static final String[] SUBJECTS = {"מתמטיקה", "אנגלית", "מדעים", "היסטוריה", "מחשבים"};
    public static final String[] PRIORITIES = {"גבוהה", "בינונית", "נמוכה"};

    private int id;
    private String title;
    private String subject;
    private String priority;
    private String dueDate;
    private boolean done;

    public Task(int id, String title, String subject, String priority, String dueDate) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.priority = priority;
        this.dueDate = dueDate;
        this.done = false;
    }

    public String getTypeName() {
        return "משימה";
    }

    protected int getPriorityBonus() {
        if (priority.equals(PRIORITIES[0])) {
            return 5;
        } else if (priority.equals(PRIORITIES[1])) {
            return 3;
        } else {
            return 1;
        }
    }

    @Override
    public int getPoints() {
        return getPriorityBonus();
    }

    @Override
    public String toString() {
        String mark = done ? "✔ " : "";
        return mark + title + "\n"
                + getTypeName() + " | " + subject + " | עדיפות " + priority + "\n"
                + "להגשה עד " + dueDate + " | " + getPoints() + " נקודות";
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public String getPriority() {
        return priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
