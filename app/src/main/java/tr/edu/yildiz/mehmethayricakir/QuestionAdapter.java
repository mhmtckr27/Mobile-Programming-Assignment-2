package tr.edu.yildiz.mehmethayricakir;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import tr.edu.yildiz.mehmethayricakir.ui.addexam.AddExamFragment;
import tr.edu.yildiz.mehmethayricakir.ui.addquestion.AddQuestionFragment;

import static android.view.View.GONE;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.UserViewHolder> {

    Context context;
    ArrayList<Question> questions;
    boolean isCreatingExamList;
    int difficulty;

    public QuestionAdapter(Context context, ArrayList<Question> questions, boolean isCreatingExamList, int difficulty){
        this.context = context;
        this.questions = questions;
        this.isCreatingExamList = isCreatingExamList;
        this.difficulty = difficulty;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView question;
        ArrayList<TextView> options;
        RadioButton[] optionsRadioButtons = new RadioButton[5];
        TextView attachmentPath;

        LinearLayout buttonsLinearLayout;
        Button delete;
        Button edit;

        ArrayList<LinearLayout> optionsLinearLayouts;

        CheckBox addQuestionToExamCheckBox;
        View checkboxDivider;
        View buttonsDivider;

        public UserViewHolder(View v){
            super(v);
            cardView = v.findViewById(R.id.card_view);

            question = v.findViewById(R.id.question);

            options = new ArrayList<>();
            options.add(v.findViewById(R.id.option_a));
            options.add(v.findViewById(R.id.option_b));
            options.add(v.findViewById(R.id.option_c));
            options.add(v.findViewById(R.id.option_d));
            options.add(v.findViewById(R.id.option_e));

            attachmentPath = v.findViewById(R.id.attachment_path);
            optionsRadioButtons[0] = v.findViewById(R.id.radio_button_option_a);
            optionsRadioButtons[1] = v.findViewById(R.id.radio_button_option_b);
            optionsRadioButtons[2] = v.findViewById(R.id.radio_button_option_c);
            optionsRadioButtons[3] = v.findViewById(R.id.radio_button_option_d);
            optionsRadioButtons[4] = v.findViewById(R.id.radio_button_option_e);

            delete = v.findViewById(R.id.delete_button);
            edit = v.findViewById(R.id.edit_button);

            optionsLinearLayouts = new ArrayList<>();
            optionsLinearLayouts.add(v.findViewById(R.id.option_a_linear_layout));
            optionsLinearLayouts.add(v.findViewById(R.id.option_b_linear_layout));
            optionsLinearLayouts.add(v.findViewById(R.id.option_c_linear_layout));
            optionsLinearLayouts.add(v.findViewById(R.id.option_d_linear_layout));
            optionsLinearLayouts.add(v.findViewById(R.id.option_e_linear_layout));

            addQuestionToExamCheckBox = v.findViewById(R.id.checkBox);
            checkboxDivider = v.findViewById(R.id.checkbox_divider);
            buttonsDivider = v.findViewById(R.id.buttons_divider);
            buttonsLinearLayout = v.findViewById(R.id.buttons_linear_layout);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.question_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if(isCreatingExamList){
            holder.question.setText(questions.get(position).getQuestion());

            for(int i = 4; i > difficulty - 1; i--){
                holder.optionsLinearLayouts.get(i).setVisibility(GONE);
            }

            ArrayList<String> newOptions = new ArrayList<>();
            ArrayList<String> tempOptions = new ArrayList<>(questions.get(position).options);
            newOptions.add(questions.get(position).options.get(questions.get(position).correctOptionIndex));
            tempOptions.remove(questions.get(position).options.get(questions.get(position).correctOptionIndex));
            for(int i = 1; i < difficulty; i++){
                Random random = new Random();
                int rand = random.nextInt(tempOptions.size());
                newOptions.add(tempOptions.get(rand));
                tempOptions.remove(rand);
            }

            Collections.shuffle(newOptions);

            for(int i = 0; i < difficulty; i++){
                holder.options.get(i).setText(newOptions.get(i));
            }

            for(RadioButton option : holder.optionsRadioButtons){
                option.setChecked(false);
                option.setEnabled(false);
                option.setButtonTintList(context.getColorStateList(R.color.radio_button_disabled));
            }
            for(String string : newOptions){
                if(string.equals(questions.get(position).options.get(questions.get(position).correctOptionIndex))){
                    holder.optionsRadioButtons[newOptions.indexOf(string)].setChecked(true);
                    holder.optionsRadioButtons[newOptions.indexOf(string)].setButtonTintList(context.getColorStateList(R.color.orange));
                }
            }

            String attachmentPathText = "attachment: " + questions.get(position).getAttachmentPath();
            holder.attachmentPath.setText(attachmentPathText);
            if(holder.attachmentPath.getText().toString().equals("attachment: ")){
                holder.attachmentPath.setVisibility(GONE);
                holder.buttonsDivider.setVisibility(GONE);
            }

            holder.checkboxDivider.setVisibility(View.VISIBLE);
            holder.addQuestionToExamCheckBox.setVisibility(View.VISIBLE);
            holder.buttonsLinearLayout.setVisibility(GONE);

            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.question_deselected));
            holder.addQuestionToExamCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.question_selected));
                        AddExamFragment.currentSelectedQuestionCount++;
                    }
                    else{
                        holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.question_deselected));
                        AddExamFragment.currentSelectedQuestionCount--;
                    }
                }
            });
        }
        else{
            holder.question.setText(questions.get(position).getQuestion());

            holder.options.get(0).setText(questions.get(position).getOptions().get(0));
            holder.options.get(1).setText(questions.get(position).getOptions().get(1));
            holder.options.get(2).setText(questions.get(position).getOptions().get(2));
            holder.options.get(3).setText(questions.get(position).getOptions().get(3));
            holder.options.get(4).setText(questions.get(position).getOptions().get(4));

            for(RadioButton option : holder.optionsRadioButtons){
                option.setChecked(false);
                option.setEnabled(false);
                option.setButtonTintList(context.getColorStateList(R.color.radio_button_disabled));
            }

            holder.optionsRadioButtons[questions.get(position).correctOptionIndex].setChecked(true);
            holder.optionsRadioButtons[questions.get(position).correctOptionIndex].setButtonTintList(context.getColorStateList(R.color.orange));

            String attachmentPathText = "attachment: " + questions.get(position).getAttachmentPath();
            holder.attachmentPath.setText(attachmentPathText);
            if(holder.attachmentPath.getText().toString().equals("attachment: ")){
                holder.attachmentPath.setVisibility(GONE);
            }

            holder.checkboxDivider.setVisibility(GONE);
            holder.addQuestionToExamCheckBox.setVisibility(GONE);
            holder.buttonsLinearLayout.setVisibility(View.VISIBLE);
            //holder.buttonsDivider.setVisibility(View.VISIBLE);

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("question", holder.question.getText().toString());
                    bundle.putString("optionA", holder.options.get(0).getText().toString());
                    bundle.putString("optionB", holder.options.get(1).getText().toString());
                    bundle.putString("optionC", holder.options.get(2).getText().toString());
                    bundle.putString("optionD", holder.options.get(3).getText().toString());
                    bundle.putString("optionE", holder.options.get(4).getText().toString());
                    bundle.putInt("correctOptionIndex", questions.get(position).correctOptionIndex);
                    bundle.putString("attachmentPath", questions.get(position).getAttachmentPath());
                    MenuActivity.instance.loadFragment(R.id.nav_add_question, (Activity)context, bundle);

                    MenuActivity.questions.remove(questions.get(position));
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, questions.size());
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure?");
                    builder.setMessage("This question will be deleted. Are you sure?");
                    builder.setNegativeButton("No", null);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MenuActivity.questions.remove(questions.get(position));
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, questions.size());
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
