package tr.edu.yildiz.mehmethayricakir;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import static tr.edu.yildiz.mehmethayricakir.User.md5;

public class SignupActivity extends AppCompatActivity implements Serializable {
    EditText name;
    EditText surname;
    EditText email;
    EditText phoneNumber;
    EditText birthDate;
    EditText password;
    EditText reEnterPassword;
    Button signUp;
    TextView login;
    TextView attachedFileName;
    Button attachPhoto;
    static final int ATTACH_FILE = 1;
    Uri photoUri;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
        password = findViewById(R.id.password_edit_text);
        reEnterPassword = findViewById(R.id.reenter_password_edit_text);
        signUp = findViewById(R.id.signup_button);
        login = findViewById(R.id.login_button);
        attachPhoto = findViewById(R.id.attach_photo_button);
        attachedFileName = findViewById(R.id.attached_file_name_text);
        birthDate = findViewById(R.id.birth_date_edit_text);
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
                // verifyStoragePermissions(SignupActivity.this);
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimeTypes = {"image/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.putExtra("return-data", true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), ATTACH_FILE);
            }
        });
        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        TextWatcher tw = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    birthDate.setText(current);
                    birthDate.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        birthDate.addTextChangedListener(tw);
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

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void onSignupAttempt(View view) {
        if (!isUserExists()) {
            if (isCredentialsValid()) {
                //signup successful
                String[] bdate = birthDate.getText().toString().split("/");
                int day = Integer.parseInt(bdate[0]);
                int month = Integer.parseInt(bdate[1]);
                int year = Integer.parseInt(bdate[2]);

                String photoPath = getApplicationContext().getFilesDir() + "/Photos/" + email.getText().toString() + ".png";
                User user = new User(name.getText().toString(), surname.getText().toString(), email.getText().toString(), phoneNumber.getText().toString(), new Date(year, month, day), md5(password.getText().toString()), photoPath);
                if (copyUserPhoto()) return;

                if(addUser(user)){
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    MainActivity.users.add(user);
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    /*Thread thread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(Toast.LENGTH_SHORT);
                                SignupActivity.this.finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();*/
                    finish();
                }
                else{
                    Toast.makeText(SignupActivity.this, "Signup failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean copyUserPhoto() {
        try{
            copy(photoUri, new File(getApplicationContext().getFilesDir(), "/Photos/" + email.getText().toString() + ".png"));
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(SignupActivity.this, "Signup failed! (Select photo error)", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public boolean addUser(User user){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getApplicationContext().getFilesDir() + "/users", true);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(user);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}