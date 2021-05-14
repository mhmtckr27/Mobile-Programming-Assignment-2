package tr.edu.yildiz.mehmethayricakir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button login;
    Button signup;
    static ArrayList<User> users;
    int currentLoginAttemptCount = 0;
    int maxLoginAttemptCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindVariables();
        bindButtons();
        if(users == null){
            users = createUsers();
        }
        readUsersFromFile();
        File photosDirectory = new File(getApplicationContext().getFilesDir() + "/Photos");
        if(!photosDirectory.exists()){
            photosDirectory.mkdir();
        }
    }

    private void readUsersFromFile() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(getApplicationContext().getFilesDir() + "/users");
            ObjectInputStream is = new ObjectInputStream(fis);
            try{
                User user = (User) is.readObject();
                users.add(user);
            }catch (EOFException e){
                e.printStackTrace();
                is.close();
                fis.close();
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
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
            Intent intent = new Intent(view.getContext(), RecyclerViewActivity.class);
            intent.putExtra("userEmailAddress", email.getText().toString());
            startActivity(intent);
        }
        else{
            password.setText("");
            Toast.makeText(this, "Wrong e-mail and/or password!", Toast.LENGTH_SHORT).show();
            currentLoginAttemptCount++;
            if(currentLoginAttemptCount >= maxLoginAttemptCount){
                login.setEnabled(false);
            }
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

    public ArrayList<User> createUsers() {
        users = new ArrayList<>();
        users.add(new User("Feyyaz", "Yiğit", "feyyazyigit@gmail.com", "5431234567", new Date(), User.md5("sevgi"), getApplicationContext().getFilesDir() + "/Photos/feyyaz.png"));
        copyUserPhoto(R.drawable.feyyaz, getApplicationContext().getFilesDir() + "/Photos/feyyaz.png");

        users.add(new User("Amanda", "Schiavone", "amyy@gmail.com", "3265002545", new Date(), User.md5("scamand1992"), getApplicationContext().getFilesDir() + "/Photos/amanda.png"));
        copyUserPhoto(R.drawable.amanda, getApplicationContext().getFilesDir() + "/Photos/amanda.png");

        users.add(new User("Atreus", "the Demigod", "atreusthearcher@gmail.com", "5963211236", new Date(), User.md5("whymydadhatesme"), getApplicationContext().getFilesDir() + "/Photos/atreus.png"));
        copyUserPhoto(R.drawable.atreus, getApplicationContext().getFilesDir() + "/Photos/atreus.png");

        users.add(new User("Kratos", "the God", "kratos@gmail.com", "7456952312", new Date(), User.md5("imissmywife"), getApplicationContext().getFilesDir() + "/Photos/kratos.png"));
        copyUserPhoto(R.drawable.kratos, getApplicationContext().getFilesDir() + "/Photos/kratos.png");

        users.add(new User("Freya", "the Goddess", "freya@gmail.com", "5496322585", new Date(), User.md5("mydearbaldur"), getApplicationContext().getFilesDir() + "/Photos/freya.png"));
        copyUserPhoto(R.drawable.freya, getApplicationContext().getFilesDir() + "/Photos/freya.png");

        users.add(new User("Geralt", "of Rivia", "geralt@gmail.com", "1285632145", new Date(), User.md5("trissciriroach"), getApplicationContext().getFilesDir() + "/Photos/geraltofrivia.png"));
        copyUserPhoto(R.drawable.geraltofrivia, getApplicationContext().getFilesDir() + "/Photos/geraltofrivia.png");

        users.add(new User("Joel", "Miller", "miller.joel@gmail.com", "5463214587", new Date(), User.md5("sarahellie"), getApplicationContext().getFilesDir() + "/Photos/joelmiller.png"));
        copyUserPhoto(R.drawable.joelmiller, getApplicationContext().getFilesDir() + "/Photos/joelmiller.png");

        users.add(new User("Johnny", "Silverhand", "silverhandjohnny@gmail.com", "7456542136", new Date(), User.md5("arasakamustdie"), getApplicationContext().getFilesDir() + "/Photos/johnnysilverhand.png"));
        copyUserPhoto(R.drawable.johnnysilverhand, getApplicationContext().getFilesDir() + "/Photos/johnnysilverhand.png");

        users.add(new User("Walter", "White", "castersugar7_24@gmail.com", "2561455222", new Date(), User.md5("mrheisenberg"), getApplicationContext().getFilesDir() + "/Photos/walterwhite.png"));
        copyUserPhoto(R.drawable.walterwhite, getApplicationContext().getFilesDir() + "/Photos/walterwhite.png");

        users.add(new User("Panam", "Palmer", "panam@gmail.com", "5426589666", new Date(), User.md5("nomadlife4ever"), getApplicationContext().getFilesDir() + "/Photos/panampalmer.png"));
        copyUserPhoto(R.drawable.panampalmer, getApplicationContext().getFilesDir() + "/Photos/panampalmer.png");

        return users;
    }

    //hardcoded createUsers fonksiyonu icin eklendi, createUsers silinince bu ve copy fonksiyonu da silinebilir.
    private void copyUserPhoto(int photoId, String photoPath) {
        try{
            copy(Uri.parse("android.resource://tr.edu.yildiz.mehmethayricakir/" + photoId), new File(photoPath));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void copy(Uri src, File dst) throws IOException {
        try (InputStream in = getContentResolver().openInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}