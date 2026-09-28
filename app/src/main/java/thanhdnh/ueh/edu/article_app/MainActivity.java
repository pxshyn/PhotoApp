package thanhdnh.ueh.edu.article_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public GridView gridview;
    public ProgressBar progressBar;

    private final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getBaseContext(), ViewUserActivity.class);
            intent.putExtra("id", (int) gridview.getAdapter().getItemId(position));
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        gridview = findViewById(R.id.gridview);
        progressBar = findViewById(R.id.progressBar);

        String jsonUrl = "https://raw.githubusercontent.com/pxshyn/PhotoApp/main/users.json";
        new UserData(getBaseContext(), gridview).loadData(jsonUrl, this, progressBar);

        gridview.setOnItemClickListener(onItemClick);
    }
}
