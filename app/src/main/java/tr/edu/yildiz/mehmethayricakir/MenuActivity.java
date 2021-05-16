package tr.edu.yildiz.mehmethayricakir;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static android.view.View.GONE;

public class MenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static User currentUser;
    public static ArrayList<Question> questions = new ArrayList<>();
    public static MenuActivity instance;
    FloatingActionButton fab;
    FloatingActionButton fab_q;
    FloatingActionButton fab_e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab_q = findViewById(R.id.fab_q);
        fab_e = findViewById(R.id.fab_e);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_list_questions, R.id.nav_exam_prefs)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        currentUser = MainActivity.users.get( getIntent().getExtras().getInt("userIndex"));

        TextView userNameSurname = navigationView.getHeaderView(0).findViewById(R.id.user_name_surname);
        TextView userEmail = navigationView.getHeaderView(0).findViewById(R.id.user_email);
        ImageView userImage = navigationView.getHeaderView(0).findViewById(R.id.user_image);

        String nameSurname = currentUser.getName() + " " + currentUser.getSurname();
        userNameSurname.setText(nameSurname);
        userEmail.setText(currentUser.getEmail());
        SetUserImage(userImage);

        File userQuestionsDirectory = new File(getApplicationContext().getFilesDir() + "/Questions/" + currentUser.getEmail());
        if(!userQuestionsDirectory.exists()){
            userQuestionsDirectory.mkdir();
        }
        questions = new ArrayList<>();
        readQuestionsFromFile();
        instance = this;
        bindButtons();
    }

    private void bindButtons(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fab_q.getVisibility() == GONE){
                    expandFabs(R.drawable.ic_x, View.VISIBLE);
                }
                else if(fab_q.getVisibility() == View.VISIBLE){
                    expandFabs(R.drawable.ic_plus, GONE);
                }
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        fab_q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(R.id.nav_add_question, MenuActivity.this, null);
                expandFabs(R.drawable.ic_plus, GONE);
                fab.setVisibility(GONE);
                fab_q.setVisibility(GONE);
                fab_e.setVisibility(GONE);
            }
        });

        fab_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(R.id.nav_add_exam, MenuActivity.this, null);
                expandFabs(R.drawable.ic_plus, GONE);
                fab.setVisibility(GONE);
                fab_q.setVisibility(GONE);
                fab_e.setVisibility(GONE);
            }
        });
    }

    private void expandFabs(int p, int visible) {
        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), p, null));
        fab_q.setVisibility(visible);
        fab_e.setVisibility(visible);
    }

    public void loadFragment(int layoutId, Activity activity, Bundle bundle) {
        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(layoutId, bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        writeQuestionsToFile();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        writeQuestionsToFile();
    }

    public void writeQuestionsToFile() {
        FileOutputStream fos = null;
        try {
            File questionsFile = new File(getApplicationContext().getFilesDir() + "/Questions/" + MenuActivity.currentUser.getEmail() + "/questions");
            questionsFile.delete();
            questionsFile.createNewFile();

            fos = new FileOutputStream(getApplicationContext().getFilesDir() + "/Questions/" + MenuActivity.currentUser.getEmail() + "/questions", true);
            ObjectOutputStream os = new ObjectOutputStream(fos);;
            for(Question newQuestion : MenuActivity.questions){
                os.writeObject(newQuestion);
            }
            if (os != null) {
                os.close();
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void readQuestionsFromFile() {
        FileInputStream fis = null;
        try {
            /*File questionsFile = new File(getApplicationContext().getFilesDir() + "/Questions/" + MenuActivity.currentUser.getEmail() + "/questions");
            questionsFile.createNewFile();*/
            fis = new FileInputStream(getApplicationContext().getFilesDir() + "/Questions/" + MenuActivity.currentUser.getEmail() + "/questions");
            ObjectInputStream is = new ObjectInputStream(fis);;
            try{
                while(true){
                    Question question = (Question) is.readObject();
                    questions.add(question);
                    //fis = new FileInputStream(getApplicationContext().getFilesDir() + "/Questions/" + MenuActivity.currentUser.getEmail() + "/questions");
                }
            }
            catch (EOFException e){
                e.printStackTrace();
                if (is != null) {
                    is.close();
                }
                fis.close();
            }
        }
        catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

        public void SetUserImage(ImageView userImage){
        File imgFile = new File(currentUser.getPhotoPath());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            userImage.setImageBitmap(myBitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem logOut = menu.findItem(R.id.action_log_out);
        logOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        //I don't want the back button to do anything.
    }
}