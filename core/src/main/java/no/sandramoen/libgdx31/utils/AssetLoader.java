package no.sandramoen.libgdx31.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.FWSkin;
import com.github.tommyettinger.textra.FWSkinLoader;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Styles;

public class AssetLoader implements AssetErrorListener {

    public static TextureAtlas textureAtlas;
    public static FWSkin mySkin;

    public static String defaultShader;
    public static String shockwaveShader;
    public static String backgroundShader;

    public static Sound click1Sound;
    public static Sound hoverOverEnterSound;
    public static Sound bubbleSound;
    public static Sound gongSound;
    public static Sound healthLossSound;
    public static Sound heartbeatSound;
    public static Array<Sound> swordSounds;
    public static Sound longHeartbeatSound;
    public static Sound openDoorSound;

    public static Array<Music> music;
    public static Music levelMusic;
    public static Music levelSelectMusic;

    public static Array<TiledMap> maps;
    public static TiledMap testMap;
    public static TiledMap level1;
    public static TiledMap level2;
    public static TiledMap currentLevel;

    static {
        long time = System.currentTimeMillis();
        BaseGame.assetManager = new AssetManager();
        BaseGame.assetManager. setLoader(Skin. class, new FWSkinLoader(BaseGame.assetManager. getFileHandleResolver()));
        BaseGame.assetManager.setErrorListener(new AssetLoader());

        loadAssets();
        BaseGame.assetManager.finishLoading();
        assignAssets();

        Gdx.app.log(AssetLoader.class.getSimpleName(), "Asset manager took " + (System.currentTimeMillis() - time) + " ms to load all game assets.");
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(AssetLoader.class.getSimpleName(), "Could not load asset: " + asset.fileName, throwable);
    }

    public static Styles.LabelStyle getLabelStyle(String fontName) {
        return new Styles.LabelStyle(
            new Font(
                AssetLoader.mySkin.get(fontName, Font.class)
            ), Color.WHITE);
    }

    private static void loadAssets() {
        // images
        BaseGame.assetManager.setLoader(Text.class, new TextLoader(new InternalFileHandleResolver()));
        BaseGame.assetManager.load("images/included/packed/images.pack.atlas", TextureAtlas.class);

        // music
        BaseGame.assetManager.load("audio/music/388340__phlair__dungeon-ambiance.ogg", Music.class);
        BaseGame.assetManager.load("audio/music/512360__jackylacracotte__epic-ambient-track.ogg", Music.class);

        // sounds
        BaseGame.assetManager.load("audio/sound/click1.wav", Sound.class);
        BaseGame.assetManager.load("audio/sound/hoverOverEnter.wav", Sound.class);
        BaseGame.assetManager.load("audio/sound/104940__glaneur-de-sons__bubble-1.ogg", Sound.class);
        BaseGame.assetManager.load("audio/sound/486629__jenszygar__gong-brilliant-paiste-32.ogg", Sound.class);
        BaseGame.assetManager.load("audio/sound/563916__gemesil__bad-omen.ogg", Sound.class);
        BaseGame.assetManager.load("audio/sound/149834__latzii__heartbeat.ogg", Sound.class);
        for (int i = 0; i <= 13; i++) {
            BaseGame.assetManager.load("audio/sound/sword sounds/" + i + ".ogg", Sound.class);
        }
        BaseGame.assetManager.load("audio/sound/149834__latzii__heartbeat_long.wav", Sound.class);
        BaseGame.assetManager.load("audio/sound/open door sound.ogg", Sound.class);

        // i18n

        // shaders
        BaseGame.assetManager.load(new AssetDescriptor("shaders/default.vs", Text.class, new TextLoader.TextParameter()));
        BaseGame.assetManager.load(new AssetDescriptor("shaders/shockwave.fs", Text.class, new TextLoader.TextParameter()));
        BaseGame.assetManager.load(new AssetDescriptor("shaders/voronoi.fs", Text.class, new TextLoader.TextParameter()));

        // skins

        // fonts

        // tiled maps
        BaseGame.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        BaseGame.assetManager.load("maps/test.tmx", TiledMap.class);
        BaseGame.assetManager.load("maps/level1.tmx", TiledMap.class);
        BaseGame.assetManager.load("maps/level2.tmx", TiledMap.class);

        // other
        // BaseGame.assetManager.load(AssetDescriptor("other/jentenavn.csv", Text::class.java, TextLoader.TextParameter()))
    }

    private static void assignAssets() {
        // images
        textureAtlas = BaseGame.assetManager.get("images/included/packed/images.pack.atlas");

        // music
        music = new Array();
        levelSelectMusic = BaseGame.assetManager.get("audio/music/388340__phlair__dungeon-ambiance.ogg", Music.class);
        music.add(levelSelectMusic);
        levelMusic = BaseGame.assetManager.get("audio/music/512360__jackylacracotte__epic-ambient-track.ogg", Music.class);
        music.add(levelMusic);

        // sounds
        click1Sound = BaseGame.assetManager.get("audio/sound/click1.wav", Sound.class);
        hoverOverEnterSound = BaseGame.assetManager.get("audio/sound/hoverOverEnter.wav", Sound.class);
        bubbleSound = BaseGame.assetManager.get("audio/sound/104940__glaneur-de-sons__bubble-1.ogg", Sound.class);
        gongSound = BaseGame.assetManager.get("audio/sound/486629__jenszygar__gong-brilliant-paiste-32.ogg", Sound.class);
        healthLossSound = BaseGame.assetManager.get("audio/sound/563916__gemesil__bad-omen.ogg", Sound.class);
        heartbeatSound = BaseGame.assetManager.get("audio/sound/149834__latzii__heartbeat.ogg", Sound.class);
        swordSounds = new Array();
        for (int i = 0; i <= 13; i++) {
            Sound sound = BaseGame.assetManager.get("audio/sound/sword sounds/" + i + ".ogg", Sound.class);
            swordSounds.add(sound);
        }
        longHeartbeatSound = BaseGame.assetManager.get("audio/sound/149834__latzii__heartbeat_long.wav", Sound.class);
        openDoorSound = BaseGame.assetManager.get("audio/sound/open door sound.ogg", Sound.class);

        // i18n

        // shaders
        defaultShader = BaseGame.assetManager.get("shaders/default.vs", Text.class).getString();
        shockwaveShader = BaseGame.assetManager.get("shaders/shockwave.fs", Text.class).getString();
        backgroundShader = BaseGame.assetManager.get("shaders/voronoi.fs", Text.class).getString();

        // skins
        mySkin = new FWSkin(Gdx.files.internal("skins/mySkin/mySkin.json"));

        // fonts
        loadFonts();

        // tiled maps
        loadTiledMap();

        // other
    }

    private static void loadFonts() {
        float scale = Gdx.graphics.getWidth() * .05f; // magic number ensures scale ~= 1, based on screen width
        scale *= 1.01f; // make x percent bigger, bigger = more fuzzy

        mySkin.get("Play-Bold20white", Font.class).scale(scale);
        mySkin.get("Play-Bold40white", Font.class).scale(scale);
        mySkin.get("Play-Bold59white", Font.class).scale(scale);
    }

    private static void loadTiledMap() {
        testMap = BaseGame.assetManager.get("maps/test.tmx", TiledMap.class);
        level1 = BaseGame.assetManager.get("maps/level1.tmx", TiledMap.class);
        level2 = BaseGame.assetManager.get("maps/level2.tmx", TiledMap.class);

        maps = new Array();
        maps.add(testMap);
        maps.add(level1);
        maps.add(level2);
    }
}
