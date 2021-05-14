package tr.edu.yildiz.mehmethayricakir;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AddQuestionActivity extends AppCompatActivity {
    EditText question;
    EditText optionA;
    EditText optionB;
    EditText optionC;
    EditText optionD;
    EditText optionE;
    Button attach;
    TextView attachedFileName;
    RadioGroup correctOption;
    Button addQuestion;
    static final int ATTACH_FILE = 1;
    Uri uri;

    static ArrayList<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        bindVariables();
        bindButtons();
    }

    private void bindButtons() {
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimeTypes = { "image/*", "video/*", "audio/*", "application/*" };
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.putExtra("return-data", true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), ATTACH_FILE);
            }
        });

        addQuestion.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                int correctOptionIndex;
                switch (correctOption.getCheckedRadioButtonId()){
                    case R.id.radio_button_option_a:
                        correctOptionIndex = 0;
                        break;
                    case R.id.radio_button_option_b:
                        correctOptionIndex = 1;
                        break;
                    case R.id.radio_button_option_c:
                        correctOptionIndex = 2;
                        break;
                    case R.id.radio_button_option_d:
                        correctOptionIndex = 3;
                        break;
                    case R.id.radio_button_option_e:
                        correctOptionIndex = 4;
                        break;
                    default:
                        correctOptionIndex = -1;
                        break;
                }
                questions.add(new Question(question.getText().toString(), new String[]{ optionA.getText().toString(), optionB.getText().toString(), optionC.getText().toString(), optionD.getText().toString(), optionE.getText().toString() }, uri, correctOptionIndex));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == ATTACH_FILE) && (resultCode == RESULT_OK)){
            if(data != null){
                uri = data.getData();
                attachedFileName.setText(uri.getLastPathSegment());
                attachedFileName.setEnabled(true);
            }
        }
    }

    private void bindVariables() {
        question = findViewById(R.id.question_edit_text);
        optionA = findViewById(R.id.option_a_edit_text);
        optionB = findViewById(R.id.option_b_edit_text);
        optionC = findViewById(R.id.option_c_edit_text);
        optionD = findViewById(R.id.option_d_edit_text);
        optionE = findViewById(R.id.option_e_edit_text);
        attach = findViewById(R.id.attach_button);
        attachedFileName = findViewById(R.id.attached_file_name_text);
        attachedFileName.setEnabled(false);
        correctOption = findViewById(R.id.correct_option_radio_group);
        addQuestion = findViewById(R.id.add_question_button);
    }
}