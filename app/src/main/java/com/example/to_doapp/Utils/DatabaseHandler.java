package com.example.to_doapp.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.to_doapp.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }
    public int getVersion(){
        return VERSION;
    }
    public void deleteAll(){
        db.execSQL("delete from "+TODO_TABLE);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);
    }
    public ToDoModel getTask(int postion){
        Cursor cur=null;
        cur.moveToPosition(postion);
        db.beginTransaction();
        ToDoModel task=new ToDoModel();
        task.setId(cur.getInt(cur.getColumnIndex(ID)));
        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
        db.endTransaction();
        return task;
    }

    public List<ToDoModel> getAllTasks(){
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        taskList.add(task);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }
//    public void moveTask(int from,int to){
//        ToDoModel task=new ToDoModel();
//        Cursor cur=null;
//        cur.moveToPosition(from);
//        task.setId(cur.getInt(cur.getColumnIndex(ID)));
//        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
//        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
//        db.beginTransaction();
//        deleteTask(task.getId());
//        cur.moveToPosition(to);
//        insertAtPosition(to,task);
//    }
//    public void insertAtPosition(int position,ToDoModel task){
//        db.execSQL("UPDATE "+ TODO_TABLE+ " SET"+  ID +"= " + "id + 1 WHERE ID >= "+ position);
//        db.execSQL("SELECT * FROM "+TODO_TABLE);
//        db.execSQL("INSERT INTO "+TODO_TABLE+"(id,task,status) VALUES "+" ("+position+","+ task.getTask()+","+task.getStatus()+")");
//    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}
