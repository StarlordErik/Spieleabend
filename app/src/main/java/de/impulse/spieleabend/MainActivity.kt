package de.impulse.spieleabend;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView helloWorld = new TextView(this);
        helloWorld.setGravity(Gravity.CENTER);
        helloWorld.setText("Hello World!");
        helloWorld.setTextSize(24);

        setContentView(helloWorld);
    }
}
