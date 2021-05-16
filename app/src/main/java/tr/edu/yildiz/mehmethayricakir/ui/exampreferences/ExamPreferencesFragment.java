package tr.edu.yildiz.mehmethayricakir.ui.exampreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import tr.edu.yildiz.mehmethayricakir.R;

import static android.view.View.GONE;

public class ExamPreferencesFragment extends Fragment {

    View root;
    EditText examDurationInMinutes;
    EditText questionPoints;
    Slider questionDifficulty;
    TextView questionDifficultyText;
    Button saveExamPrefs;
    FloatingActionButton fab;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_exam_preferences, container, false);

        sharedPreferences = root.getContext().getSharedPreferences(root.getContext().getPackageName(), Context.MODE_PRIVATE);
        bindVariables();
        if(fab != null){
            fab.setVisibility(GONE);
            getActivity().findViewById(R.id.fab_q).setVisibility(GONE);
            getActivity().findViewById(R.id.fab_e).setVisibility(GONE);
        }
        return root;
    }

    private void bindVariables(){
        examDurationInMinutes = root.findViewById(R.id.exam_duration_in_minutes);
        questionPoints = root.findViewById(R.id.question_points);
        questionDifficulty = root.findViewById(R.id.question_difficulty_slider);
        questionDifficultyText = root.findViewById(R.id.question_difficulty_text);
        saveExamPrefs = root.findViewById(R.id.save_exam_prefs_button);

        questionDifficulty.setValueFrom(2);
        questionDifficulty.setValueTo(5);
        questionDifficulty.setStepSize(1);

        String duration = sharedPreferences.getInt("examDurationInt", 0) + "";
        examDurationInMinutes.setText(duration);

        String points = sharedPreferences.getInt("questionPointsInt", 0) + "";
        questionPoints.setText(points);

        int difficultyInt = sharedPreferences.getInt("questionDifficultyInt", 2);
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

        saveExamPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(areFieldsValid()){
                    int examDurationInt = Integer.parseInt(examDurationInMinutes.getText().toString());
                    int questionPointsInt = Integer.parseInt(questionPoints.getText().toString());
                    int questionDifficultyInt = Integer.parseInt(questionDifficultyText.getText().toString());

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("examDurationInt", examDurationInt);
                    editor.putInt("questionPointsInt", questionPointsInt);
                    editor.putInt("questionDifficultyInt", questionDifficultyInt);
                    editor.apply();
                    Toast.makeText(getActivity(), "Exam preferences saved successfully!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
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