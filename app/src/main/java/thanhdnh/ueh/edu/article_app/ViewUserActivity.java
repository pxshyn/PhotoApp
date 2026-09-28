package thanhdnh.ueh.edu.article_app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ViewUserActivity extends AppCompatActivity {
    ImageView iv_avatar;
    TextView tv_username, tv_id, tv_email, tv_tel, tv_hobby, tv_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        iv_avatar = findViewById(R.id.iv_detail_avatar);
        tv_username = findViewById(R.id.tv_detail_username);
        tv_id = findViewById(R.id.tv_detail_id);
        tv_email = findViewById(R.id.tv_detail_email);
        tv_tel = findViewById(R.id.tv_detail_tel);
        tv_hobby = findViewById(R.id.tv_detail_hobby);
        tv_description = findViewById(R.id.tv_detail_description);

        int id = getIntent().getIntExtra("id", 1);
        UserProfile user = UserData.getUserFromId(id);

        if (user != null) {
            tv_username.setText(user.getUsername());
            tv_id.setText("ID: " + user.getId());
            tv_email.setText("Email: " + user.getEmail());
            tv_tel.setText("Tel: " + user.getTel());
            tv_hobby.setText("Hobby: " + user.getHobby());
            tv_description.setText("Description: " + user.getDescription());

            if (user.getAvatar_url() != null && !user.getAvatar_url().isEmpty()) {
                Picasso.get()
                        .load(user.getAvatar_url())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .resize(300, 300)
                        .centerCrop()
                        .transform(new CircleTransform())
                        .into(iv_avatar);
            } else {
                iv_avatar.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
}
