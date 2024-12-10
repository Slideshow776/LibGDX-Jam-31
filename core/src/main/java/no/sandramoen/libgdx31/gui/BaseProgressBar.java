package no.sandramoen.libgdx31.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseActor;

public class BaseProgressBar extends BaseActor {

    private BaseActor progress;
    private TypingLabel label;

    private int level = 0; // Store level as an integer from 0 to 100

    public BaseProgressBar(float x, float y, Stage stage) {
        super(0f, 0f, stage);

        loadImage("whitePixel");
        setColor(new Color(0.035f, 0.039f, 0.078f, 1f));
        setSize(Gdx.graphics.getWidth() * 0.9f, Gdx.graphics.getHeight() * 0.05f);
        setPosition(x, y - getHeight());

        progress = new BaseActor(0f, 0f, stage);
        progress.loadImage("whitePixel");
        progress.setColor(new Color(0.875f, 0.518f, 0.647f, 1f)); // light pink
        progress.setSize(0.0f, getHeight());
        addActor(progress);

        label = new TypingLabel("" + level + " / 100", AssetLoader.getLabelStyle("Play-Bold59white"));
        label.setColor(new Color(0.922f, 0.929f, 0.914f, 1f)); // white
        label.getFont().scale(0.5f, 0.5f);
        label.setPosition(
            getWidth() / 2 - label.getPrefWidth() / 4,
            (getHeight() / 2) * 1.1f
        );
        addActor(label);
    }

    // Increment the progress bar by a certain percentage (in integer values)
    public void incrementPercentage(int percentage) {
        // Make sure the percentage is within the valid range [0, 100]
        level = Math.min(level + percentage, 100); // Clamp to 100%

        // Calculate the new width based on the level percentage
        float newWidth = (float) level / 100 * getWidth();
        progress.addAction(Actions.sizeTo(newWidth, getHeight(), 0.25f));

        // Update the label with the new level
        label.setText(level + " / 100");
    }

    // Decrement the progress bar by a certain percentage (in integer values)
    public void decrementPercentage(int percentage) {
        // Make sure the percentage is within the valid range [0, 100]
        level = Math.max(level - percentage, 0); // Clamp to 0%

        // Calculate the new width based on the level percentage
        float newWidth = (float) level / 100 * getWidth();
        progress.addAction(Actions.sizeTo(newWidth, getHeight(), 0.25f));

        // Update the label with the new level
        label.setText(level + " / 100");
    }

    // Set the color of the progress bar
    public void setProgressBarColor(Color color) {
        if (progress != null) {
            progress.setColor(color);
        }
    }
}
