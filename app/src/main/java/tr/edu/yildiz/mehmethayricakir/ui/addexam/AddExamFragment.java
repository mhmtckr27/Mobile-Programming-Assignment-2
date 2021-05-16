package tr.edu.yildiz.mehmethayricakir.ui.addexam;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

import tr.edu.yildiz.mehmethayricakir.MenuActivity;
import tr.edu.yildiz.mehmethayricakir.Question;
import tr.edu.yildiz.mehmethayricakir.QuestionAdapter;
import tr.edu.yildiz.mehmethayricakir.R;

import static android.view.View.GONE;

public class AddExamFragment extends Fragment {
    View root;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    ArrayList<Question> questions;
    EditText examDurationInMinutes;
    EditText questionPoints;
    Slider questionDifficulty;
    TextView questionDifficultyText;
    Button loadQuestions;
    Button createExam;
    SharedPreferences sharedPreferences;
    FloatingActionButton fab;


    public static int currentSelectedQuestionCount;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_exam, container, false);
        sharedPreferences = root.getContext().getSharedPreferences(root.getContext().getPackageName(), Context.MODE_PRIVATE);
        bindVariables();
        currentSelectedQuestionCount = 0;
        if(fab != null){
            fab.setVisibility(GONE);
            getActivity().findViewById(R.id.fab_q).setVisibility(GONE);
            getActivity().findViewById(R.id.fab_e).setVisibility(GONE);
        }
        return root;
    }

    private void bindVariables(){
        recyclerView = root.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        questions = MenuActivity.questions;

        examDurationInMinutes = root.findViewById(R.id.exam_duration_in_minutes);
        questionPoints = root.findViewById(R.id.question_points);
        questionDifficulty = root.findViewById(R.id.question_difficulty_slider);
        questionDifficultyText = root.findViewById(R.id.question_difficulty_text);
        loadQuestions = root.findViewById(R.id.save_exam_prefs_button);
        createExam = root.findViewById(R.id.create_exam_button);

        questionDifficulty.setValueFrom(2);
        questionDifficulty.setValueTo(5);
        questionDifficulty.setStepSize(1);

        String duration = sharedPreferences.getInt("examDurationInt", 60) + "";
        examDurationInMinutes.setText(duration);

        String points = sharedPreferences.getInt("questionPointsInt", 5) + "";
        questionPoints.setText(points);

        int difficultyInt = sharedPreferences.getInt("questionDifficultyInt", 5);
        questionDifficulty.setValue(difficultyInt);
        String difficulty = difficultyInt + "";
        questionDifficultyText.setText(difficulty);

        questionDifficulty.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String val = (int) value + "";
                questionDifficultyText.setText(val);
            }
        });

        loadQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(areFieldsValid()){
                    int examDurationInt = Integer.parseInt(examDurationInMinutes.getText().toString());
                    int questionPointsInt = Integer.parseInt(questionPoints.getText().toString());
                    int questionDifficultyInt = Integer.parseInt(questionDifficultyText.getText().toString());

                    Toast.makeText(getActivity(), "Loading questions...", Toast.LENGTH_SHORT).show();

                    recyclerView.setAdapter(new QuestionAdapter(getActivity(), questions, true, questionDifficultyInt));
                }
                else{
                    Toast.makeText(getActivity(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        createExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSelectedQuestionCount <= 0){
                    Toast.makeText(getActivity(), "Please select at least 1 question!!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Exam created successfully!", Toast.LENGTH_SHORT).show();
                    MenuActivity.instance.loadFragment(R.id.nav_home, getActivity(), null);
                }
            }
        });

        if(getActivity() != null){
            fab = getActivity().findViewById(R.id.fab);
        }
    }

    private boolean areFieldsValid() {
        return  !examDurationInMinutes.getText().toString().equals("") &&
                !questionPoints.getText().toString().equals("");
    }
}