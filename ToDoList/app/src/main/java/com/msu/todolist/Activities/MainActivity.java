package com.msu.todolist.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.msu.todolist.CustomRecyclerViewAdapter;
import com.msu.todolist.DatabaseHelper;
import com.msu.todolist.DateConverter;
import Fragments.Fragment_TaskCategories;
import Fragments.Fragment_TaskTypes;

import com.msu.todolist.R;
import com.msu.todolist.Task;
import com.msu.todolist.TaskDB;
import com.msu.todolist.TaskSys;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CustomRecyclerViewAdapter.TaskRecyclerAdapterInterface, Fragment_TaskCategories.TaskCategoryInterface, Fragment_TaskTypes.TaskTypeInterface{

    Toast toast;
    RecyclerView taskRecyclerView;
    LinearLayoutManager tasksRecyclerLayout;
    CustomRecyclerViewAdapter taskRecyclerAdapter;

    DatabaseHelper dbHelper;
    TextView tv_info;
    SearchView searchView;

    private ArrayList<Task> taskList;
    private boolean switchStatus, orderStatus, searchStatus;
    private String currentCategory, currentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragment_TaskTypes();

        dbHelper = new DatabaseHelper(MainActivity.this); //DB creation

        tv_info = findViewById(R.id.mainAct_infoTv);
        currentType = "All";
        currentCategory = "All";
        switchStatus = false;
        orderStatus = false;
        searchStatus = false;

        taskRecyclerView = findViewById(R.id.mainAct_taskRecycler);
        tasksRecyclerLayout = new LinearLayoutManager(this);
        taskRecyclerView.setLayoutManager(tasksRecyclerLayout);

        taskList =  new ArrayList<>();
        taskList = TaskDB.getAllTasks(dbHelper);

        taskRecyclerAdapter = new CustomRecyclerViewAdapter(this, taskList);
        taskRecyclerView.setAdapter(taskRecyclerAdapter);

        displayMessage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        taskRecyclerAdapter.stopSound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskRecyclerAdapter.stopSound();
    }

    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        MenuItem vertItem = menu.findItem(R.id.app_bar_vert);

        vertItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                    MenuItem mp = menu.findItem(R.id.menuItem_Delete);
                    if(!switchStatus){
                        mp.setEnabled(true);
                        if(currentType.equalsIgnoreCase("Completed")){
                            mp.setTitle("Delete Completed Tasks");
                        } else if(currentType.equalsIgnoreCase("Past")){
                            mp.setTitle("Delete Past Tasks");
                        } else {
                            mp.setTitle("Del. Comp. & Past Tasks");
                        }
                    }
                    else{
                        mp.setTitle("Unavailable"); // since completed and past tasks are not shown on Fragment_TaskCategories
                        mp.setEnabled(false);
                    }
                return false;
            }
        });

            searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
            searchView.setQueryHint("Search Task");
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchStatus = true;
                    updateSearchBoxItems();
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) { return false; }
                @Override
                public boolean onQueryTextChange(String newText) {
                    if(searchStatus){
                        taskRecyclerAdapter.getFilter().filter(newText);
                    }
                  return false;
                }
            });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.app_bar_swap:
                    if(switchStatus){
                        setFragment_TaskTypes();
                        currentType = "All";
                    }
                    else {
                        setFragment_TaskCategories();
                        currentCategory = "All";
                    }
                    switchStatus = !switchStatus;
                    updateRecycler();
                    displayToast("Switched");
                return true;
            case R.id.menuItem_Delete:

                if(currentType.equalsIgnoreCase("Completed")){
                    deleteCompletedTasks();
                } else if(currentType.equalsIgnoreCase("Past")){
                    deletePastTasks();
                } else {
                    deleteCompletedTasks();
                    deletePastTasks();
                }

                displayToast("Done");
                updateRecycler();
                return true;
            case R.id.menuItem_Order:
                    if(orderStatus){
                        TaskDB.ORDER_VALUE = "ASC";
                        item.setTitle("Sort by Date (Furthest)");
                    } else{
                        TaskDB.ORDER_VALUE = "DESC";
                        item.setTitle("Sort by Date (Closest)");
                    }
                    updateRecycler();
                    orderStatus = !orderStatus;
                return true;

            case R.id.menuItem_About:
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivityForResult(intent, 3);
                return true;
            default:
                clearSearchView();
                return super.onOptionsItemSelected(item);
        }

    }

    private void setFragment_TaskTypes(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.mainAct_fragment, new Fragment_TaskTypes()).commit();
    }

    private void setFragment_TaskCategories(){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.mainAct_fragment, new Fragment_TaskCategories()).commit();
    }

    private void getTaskTypeToDisplay(String taskType) {
        this.currentType = taskType;
        this.taskList.clear();

        switch (taskType) {
            case "All": getAllTasksFromDB();
                break;
            case "Today": getTodayTasks();
                break;
            case "Planned": getPlannedTasks();
                break;
            case "Completed": getCompletedTasksFromDB();
                break;
            case "Favorite": getPlannedFavoriteTasksFromDB();
                break;
            case "Past": getPastTasks();
                break;
            default:
                break;
        }

        this.taskRecyclerAdapter.notifyDataSetChanged();
        this.displayMessage();
    }

    private void getTaskCategoryToDisplay(String catName){
        this.currentCategory = catName;
        this.taskList.clear();

        if(catName.equalsIgnoreCase("All")){ getPlannedTasks(); } //Uncompleted and Planned tasks are shown
        else{ getSomeTasksFromDB(catName); } //In each category, uncompleted and Planned tasks are shown.

        this.taskRecyclerAdapter.notifyDataSetChanged();
        this.displayMessage();
    }

    private void getAllTasksFromDB(){
        this.taskList.addAll(TaskDB.getAllTasks(dbHelper));
    }
    private void getSomeTasksFromDB(String curr_Cat){
        this.taskList.addAll(TaskDB.getUncompletedSomeTasks(dbHelper, curr_Cat));
    }
    private void getCompletedTasksFromDB(){
        this.taskList.addAll(TaskDB.getCompletedTasks(dbHelper));
    }
    private void getPlannedFavoriteTasksFromDB(){
        this.taskList.addAll(TaskDB.getPlannedFavoriteTasks(dbHelper));
    }
    private void getTodayTasks(){
        this.taskList.addAll(TaskSys.todayTaskList(TaskDB.getAllTasks(dbHelper)));
    }
    private void getPastTasks(){
        this.taskList.addAll(TaskSys.pastTaskList(TaskDB.getAllTasks(dbHelper)));
    }
    private void getPlannedTasks(){
        this.taskList.addAll(TaskSys.plannedTaskList(TaskDB.getAllTasks(dbHelper)));
    }
    private void deleteCompletedTasks(){
        boolean res = TaskDB.deleteSelectedTasks(dbHelper);
    }
    private void deletePastTasks(){
        for(Task t: TaskDB.getAllTasks(dbHelper)){
            if(DateConverter.pastDateIdentification(t.getDate(), t.getTime())){
                boolean res =  TaskDB.delete(dbHelper, t.getId()+"");
            }
        }
    }

    private void displayToast(String msg){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void updateRecycler() {
        clearSearchView();
        determineSwitchStatus();
    }

    private void updateSearchBoxItems() {
        this.taskList.clear();
        determineSwitchStatus();
        this.taskRecyclerAdapter = new CustomRecyclerViewAdapter(this, taskList);
        this.taskRecyclerView.setAdapter(taskRecyclerAdapter);
    }

    private void determineSwitchStatus(){
        if(switchStatus){
            getTaskCategoryToDisplay(currentCategory);
        } else{
            getTaskTypeToDisplay(currentType);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateRecycler();
    }

    @Override
    public void clearSearchView(){
        searchStatus = false;
        searchView.onActionViewCollapsed();
        searchView.setQuery("", false);
        searchView.clearFocus();
    }
    @Override
    public void displayMessage(){
        if(taskList.isEmpty()){
            tv_info.setVisibility(View.VISIBLE);
            tv_info.setText("There are no records to display!");
        } else{
            tv_info.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void updateCompletedAndFavorite() {
        if(!switchStatus) {
            if(!currentType.equalsIgnoreCase("All")) {
                getTaskTypeToDisplay(currentType);
            }
        }
    }
    @Override
    public void getTaskCategory(String cat) { getTaskCategoryToDisplay(cat); }
    @Override
    public void getTaskType(String type) { getTaskTypeToDisplay(type);}
}