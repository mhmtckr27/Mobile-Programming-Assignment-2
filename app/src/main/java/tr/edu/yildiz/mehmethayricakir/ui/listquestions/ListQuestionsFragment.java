package tr.edu.yildiz.mehmethayricakir.ui.listquestions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import tr.edu.yildiz.mehmethayricakir.MenuActivity;
import tr.edu.yildiz.mehmethayricakir.Question;
import tr.edu.yildiz.mehmethayricakir.R;
import tr.edu.yildiz.mehmethayricakir.QuestionAdapter;

import static android.view.View.GONE;

public class ListQuestionsFragment extends Fragment {
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    ArrayList<Question> questions;
    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_list_questions, container, false);
        bindVariables();
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        if(fab != null){
            fab.setVisibility(GONE);
            getActivity().findViewById(R.id.fab_q).setVisibility(GONE);
            getActivity().findViewById(R.id.fab_e).setVisibility(GONE);
        }
        return root;
    }

    private void bindVariables() {
        recyclerView = root.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        questions = MenuActivity.questions;
        recyclerView.setAdapter(new QuestionAdapter(getActivity(), questions));
    }
}