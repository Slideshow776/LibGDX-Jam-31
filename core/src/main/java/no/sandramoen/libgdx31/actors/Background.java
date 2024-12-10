package no.sandramoen.libgdx31.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseActor;

public class Background extends BaseActor {
    private final String tag = "Background";
    private String vertexShaderCode;
    private String fragmentShaderCode;
    private ShaderProgram shaderProgram;
    private float time = 0.0f;
    private boolean disabled = false;
    private Vector3 colour;
    public float timeMultiplier = 1f;
    public float timeIncrement = 1.1f;
    public boolean increaseSpeed = false;
    public boolean decreaseSpeed = false;

    public Background(float x, float y, Stage s, Vector3 colour) {
        super(x, y, s);
        loadImage("whitePixel");

        setPosition(x, y);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        ShaderProgram.pedantic = false;
        vertexShaderCode = AssetLoader.defaultShader.toString();
        fragmentShaderCode = AssetLoader.backgroundShader.toString();
        shaderProgram = new ShaderProgram(vertexShaderCode, fragmentShaderCode);
        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("Background", "Shader compile error: " + shaderProgram.getLog());
        }
        this.colour = colour;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (disabled) {
            super.draw(batch, parentAlpha);
        } else if (true) {
            try {
                batch.setShader(shaderProgram);
                shaderProgram.setUniformf("u_time", time * timeMultiplier);
                shaderProgram.setUniformf("u_resolution", new Vector2(getWidth() * 0.125f, getHeight() * 0.125f));
                shaderProgram.setUniformf("u_color", colour);
                super.draw(batch, parentAlpha);
                batch.setShader(null);
            } catch (Throwable error) {
                super.draw(batch, parentAlpha);
            }
        }
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        time += dt;

        if (increaseSpeed) {
            timeMultiplier += 0.001f;
        }

        if (increaseSpeed && timeMultiplier >= timeIncrement) {
            increaseSpeed = false;
            timeIncrement += 0.1f;
        }

        if (decreaseSpeed && timeMultiplier >= 1f) {
            timeMultiplier -= 0.02f;
        }
    }

    public void restart() {
        timeMultiplier = 1f;
        timeIncrement = 1.1f;
    }
}
