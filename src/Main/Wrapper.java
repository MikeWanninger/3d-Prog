package Main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.io.File;
import java.io.FileNotFoundException;

import javax.media.j3d.Alpha;
import javax.media.j3d.AudioDevice;
import javax.media.j3d.Background;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.LinearFog;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.PointSound;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class Wrapper extends JPanel {
	private final static int PWIDTH = 512;
	private final static int PHEIGHT = 512;

	private static final int BOUNDSIZE = 100;

	private Color3f skyColour = new Color3f(0.17f, 0.07f, 0.45f);

	private SimpleUniverse su;
	private BranchGroup sceneBG;

	private BoundingSphere bounds;

	private Landscape land;

	public Wrapper(double flatness) {
		setLayout(new BorderLayout());
		setOpaque(false);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		Canvas3D canvas3D = new Canvas3D(config);
		add("Center", canvas3D);
		canvas3D.setFocusable(true);
		canvas3D.requestFocus();
		su = new SimpleUniverse(canvas3D);
		System.setProperty("j3d.audiodevice",
				"com.sun.j3d.audioengines.javasound.JavaSoundMixer");
		AudioDevice audiDevice = su.getViewer().createAudioDevice();
		BackgroundSound bgSound = initSound("ChillingMusic.wav");
		bgSound.setEnable(true);
		// play("ChillingMusic");
		createSceneGraph(flatness);
		createUserControls(sceneBG);

		su.addBranchGraph(sceneBG);
	} 

	void createSceneGraph(double flatness) {
		sceneBG = new BranchGroup();
		sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

		lightScene();
		addBackground();
//		addFog();

		land = new Landscape(flatness);
		sceneBG.addChild(land.getLandBG());
		addLinesFountain(3000);
		TransformGroup tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Loader loader = new ObjectFile();
		Scene scene = null;
		try {
			scene = loader.load("images/duck.obj");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IncorrectFormatException e) {
			e.printStackTrace();
		} catch (ParsingErrorException e) {
			e.printStackTrace();
		}
		tg.addChild(scene.getSceneGroup());
		Alpha alpha = new Alpha(-1, 3000);
		alpha.setMode(Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE);
		CircleAnimation pInterp = new CircleAnimation(alpha, tg, 1,
				1.5f);
		pInterp.setSchedulingBounds(new BoundingSphere());
		
		tg.addChild(pInterp);
		Transform3D t3d = new Transform3D();
		t3d.setTranslation(new Vector3d(0, 40, 0));
		tg.setTransform(t3d);
		sceneBG.addChild(tg);
		sceneBG.compile();
	}

	public void play(String name) {
		try {
			File file = new File("sounds/" + name + ".wav");
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(file));
			clip.start();
			// Thread.sleep(clip.getMicrosecondLength());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	// one directional light
	private void lightScene() {
		Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

		Vector3f lightDir = new Vector3f(1.0f, -1.0f, -0.8f); // upper left
		DirectionalLight light1 = new DirectionalLight(white, lightDir);
		light1.setInfluencingBounds(bounds);
		sceneBG.addChild(light1);
	}

	private void addBackground() {
		Background back = new Background();
		back.setApplicationBounds(bounds);
		back.setColor(skyColour); // darkish blue
		sceneBG.addChild(back);
	}

	private void addFog() {
		LinearFog fogLinear = new LinearFog(skyColour, 15.0f, 30.0f);
		fogLinear.setInfluencingBounds(bounds); // same as background
		sceneBG.addChild(fogLinear);
	}


	private void createUserControls(BranchGroup bg) {
		View view = su.getViewer().getView();
		view.setBackClipDistance(20);
		view.setFrontClipDistance(0.05);

		ViewingPlatform vp = su.getViewingPlatform();
		TransformGroup steerTG = vp.getViewPlatformTransform();

		KeyBehavior keybeh = new KeyBehavior(land, steerTG, bg);
		keybeh.setSchedulingBounds(bounds);
		vp.setViewPlatformBehavior(keybeh);
	}

	private void addLinesFountain(int numParts) {
		LineParticles linesFountain = new LineParticles(numParts, 20);
		sceneBG.addChild(linesFountain);
		Behavior partBeh = linesFountain.getParticleBeh();
		partBeh.setSchedulingBounds(bounds);
		sceneBG.addChild(partBeh);
	}

	private BackgroundSound initSound(String filename) {
		MediaContainer soundMC = null;
		try {
			soundMC = new MediaContainer("file:sounds/" + filename);
			soundMC.setCacheEnable(true);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		// create a point sound
		BackgroundSound ps = new BackgroundSound();
		ps.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		ps.setSoundData(soundMC);
		ps.setInitialGain(1.0f);
		ps.setCapability(PointSound.ALLOW_ENABLE_WRITE);
		ps.setCapability(PointSound.ALLOW_POSITION_WRITE);
		System.out.println("Backgroundsound created from sounds/" + filename);
		return ps;
	}

}
