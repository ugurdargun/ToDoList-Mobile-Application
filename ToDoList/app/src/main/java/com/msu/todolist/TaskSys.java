package com.msu.todolist;

import java.util.ArrayList;

public class TaskSys {

    public static ArrayList<Task> todayTaskList(ArrayList<Task> taskList) {
        ArrayList<Task> tList = new ArrayList<>();
        for(Task t: taskList){
            if(t.getStatus() == 0  && DateConverter.todayDateIdentification(t.getDate(), t.getTime())){
                tList.add(t);
            }
        }
        return tList;
    }
    public static ArrayList<Task> pastTaskList(ArrayList<Task> taskList) {
        ArrayList<Task> tList = new ArrayList<>();
        for(Task t: taskList){
            if(t.getStatus() == 0 && DateConverter.pastDateIdentification(t.getDate(), t.getTime())){
                tList.add(t);
            }
        }
        return tList;
    }
    public static ArrayList<Task> plannedTaskList(ArrayList<Task> taskList) {
        ArrayList<Task> tList = new ArrayList<>();
        for(Task t: taskList){
            if(t.getStatus() == 0  && DateConverter.FutureDateIdentification(t.getDate(), t.getTime())){
                tList.add(t);
            }
        }
        return tList;
    }

}