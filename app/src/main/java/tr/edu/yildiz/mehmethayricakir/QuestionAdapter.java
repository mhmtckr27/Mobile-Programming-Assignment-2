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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tr.edu.yildiz.mehmethayricakir.ui.addquestion.AddQuestionFragment;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.UserViewHolder> {

    Context context;
    ArrayList<Question> questions;

    public QuestionAdapter(Context context, ArrayList<Question> questions){
        this.context = context;
        this.questions = questions;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView question;
        TextView optionA;
        TextView optionB;
        TextView optionC;
        TextView optionD;
        TextView optionE;
        RadioButton[] optionsRadioButtons = new RadioButton[5];
        TextView attachmentPath;

        Button delete;
        Button edit;

        public UserViewHolder(View v){
            super(v);
            question = v.findViewById(R.id.question);
            optionA = v.findViewById(R.id.option_a);
            optionB = v.findViewById(R.id.option_b);
            optionC = v.findViewById(R.id.option_c);
            optionD = v.findViewById(R.id.option_d);
            optionE = v.findViewById(R.id.option_e);
            attachmentPath = v.findViewById(R.id.attachment_path);
            optionsRadioButtons[0] = v.findViewById(R.id.radio_button_option_a);
            optionsRadioButtons[1] = v.findViewById(R.id.radio_button_option_b);
            optionsRadioButtons[2] = v.findViewById(R.id.radio_button_option_c);
            optionsRadioButtons[3] = v.findViewById(R.id.radio_button_option_d);
            optionsRadioButtons[4] = v.findViewById(R.id.radio_button_option_e);
            delete = v.findViewById(R.id.delete_button);
            edit = v.findViewById(R.id.edit_button);
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
        holder.question.setText(questions.get(position).getQuestion());

        holder.optionA.setText(questions.get(position).getOptionA());
        holder.optionB.setText(questions.get(position).getOptionB());
        holder.optionC.setText(questions.get(position).getOptionC());
        holder.optionD.setText(questions.get(position).getOptionD());
        holder.optionE.setText(questions.get(position).getOptionE());

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
            holder.attachmentPath.setVisibility(View.GONE);
        }
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("question", holder.question.getText().toString());
                bundle.putString("optionA", holder.optionA.getText().toString());
                bundle.putString("optionB", holder.optionB.getText().toString());
                bundle.putString("optionC", holder.optionC.getText().toString());
                bundle.putString("optionD", holder.optionD.getText().toString());
                bundle.putString("optionE", holder.optionE.getText().toString());
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

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
