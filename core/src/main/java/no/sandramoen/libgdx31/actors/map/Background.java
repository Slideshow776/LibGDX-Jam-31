package no.sandramoen.libgdx31.actors.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdx31.utils.AssetLoader;
import no.sandramoen.libgdx31.utils.BaseActor;


public class Background extends BaseActor {
    private Array<TextureAtlas.AtlasRegion> animationImages = new Array();

    public Background(float x, float y, Stage stage) {
        super(x, y, stage);
        animationImages.add(AssetLoader.textureAtlas.findRegion("grass"));
        animation = new Animation(2f, animationImages, Animation.PlayMode.LOOP);
        setAnimation(animation);/*
        setSize(TiledMapActor.mapWidth, TiledMapActor.mapHeight);*/
    }
}
