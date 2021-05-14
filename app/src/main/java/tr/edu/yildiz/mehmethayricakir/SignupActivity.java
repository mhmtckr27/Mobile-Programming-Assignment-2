package tr.edu.yildiz.mehmethayricakir;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;

import static tr.edu.yildiz.mehmethayricakir.User.md5;

public class SignupActivity extends AppCompatActivity implements Serializable {
    EditText name;
    EditText surname;
    EditText email;
    EditText phoneNumber;
    DatePicker birthDate;
    EditText password;
    EditText reEnterPassword;
    Button signUp;
    TextView login;
    TextView attachedFileName;
    Button attachPhoto;
    static final int ATTACH_FILE = 1;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        bindVariables();
        bindButtons();
        if (getIntent().getExtras() != null) {
            email.setText(getIntent().getExtras().getString("emailAddress", ""));
        }
    }

    private void bindVariables() {
        name = findViewById(R.id.name_edit_text);
        surname = findViewById(R.id.surname_edit_text);
        email = findViewById(R.id.email_edit_text);
        phoneNumber = findViewById(R.id.phone_number_edit_text);
        birthDate = findViewById(R.id.birth_date_picker);
        password = findViewById(R.id.password_edit_text);
        reEnterPassword = findViewById(R.id.reenter_password_edit_text);
        signUp = findViewById(R.id.signup_button);
        login = findViewById(R.id.login_button);
        attachPhoto = findViewById(R.id.attach_photo_button);
        attachedFileName = findViewById(R.id.attached_file_name_text);
    }

    private void bindButtons() {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignupAttempt(v);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        attachPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimeTypes = { "image/*" };
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.putExtra("return-data", true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), ATTACH_FILE);
            }
        });
        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == ATTACH_FILE) && (resultCode == RESULT_OK)){
            if(data != null){
                photoUri = data.getData();
                attachedFileName.setText(photoUri.getLastPathSegment());
                attachedFileName.setEnabled(true);
            }
        }
    }
    private void onSignupAttempt(View view) {

        if (!isUserExists()) {
            if (isCredentialsValid()) {
                //signup successful
                int year = birthDate.getYear();
                int month = birthDate.getMonth() + 1;
                int day = birthDate.getDayOfMonth();
                //MainActivity.users.add(new User(name.getText().toString(), surname.getText().toString(), email.getText().toString(), phoneNumber.getText().toString(), new Date(year, month, day), md5(password.getText().toString()), photoUri));
                User user = new User(name.getText().toString(), surname.getText().toString(), email.getText().toString(), phoneNumber.getText().toString(), new Date(year, month, day), md5(password.getText().toString()), photoUri);
                if(addUser(user)){
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(SignupActivity.this, "Signup failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public boolean addUser(User user){
        try{
            copy(user.getPhoto_uri(), new File(MainActivity.photosPath + "/" + email.getText().toString() + ".png"));
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

      /*  FileOutputStream fos = null;
        try {
            fos = getApplicationContext().openFileOutput(MainActivity.usersPath + "/", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(user);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }*/
        return true;
    }

    public void copy(Uri photoUri, File dst) throws IOException {

        try (InputStream in = getContentResolver().openInputStream(photoUri);) {
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

    private boolean isCredentialsValid() {
        if (name.getText().toString().equals("")) {
            Toast.makeText(SignupActivity.this, "Name can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (surname.getText().toString().equals("")) {
            Toast.makeText(SignupActivity.this, "Surname can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.getText().toString().equals("")) {
            Toast.makeText(SignupActivity.this, "Email address can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneNumber.getText().toString().equals("")) {
            Toast.makeText(SignupActivity.this, "Phone number can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.getText().toString().equals("")) {
            Toast.makeText(SignupActivity.this, "Password can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reEnterPassword.getText().toString().equals("")) {
            Toast.makeText(SignupActivity.this, "Reenter password can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.getText().toString().equals(reEnterPassword.getText().toString())){
            Toast.makeText(SignupActivity.this, "Passwords does not match!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(photoUri == null || photoUri.equals(Uri.EMPTY)){
            Toast.makeText(SignupActivity.this, "Photo can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isUserExists() {
        for (User user : MainActivity.users) {
            if (user.getEmail().equals(email.getText().toString())) {
                Toast.makeText(SignupActivity.this, "User with this email address already exists!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        for(User user : MainActivity.users){
            if(user.getPhoneNumber().equals(phoneNumber.getText().toString())){
                Toast.makeText(SignupActivity.this, "User with this phone number already exists!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
}