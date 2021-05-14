package tr.edu.yildiz.mehmethayricakir;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context context;
    ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        ImageView userImage;
        TextView emailAddress;
        TextView password;
        ToggleButton showHideButton;
        public UserViewHolder(View v){
            super(v);
            userImage = v.findViewById(R.id.user_image);
            emailAddress = v.findViewById(R.id.user_email);
            password = v.findViewById(R.id.user_password);
            showHideButton = v.findViewById(R.id.show_hide_button);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.emailAddress.setText(users.get(position).getEmail());
        holder.password.setText(users.get(position).getPassword());

        File imgFile = new File(users.get(position).getPhotoPath());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.userImage.setImageBitmap(myBitmap);
        }

     //   holder.userImage.setImageResource(Uri.fromFile());
        holder.showHideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((ToggleButton) v).isChecked();
                if(isChecked){
                    holder.password.setText("****");
                }
                else{
                    holder.password.setText(users.get(position).getPassword());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}
