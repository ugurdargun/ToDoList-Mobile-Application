package com.msu.todolist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.msu.todolist.Activities.EditActivity;
import com.msu.todolist.Activities.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.MyRecyclerViewItemHolder> implements Filterable {

    Context context;
    Toast toast;
    Dialog customDialog;
    DatabaseHelper dbHelper;
    NotificationHelper notificationHelper;
    MediaPlayer mp;

    private ArrayList<Task> recyclerItemValues, recyclerItemValues_Full;
    private Task selectedTask;
    private boolean isTaskDatePast;

    public interface TaskRecyclerAdapterInterface{
        void updateCompletedAndFavorite();
        void displayMessage();
    }
    TaskRecyclerAdapterInterface taskAdapterInterface;

    public CustomRecyclerViewAdapter(Context context, ArrayList<Task> values){
        this.context = context;
        this.recyclerItemValues = values;
        recyclerItemValues_Full = new ArrayList<>(recyclerItemValues);
        dbHelper = new DatabaseHelper(context);
        notificationHelper = new NotificationHelper(this.context);
        taskAdapterInterface = (TaskRecyclerAdapterInterface)context;
        createSound();
    }

    @NonNull
    @Override
    public MyRecyclerViewItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        itemView = inflater.inflate(R.layout.custom_recyclerview1, viewGroup, false);
        MyRecyclerViewItemHolder mViewHolder = new MyRecyclerViewItemHolder(itemView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewItemHolder myRecyclerViewItemHolder, int position) {
        MyRecyclerViewItemHolder itemView = (MyRecyclerViewItemHolder) myRecyclerViewItemHolder;
        final Task taskObj = recyclerItemValues.get(position);
        selectedTask = taskObj;
        isTaskDatePast = false;

        itemView.taskName.setText(taskObj.getName());
        itemView.taskCategory.setText(taskObj.getCategory());
        itemView.taskDate.setText(taskObj.getDate() + " - " + taskObj.getTime());

        if(taskObj.getStatus() == 1){ itemView.taskCheckbox1.setChecked(true); }
        else{ itemView.taskCheckbox1.setChecked(false); }
        if(taskObj.getFavorite() == 1){itemView.taskCheckbox2.setChecked(true); }
        else{ itemView.taskCheckbox2.setChecked(false); }
        if(DateConverter.pastDateIdentification(selectedTask.getDate(), selectedTask.getTime())) {
            isTaskDatePast = true;
            itemView.taskParentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.pastDate));
        } else{
            itemView.taskParentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        itemView.taskCheckbox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemView.taskCheckbox1.isChecked()){
                    boolean res = TaskDB.updateStatus(dbHelper, taskObj.getId()+"", "1");
                    taskObj.setStatus(1);
                    notifyItemChanged(position);

                    createSound();
                    playSound();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() { taskAdapterInterface.updateCompletedAndFavorite(); }
                    }, 500);

                    long id = (long) taskObj.getId();
                    notificationHelper.cancelNotification(id);
                }
                else{
                    boolean res = TaskDB.updateStatus(dbHelper, taskObj.getId()+"", "0");
                    taskObj.setStatus(0);
                    notifyItemChanged(position);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            taskAdapterInterface.updateCompletedAndFavorite();
                        }
                    }, 500);

                    if(!isTaskDatePast){
                        long id = (long) taskObj.getId();
                        Calendar task_calendar = DateConverter.convertToCalendar(taskObj.getDate(), taskObj.getTime());
                        notificationHelper.newNotification(id, task_calendar);
                    }

                }
            }
        });

            itemView.taskCheckbox2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemView.taskCheckbox2.isChecked()){
                        boolean res = TaskDB.updateFavorite(dbHelper, taskObj.getId()+"", "1");
                        taskObj.setFavorite(1);
                        notifyItemChanged(position);
                    }
                    else{
                        boolean res = TaskDB.updateFavorite(dbHelper, taskObj.getId()+"", "0");
                        taskObj.setFavorite(0);
                        notifyItemChanged(position);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                taskAdapterInterface.updateCompletedAndFavorite();
                            }
                        }, 500);

                    }
                }
            });

        itemView.taskParentLayout.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    selectedTask = taskObj;
                    displayCustomDialog();
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        itemView.taskEditButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditActivity.class);
                Bundle b = new Bundle();
                Task task = recyclerItemValues.get(position);
                b.putParcelable("Task", task);
                intent.putExtras(b);
                ((MainActivity) context).startActivityForResult(intent, 2);
            }
        });

        itemView.taskDeleteButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position,taskObj.getId(), taskObj.getName());
            }
        });
    }

    private void refreshMyAdapterAfterDelete(int position){
        this.recyclerItemValues.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    @Override
    public int getItemCount() {
        return recyclerItemValues.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Task> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.clear();
                filteredList.addAll(recyclerItemValues_Full);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Task item : recyclerItemValues_Full) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recyclerItemValues.clear();
            recyclerItemValues.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    class MyRecyclerViewItemHolder extends RecyclerView.ViewHolder{
        TextView taskName, taskCategory, taskDate;
        Button taskEditButt, taskDeleteButt;
        CheckBox taskCheckbox1, taskCheckbox2;
        ConstraintLayout taskParentLayout;

        public MyRecyclerViewItemHolder(@NonNull View itemView) {
            super(itemView);
            taskParentLayout = itemView.findViewById(R.id.recycler_ParentLayout);
            taskName = itemView.findViewById(R.id.recycler1_tv1);
            taskCategory = itemView.findViewById(R.id.recycler1_tv2);
            taskDate = itemView.findViewById(R.id.recycler1_tv3);
            taskCheckbox1 = itemView.findViewById(R.id.recycler1_cbox1);
            taskCheckbox2 = itemView.findViewById(R.id.recycler1_cbox2);
            taskEditButt = itemView.findViewById(R.id.recycler1_but1);
            taskDeleteButt = itemView.findViewById(R.id.recycler1_but2);
        }
    }

    private void showDialog(int pos, int taskId, String taskName) {
        AlertDialog.Builder box = new AlertDialog.Builder(context);
        box.setTitle("Confirm");
        box.setMessage("Are you sure you want to delete \""+taskName+"\"?");

        box.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean res = TaskDB.delete(dbHelper, taskId+"");
                if(res) {
                    refreshMyAdapterAfterDelete(pos);
                    displayToast("Deleted");

                    long id = (long) taskId;
                    notificationHelper.cancelNotification(id);
                    taskAdapterInterface.displayMessage();
                } else{
                    displayToast("Deleting Error!");
                }
            }
        });
        box.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        box.create();
        box.show();
    }

    private void displayCustomDialog() {
        customDialog = new Dialog(context);
        customDialog.setContentView(R.layout.custom_dialog1);

        Button task_btnClose = customDialog.findViewById(R.id.custDia_but1);
        ImageView task_img = customDialog.findViewById(R.id.custDia_img1);
        ImageView task_star = customDialog.findViewById(R.id.custDia_img2);
        TextView task_category = customDialog.findViewById(R.id.custDia_tv1);
        TextView task_name = customDialog.findViewById(R.id.custDia_tv2);
        TextView task_details = customDialog.findViewById(R.id.custDia_tv3);
        TextView task_date = customDialog.findViewById(R.id.custDia_tv4);

        int id = context.getResources().getIdentifier("img_"+selectedTask.getCategory().toLowerCase(), "drawable", context.getPackageName());
        task_img.setImageResource(id);

        if(selectedTask.getFavorite() == 0){task_star.setVisibility(View.INVISIBLE);}
        else{ task_star.setVisibility(View.VISIBLE);}

        task_category.setText(selectedTask.getCategory());
        task_name.setText(selectedTask.getName());
        task_details.setText(selectedTask.getDetails());
        task_date.setText(selectedTask.getDate() + " - " + selectedTask.getTime());

        task_btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }

    private void displayToast(String msg){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void createSound() { if(mp == null){mp = MediaPlayer.create(context, R.raw.cbox_completed_effect);} }
    private void playSound() {mp.start();}
    public void stopSound(){
        if(mp != null && mp.isPlaying()){
            mp.stop();
            mp.release();
            mp = null;
        }
    }

}