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

public class Fragment_TaskCategories extends Fragment {

    View view;
    Button buttons[] = null;
    final int SIZE = 7;

    public interface TaskCategoryInterface{
        void getTaskCategory(String cat);
        void clearSearchView();
    }
    TaskCategoryInterface taskCategoryInterface;

    public Fragment_TaskCategories() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_taskcategories, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskCategoryInterface = (TaskCategoryInterface ) getContext();

        buttons = new Button[SIZE];
        buttons[0] = (Button) view.findViewById(R.id.tcat_button1); buttons[0].setOnClickListener(this::onClick);
        buttons[1] = (Button) view.findViewById(R.id.tcat_button2); buttons[1].setOnClickListener(this::onClick);
        buttons[2] = (Button) view.findViewById(R.id.tcat_button3); buttons[2].setOnClickListener(this::onClick);
        buttons[3] = (Button) view.findViewById(R.id.tcat_button4); buttons[3].setOnClickListener(this::onClick);
        buttons[4] = (Button) view.findViewById(R.id.tcat_button5); buttons[4].setOnClickListener(this::onClick);
        buttons[5] = (Button) view.findViewById(R.id.tcat_button6); buttons[5].setOnClickListener(this::onClick);
        buttons[6] = (Button) view.findViewById(R.id.tcat_button7); buttons[6].setOnClickListener(this::onClick);
    }

    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tcat_button1:
                selectedBtn(buttons[0]);
                taskCategoryInterface.getTaskCategory(buttons[0].getText().toString());
                break;
            case R.id.tcat_button2:
                selectedBtn(buttons[1]);
                taskCategoryInterface.getTaskCategory(buttons[1].getText().toString());
                break;
            case R.id.tcat_button3:
                selectedBtn(buttons[2]);
                taskCategoryInterface.getTaskCategory(buttons[2].getText().toString());
                break;
            case R.id.tcat_button4:
                selectedBtn(buttons[3]);
                taskCategoryInterface.getTaskCategory(buttons[3].getText().toString());
                break;
            case R.id.tcat_button5:
                selectedBtn(buttons[4]);
                taskCategoryInterface.getTaskCategory(buttons[4].getText().toString());
                break;
            case R.id.tcat_button6:
                selectedBtn(buttons[5]);
                taskCategoryInterface.getTaskCategory(buttons[5].getText().toString());
                break;
            case R.id.tcat_button7:
                selectedBtn(buttons[6]);
                taskCategoryInterface.getTaskCategory(buttons[6].getText().toString());
                break;
            default:
                break;
        }

        taskCategoryInterface.clearSearchView();
    }

    private void selectedBtn(Button btn){
        btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.selected_Color2));
        for(int i=0; i<buttons.length; i++){
            if(buttons[i] != btn){
                buttons[i].setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.unselected_Color));
            }
        }
    }

}