package com.zhinkk.mobilemmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends InputAdapter implements Screen {

	private static final int FRAME_COLS = 3;
	private static final int FRAME_ROWS = 4;
	/*
	SpriteBatch batch;
	Texture img;
	*/
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector3 touchPos;
	private MobileMMO game;
	private Viewport viewport;
	static final int WORLD_WIDTH = 100;
	static final int WORLD_HEIGHT = 100;
	private Music mainMusic;
	private AssetManager assetManager;
	private TiledMap mainMap;
	OrthogonalTiledMapRenderer renderer;
	private Texture spriteSheet;
	Animation<TextureRegion> spriteAnimation;
	private Sprite sprite;
	private PlayerMovement playerMovement;
	private TiledMapTileLayer walkableLayer;
	boolean drawRect;
	private Texture selectedSuccessRect;
	boolean targetTileWalkable;

	public GameScreen(MobileMMO game) {
		this.game = game;

		// Create asset manager
		assetManager = new AssetManager();

		// Needed to be able to load TMX maps
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

		// Load assets into asset manager
		assetManager.load("tilemaps/main.tmx", TiledMap.class);
		assetManager.load("music/magical-story.mp3", Music.class);
		assetManager.load("sprites/sprite.png", Texture.class);
		assetManager.load("sprites/selected_success.png", Texture.class);
		assetManager.load("sprites/selected_failure.png", Texture.class);


		// Synchronously (block) load assets. TODO: Loading screen, while asynchronously loading assets with manager.update()
		assetManager.finishLoading();
		mainMap = assetManager.get("tilemaps/main.tmx");
		walkableLayer = (TiledMapTileLayer) mainMap.getLayers().get("Walkable");

		// Rectangles for drawing target position
		 selectedSuccessRect = assetManager.get("sprites/selected_success.png");


		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(12, 12, camera);

		// Render tile map and its layers
		// 1/32 unit-scale allows us to map one "world unit" to exactly 1 32x32 tile.
		float unitScale = 1 / 32f;
		renderer = new OrthogonalTiledMapRenderer(mainMap, unitScale);

		sprite = new Sprite((Texture) assetManager.get("sprites/sprite.png"));
		sprite.setPosition(0, 0);
		sprite.setSize(1, 2);

		playerMovement = new PlayerMovement(sprite, camera);
		InputMultiplexer multiplexer = new InputMultiplexer();
		// multiplexer.addProcessor(new MyUiInputProcessor());
		multiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(multiplexer);

		touchPos = new Vector3();

	}


	@Override
	public void render (float delta) {
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// Render tilemap
		renderer.setView(camera);
		renderer.render();

		// Tell the SpriteBatch to render in the  coordinate system specified by the camera.
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		sprite.draw(game.batch);

		// Handle player movement
		playerMovement.handleMovement();

		// Make camera follow player
		camera.position.set(sprite.getX(), sprite.getY(), 0);

		// Draw green rect until player is done moving
		if (playerMovement.isMoving()) {
			game.batch.draw(selectedSuccessRect, Math.round(touchPos.x), Math.round(touchPos.y), 1, 1);
		}



		game.batch.end();


	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();

	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		mainMusic = assetManager.get("music/magical-story.mp3");
		mainMusic.setLooping(true);
		mainMusic.play();

	}


	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		// TODO: Determine for sure if player is intending to move
		touchPos.set(x, y, 0);
		camera.unproject(touchPos);
		Tile targetPos = new Tile(Math.round(touchPos.x), Math.round(touchPos.y));

		/* Determine if tapped tile is walkable. This is done in two steps:
		 * 1. Check if tile is actually a "walkable" tile (as defined in the tilemap). E.g., a road.
		 * 2. Determine if a path exists from user to target. findpath() will also generate the shortest
		 *    path using the A* algorithm.
		 *
		 */
		if (walkableLayer.getCell(targetPos.x, targetPos.y) != null && playerMovement.findPath(targetPos, walkableLayer)) {
			Gdx.app.log("gamescreen", "You can walk here.");

			// Move player to that tile
			playerMovement.moveTo(targetPos, walkableLayer);

		} else {
			Gdx.app.log("gamescreen", "You can't walk here.");
		}

		return true; // return true to indicate the event was handled
	}

}
