package saim.hassan.arfyppos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ModelAnimation extends AppCompatActivity {

    Button btnhp,btnclock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_animation);

        btnhp = (Button)findViewById(R.id.hp);
        btnhp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent anim = new Intent(ModelAnimation.this,AnimationActivity.class);
                startActivity(anim);

            }
        });
        btnclock = (Button)findViewById(R.id.clock);
        btnclock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
