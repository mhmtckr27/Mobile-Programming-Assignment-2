package tr.edu.yildiz.mehmethayricakir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;

public class MainActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button login;
    Button signup;
    static ArrayList<User> users;
    int currentLoginAttemptCount = 0;
    int maxLoginAttemptCount = 3;

    //sonradan internal storage'a tasi, kullanicinin kolay erisemeyecegi bir yere
    public static final String usersPath = "/storage/emulated/0/Download/Users";
    public static final String photosPath = "/storage/emulated/0/Download/Users/Photos";
    //public static final String usersPath = Environment.getDataDirectory() + "/Users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindVariables();
        bindButtons();
        if(users == null){
            users = createUsers();
        }
        File usersDirectory = new File(usersPath);
        File photosDirectory = new File(photosPath);
        if (!usersDirectory.exists()) {
            usersDirectory.mkdir();
        }
        if(!photosDirectory.exists()){
            photosDirectory.mkdir();
        }
    }


    private void bindVariables() {
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        login = findViewById(R.id.login_button);
        signup = findViewById(R.id.signup_button);
    }

    private void bindButtons(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginAttempt(v);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignupActivity.class);
                intent.putExtra("emailAddress", email.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void onLoginAttempt(View view){
        if(isCredentialsValid()){
            //loginsuccessfull
            currentLoginAttemptCount = 0;
            Toast.makeText(MainActivity.this, "Welcome onboard " + email.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(view.getContext(), MenuActivity.class);
            intent.putExtra("userEmailAddress", email.getText().toString());
        }
        password.setText("");
        Toast.makeText(this, "Wrong e-mail and/or password!", Toast.LENGTH_SHORT).show();
        currentLoginAttemptCount++;
        if(currentLoginAttemptCount >= maxLoginAttemptCount){
            login.setEnabled(false);
        }
    }

    private boolean isCredentialsValid(){
        for (User user : users) {
            if(user.getEmail().equals(email.getText().toString()) && user.getPassword().equals(User.md5(password.getText().toString()))){
                return true;
            }
        }
        return false;
    }

    public static ArrayList<User> createUsers() {
        users = new ArrayList<>();
        Uri uri_path = Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + R.drawable.feyyaz);
        users.add(new User("Feyyaz", "Yiğit", "feyyazyigit@gmail.com", "5431234567", new Date(), User.md5("sevgi"), uri_path));
        uri_path = Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + R.drawable.amanda);
        users.add(new User("Amanda", "Schiavone", "amyy@gmail.com", "3265002545", new Date(), User.md5("scamand1992"), uri_path));
        uri_path = Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + R.drawable.atreus);
        users.add(new User("Atreus", "the Demigod", "atreusthearcher@gmail.com", "5963211236", new Date(), User.md5("whymydadhatesme"), uri_path));
        uri_path = Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + R.drawable.kratos);
        users.add(new User("Kratos", "the God", "kratos@gmail.com", "7456952312", new Date(), User.md5("imissmywife"), uri_path));
        uri_path = Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + R.drawable.freya);
        users.add(new User("Freya", "the Goddess", "freya@gmail.com", "5496322585", new Date(), User.md5("mydearbaldur"), uri_path));
        uri_path = Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + R.drawable.geraltofrivia);
        users.add(new User("Geralt", "of Rivia", "geralt@gmail.com", "1285632145", new Date(), User.md5("trissciriroach"), uri_path));
        uri_path = Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + R.drawable.joelmiller);
        users.add(new User("Joel", "Miller", "miller.joel@gmail.com", "5463214587", new Date(), User.md5("sarahellie"), uri_path));
        uri_path = Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + R.drawable.johnnysilverhand);
        users.add(new User("Johnny", "Silverhand", "silverhandjohnny@gmail.com", "7456542136", new Date(), User.md5("arasakamustdie"), uri_path));
        uri_path = Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + R.drawable.walterwhite);
        users.add(new User("Walter", "White", "castersugar7_24@gmail.com", "2561455222", new Date(), User.md5("mrheisenberg"), uri_path));
        uri_path = Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + R.drawable.panampalmer);
        users.add(new User("Panam", "Palmer", "panam@gmail.com", "5426589666", new Date(), User.md5("nomadlife4ever"), uri_path));
        return users;
    }
}