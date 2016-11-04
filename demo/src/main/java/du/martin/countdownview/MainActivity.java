package du.martin.countdownview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import du.martin.library.GWProgressView;

public class MainActivity extends AppCompatActivity {

    float progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final GWProgressView progressView = ((GWProgressView) findViewById(R.id.progress));

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressView.setProgress(progress += 0.02f);
            }
        });
    }
}
