package thanhdnh.ueh.edu.article_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    private ArrayList<UserProfile> userList;
    private Context context;

    public UserAdapter(ArrayList<UserProfile> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.user_disp_tpl, parent, false);
            viewHolder.iv_photo = convertView.findViewById(R.id.imv_photo);
            viewHolder.tv_caption = convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserProfile user = userList.get(position);

        if (user.getAvatar_url() != null && !user.getAvatar_url().isEmpty()) {
            Picasso.get()
                    .load(user.getAvatar_url())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .resize(250, 250)
                    .centerCrop()
                    .transform(new CircleTransform())
                    .into(viewHolder.iv_photo);
        } else {
            viewHolder.iv_photo.setImageResource(R.drawable.ic_launcher_background);
        }

        viewHolder.tv_caption.setText(user.getUsername());
        return convertView;
    }

    private static class ViewHolder {
        ImageView iv_photo;
        TextView tv_caption;
    }
}
