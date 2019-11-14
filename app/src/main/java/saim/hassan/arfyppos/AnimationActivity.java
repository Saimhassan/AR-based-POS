package saim.hassan.arfyppos;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.SkeletonNode;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class AnimationActivity extends AppCompatActivity {

    private ModelAnimator modelAnimator;
    private int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        ArFragment arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            createModel(hitResult.createAnchor(),arFragment);
        });
    }
    private void createModel(Anchor anchor, ArFragment arFragment) {
        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("lap.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
                    transformableNode.setParent(anchorNode);
                    transformableNode.setRenderable(modelRenderable);
                    SkeletonNode skeletonNode = new SkeletonNode();
                    skeletonNode.setParent(anchorNode);
                    skeletonNode.setRenderable(modelRenderable);
                    arFragment.getArSceneView().getScene().addChild(anchorNode);
                    transformableNode.select();

                    Button btn = findViewById(R.id.btn);

                    btn.setOnClickListener(v -> animateModel(modelRenderable));

                });
    }

    private void animateModel(ModelRenderable modelRenderable) {

        if (modelAnimator != null && modelAnimator.isRunning())
            modelAnimator.end();
        int animationCount = modelRenderable.getAnimationDataCount();
        if (i == animationCount)
            i = 0;
        AnimationData animationData = modelRenderable.getAnimationData(i);
        modelAnimator = new ModelAnimator(animationData,modelRenderable);
        modelAnimator.start();
        i++;


    }
    }
