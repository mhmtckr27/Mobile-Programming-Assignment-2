package tr.edu.yildiz.mehmethayricakir.ui.addquestion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tr.edu.yildiz.mehmethayricakir.MenuActivity;
import tr.edu.yildiz.mehmethayricakir.Question;
import tr.edu.yildiz.mehmethayricakir.R;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AddQuestionFragment extends Fragment {
    public View root;
    LinearLayout linearLayout;
    EditText question;
    EditText optionA;
    EditText optionB;
    EditText optionC;
    EditText optionD;
    EditText optionE;
    RadioGroup correctOption;
    TextView attachedFileName;
    ImageView attachedImage;
    VideoView attachedVideo;
    Button attach;
    Button addQuestion;
    Uri uri = Uri.EMPTY;
    boolean isVideoReadyToPlay = false;

    String questionAddedText = "Question is added successfully!";
    static final int ATTACH_FILE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_question, container, false);
        bindVariables();
        bindButtons();
        if(getArguments() != null){
            setFields(getArguments());
        }
        return root;
    }

    private void setFields(Bundle args){
        question.setText(args.getString("question"));
        optionA.setText(args.getString("optionA"));
        optionB.setText(args.getString("optionB"));
        optionC.setText(args.getString("optionC"));
        optionD.setText(args.getString("optionD"));
        optionE.setText(args.getString("optionE"));
        correctOption.clearCheck();
        ((RadioButton) correctOption.getChildAt(args.getInt("correctOptionIndex"))).setChecked(true);
        attachedFileName.setText(args.getString("attachmentPath"));
        if(attachedFileName.getText().toString() != ""){
            uri = Uri.fromFile(new File(attachedFileName.getText().toString()));
            onAttachMedia(null, uri);
        }
        addQuestion.setText("UPDATE");
        questionAddedText = "Question is updated successfully!";
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
                if(isInputsValid()){
                    Question newQuestion = new Question(question.getText().toString(), optionA.getText().toString(), optionB.getText().toString(), optionC.getText().toString(), optionD.getText().toString(), optionE.getText().toString(), "", correctOptionIndex);
                    if(uri != Uri.EMPTY){
                        newQuestion.setAttachmentPath(copyQuestionAttachment(uri));
                    }
                    Toast.makeText(getContext(), questionAddedText, Toast.LENGTH_LONG).show();
                    MenuActivity.questions.add(newQuestion);
                    clearFields();
                    if(questionAddedText.equals("Question is updated successfully!")){
                        MenuActivity.instance.loadFragment(R.id.nav_list_questions, getActivity(), null);
                    }
                }
                else{
                    Toast.makeText(getContext(), "Please fill in all fields!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        attachedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVideoReadyToPlay){
                    attachedImage.getOverlay().clear();
                    attachedImage.setVisibility(GONE);
                    attachedVideo.setVisibility(VISIBLE);
                    MediaController mediaController = new MediaController(attachedVideo.getContext());
                    attachedVideo.setMediaController(mediaController);
                    attachedVideo.start();
                }
            }
        });
    }

    private void clearFields() {
        question.setText("");
        optionA.setText("");
        optionB.setText("");
        optionC.setText("");
        optionD.setText("");
        optionE.setText("");
        attachedFileName.setText("");
        uri = Uri.EMPTY;
        correctOption.clearCheck();
        attachedImage.getOverlay().clear();
        attachedImage.setVisibility(GONE);
        attachedVideo.setVisibility(GONE);
        ((RadioButton) correctOption.getChildAt(0)).setChecked(true);
    }

    private String getUriFileName(Uri uri){
        Cursor returnCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String fileName = returnCursor.getString(nameIndex);
        returnCursor.close();
        return fileName;
    }

    private String copyQuestionAttachment(Uri attachmentUri) {
        String attachmentPath = "";
        try{
            attachmentPath = getActivity().getApplicationContext().getFilesDir() + "/Questions/" + MenuActivity.currentUser.getEmail() + "/" + getUriFileName(attachmentUri);
            copy(attachmentUri, new File(attachmentPath));
        }
        catch (Exception e){
            e.printStackTrace();
            return attachmentPath;
        }
        return attachmentPath;
    }

    public void copy(Uri src, File dst) throws IOException {
        try (InputStream in = getActivity().getContentResolver().openInputStream(src)) {
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

    private boolean isInputsValid() {
        return !question.getText().toString().equals("") &&
                !optionA.getText().toString().equals("") &&
                !optionB.getText().toString().equals("") &&
                !optionC.getText().toString().equals("") &&
                !optionD.getText().toString().equals("") &&
                !optionE.getText().toString().equals("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == ATTACH_FILE) && (resultCode == RESULT_OK)){
            if(data != null){
                onAttachMedia(data, null);
            }
        }
    }

    private void onAttachMedia(@Nullable Intent data, @Nullable Uri uri1) {
        if(data != null){
            uri = data.getData();
        }
        else if(uri1 != null){
            uri = uri1;
        }
        else{return;}
        attachedFileName.setText(uri.getPath());
        attachedFileName.setEnabled(true);

        attachedImage.getOverlay().clear();
        attachedImage.setVisibility(GONE);
        attachedVideo.setVisibility(GONE);

        switch (getMimeType(uri).split("/")[0]){
            case "image":
                attachedImage.setImageURI(uri);
                setViewSize(attachedImage);
                isVideoReadyToPlay = false;
                attachedImage.setVisibility(VISIBLE);
                break;
            case "video":
                attachedVideo.setVideoURI(uri);
                setViewSize(attachedVideo);

                Bitmap thumbnail_bitmap;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    thumbnail_bitmap = createThumbnail(getActivity(), uri.toString());
                } else {
                    thumbnail_bitmap = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                }
                attachedImage.setImageBitmap(thumbnail_bitmap);

                ViewGroup.LayoutParams lp = attachedVideo.getLayoutParams();
                ViewGroup.LayoutParams lp2 = attachedImage.getLayoutParams();
                lp2.width = lp.width;
                lp2.height = lp.height;
                attachedImage.setLayoutParams(lp2);
                overlayImage(lp2.width, lp2.height);
                isVideoReadyToPlay = true;
                attachedImage.setVisibility(VISIBLE);
                break;
            default:
                isVideoReadyToPlay = false;
                break;
        }
    }

    private void overlayImage(int width, int height) {
        ViewOverlay viewOverlay = attachedImage.getOverlay();
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_layered, null);
        Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_layered_bg, null);
        Rect bounds = new Rect();
        if(height > width){
            bounds.left = 0;
            bounds.right = width;
            bounds.top = height / 2 - width / 2;
            bounds.bottom = height / 2 + width / 2;
        }
        else{
            bounds.left = width / 2 - height / 2;
            bounds.right = width / 2 + height / 2;
            bounds.top = 0;
            bounds.bottom = height;
        }

        drawable.setBounds(bounds);

        bounds.left = 0;
        bounds.top = 0;
        bounds.right = width;
        bounds.bottom = height;
        background.setBounds(bounds);

        viewOverlay.add(background);
        viewOverlay.add(drawable);
    }

    public static Bitmap createThumbnail(Activity activity, String path) {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        Bitmap bitmap = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(activity, Uri.parse(path));
            bitmap = mediaMetadataRetriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
    public void setViewSize(View view){
        int maxWidth = linearLayout.getWidth();

        if(view instanceof VideoView){
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getContext(), Uri.parse(uri.toString()));
            int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            retriever.release();

            android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
            float aspectRatio = (float) height / width;
            lp.width = maxWidth;
            lp.height = (int) (aspectRatio * maxWidth);
            view.setLayoutParams(lp);

        }
        else if(view instanceof ImageView){
            android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
            int[] dims = getIMGSize(uri);
            float aspectRatio = (float) dims[1] / dims[0];
            lp.width = maxWidth;
            lp.height = (int) (aspectRatio * maxWidth);
            view.setLayoutParams(lp);
        }
    }

    private int[] getIMGSize(Uri uri){
        int[] dims = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            InputStream is = getContext().getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
            dims[0] = options.outWidth;
            dims[1] = options.outHeight;
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return dims;
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = getContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private void bindVariables(){
        linearLayout = root.findViewById(R.id.linear_layout);
        question = root.findViewById(R.id.question_edit_text);
        optionA = root.findViewById(R.id.option_a_edit_text);
        optionB = root.findViewById(R.id.option_b_edit_text);
        optionC = root.findViewById(R.id.option_c_edit_text);
        optionD = root.findViewById(R.id.option_d_edit_text);
        optionE = root.findViewById(R.id.option_e_edit_text);
        correctOption = root.findViewById(R.id.correct_option_radio_group);
        attachedImage = root.findViewById(R.id.attachment_image);
        attachedVideo = root.findViewById(R.id.attachment_video);
        attachedFileName = root.findViewById(R.id.attached_file_name_text);
        attachedFileName.setEnabled(false);
        attach = root.findViewById(R.id.attach_button);
        addQuestion = root.findViewById(R.id.add_question_button);
    }

}