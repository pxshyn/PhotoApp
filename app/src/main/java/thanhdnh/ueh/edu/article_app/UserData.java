package thanhdnh.ueh.edu.article_app;

import android.app.Activity;
import android.content.Context;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UserData {
    public static UserList data;
    private Context context;
    private GridView gridview;

    public UserData(Context context, GridView gridview) {
        this.context = context;
        this.gridview = gridview;
    }

    public static UserProfile getUserFromId(int id) {
        if (data != null && data.getUsers() != null) {
            for (int i = 0; i < data.getUsers().size(); i++) {
                if (data.getUsers().get(i).getId() == id) {
                    return data.getUsers().get(i);
                }
            }
        }
        return null;
    }

    public void loadData(String url, Activity activity, ProgressBar progressBar) {
        Downloader.downloadWithProgress(url, context.getCacheDir(), progressBar, new Downloader.DownloadCallback() {
            @Override
            public void onDownloadSuccess(File downloadedFile) {
                activity.runOnUiThread(() -> {
                    String jsonText = readText(downloadedFile);
                    parseAndSetAdapter(jsonText, activity);
                });
            }

            @Override
            public void onDownloadFailed(Exception e) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(context, "Mạng không khả dụng. Đang tải từ bộ nhớ cục bộ...", Toast.LENGTH_SHORT).show();
                    String jsonText = readFromAssets("users.json");
                    parseAndSetAdapter(jsonText, activity);
                });
            }
        });
    }

    private void parseAndSetAdapter(String jsonText, Activity activity) {
        try {
            Gson gson = new Gson();
            data = gson.fromJson(jsonText, UserList.class);
            if (data != null && data.getUsers() != null) {
                UserAdapter adapter = new UserAdapter(data.getUsers(), context);
                gridview.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readText(File file) {
        StringBuilder buffer = new StringBuilder();
        try (InputStream stream = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    private String readFromAssets(String fileName) {
        StringBuilder buffer = new StringBuilder();
        try (InputStream is = context.getAssets().open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
