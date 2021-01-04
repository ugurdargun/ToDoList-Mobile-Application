package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.msu.todolist.R;

public class Fragment_TaskTypes extends Fragment {

    View view;
    Button buttons[] = null;
    final int SIZE = 6;

    public interface TaskTypeInterface{
        void getTaskType(String type);
        void clearSearchView();
    }
    TaskTypeInterface taskTypeInterface;

    public Fragment_TaskTypes() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasktypes, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskTypeInterface = (TaskTypeInterface) getContext();

        buttons = new Button[SIZE];
        buttons[0] = (Button) view.findViewById(R.id.ttype_button1); buttons[0].setOnClickListener(this::onClick);
        buttons[1] = (Button) view.findViewById(R.id.ttype_button2); buttons[1].setOnClickListener(this::onClick);
        buttons[2] = (Button) view.findViewById(R.id.ttype_button3); buttons[2].setOnClickListener(this::onClick);
        buttons[3] = (Button) view.findViewById(R.id.ttype_button4); buttons[3].setOnClickListener(this::onClick);
        buttons[4] = (Button) view.findViewById(R.id.ttype_button5); buttons[4].setOnClickListener(this::onClick);
        buttons[5] = (Button) view.findViewById(R.id.ttype_button6); buttons[5].setOnClickListener(this::onClick);
    }

    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.ttype_button1:
                selectedBtn(buttons[0]);
                taskTypeInterface.getTaskType(buttons[0].getText().toString());
                break;
            case R.id.ttype_button2:
                selectedBtn(buttons[1]);
                taskTypeInterface.getTaskType(buttons[1].getText().toString());
                break;
            case R.id.ttype_button3:
                selectedBtn(buttons[2]);
                taskTypeInterface.getTaskType(buttons[2].getText().toString());
                break;
            case R.id.ttype_button4:
                selectedBtn(buttons[3]);
                taskTypeInterface.getTaskType(buttons[3].getText().toString());
                break;
            case R.id.ttype_button5:
                selectedBtn(buttons[4]);
                taskTypeInterface.getTaskType(buttons[4].getText().toString());
                break;
            case R.id.ttype_button6:
                selectedBtn(buttons[5]);
                taskTypeInterface.getTaskType(buttons[5].getText().toString());
                break;
            default:
                break;
        }

        taskTypeInterface.clearSearchView();
    }

    private void selectedBtn(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.selected_Color));
        for(int i=0; i<buttons.length; i++){
            if(buttons[i] != btn){
                buttons[i].setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.unselected_Color));
            }
        }
    }

}