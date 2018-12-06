package Main;

import java.awt.AWTEvent;
import java.awt.event.*;
import java.util.Enumeration;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.vp.*;

public class KeyBehavior extends ViewPlatformBehavior {
	private static final double ROT_AMT = Math.PI / 36.0;
	private static final double MOVE_STEP = 0.2;

	private static final double USER_HEIGHT = 1.0;


	private static final Vector3d FWD = new Vector3d(0, 0, -MOVE_STEP);
	private static final Vector3d BACK = new Vector3d(0, 0, MOVE_STEP);
	private static final Vector3d LEFT = new Vector3d(-MOVE_STEP, 0, 0);
	private static final Vector3d RIGHT = new Vector3d(MOVE_STEP, 0, 0);
	private static final Vector3d UP = new Vector3d(0, MOVE_STEP, 0);
	private static final Vector3d DOWN = new Vector3d(0, -MOVE_STEP, 0);


	private int forwardKey = KeyEvent.VK_UP;
	private int backKey = KeyEvent.VK_DOWN;
	private int leftKey = KeyEvent.VK_LEFT;
	private int rightKey = KeyEvent.VK_RIGHT;
	private int shootKey = KeyEvent.VK_SPACE;

	private WakeupCondition keyPress;

	private Landscape land;
	private double currLandHeight;
	private int zOffset;

	private Transform3D t3d = new Transform3D();
	private Transform3D toMove = new Transform3D();
	private Transform3D toRot = new Transform3D();
	private Vector3d trans = new Vector3d();
	private BranchGroup scene;

	public KeyBehavior(Landscape ld, TransformGroup steerTG, BranchGroup bg) {
		land = ld;
		zOffset = 0;
		initViewPosition(steerTG);
		scene = bg;
		keyPress = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
	}

	private void initViewPosition(TransformGroup steerTG) {
		Vector3d startPosn = land.getOriginVec();

		currLandHeight = startPosn.y; // store current floor height
		startPosn.y += USER_HEIGHT; // add user height

		steerTG.getTransform(t3d);
		t3d.setTranslation(startPosn);
		steerTG.setTransform(t3d);
	}

	public void initialize() {
		wakeupOn(keyPress);
	}

	public void processStimulus(Enumeration criteria) {
		WakeupCriterion wakeup;
		AWTEvent[] event;

		while (criteria.hasMoreElements()) {
			wakeup = (WakeupCriterion) criteria.nextElement();
			if (wakeup instanceof WakeupOnAWTEvent) {
				event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
				for (int i = 0; i < event.length; i++) {
					if (event[i].getID() == KeyEvent.KEY_PRESSED)
						processKeyEvent((KeyEvent) event[i]);
				}
			}
		}
		wakeupOn(keyPress);
	}

	private void processKeyEvent(KeyEvent eventKey) {
		int keyCode = eventKey.getKeyCode();

		if (eventKey.isAltDown()) // key + <alt>
			altMove(keyCode);
		else
			standardMove(keyCode);
	}

	private void standardMove(int keycode) {
		if (keycode == forwardKey)
			moveBy(FWD);
		else if (keycode == backKey)
			moveBy(BACK);
		else if (keycode == leftKey)
			rotateY(ROT_AMT);
		else if (keycode == rightKey)
			rotateY(-ROT_AMT);
	}

	private void altMove(int keycode) {
		if (keycode == forwardKey) {
			doMove(UP);
			zOffset++;
		} else if (keycode == backKey) {
			if (zOffset > 0) {
				doMove(DOWN);
				zOffset--;
			}
		} else if (keycode == leftKey)
			moveBy(LEFT);
		else if (keycode == rightKey)
			moveBy(RIGHT);
	}


	private void moveBy(Vector3d theMove) {
		Vector3d nextLoc = tryMove(theMove);
		if (!land.inLandscape(nextLoc.x, nextLoc.z))
			return;

		double floorHeight = land.getLandHeight(nextLoc.x, nextLoc.z,
				currLandHeight);

		double heightChg = floorHeight - currLandHeight - (MOVE_STEP * zOffset);

		currLandHeight = floorHeight;
		zOffset = 0; 
		Vector3d actualMove = new Vector3d(theMove.x, heightChg, theMove.z);
		doMove(actualMove);
	}


	private Vector3d tryMove(Vector3d theMove) {
		targetTG.getTransform(t3d);
		toMove.setTranslation(theMove);
		t3d.mul(toMove);
		t3d.get(trans);
		return trans;
	}

	private void doMove(Vector3d theMove) {
		targetTG.getTransform(t3d);
		toMove.setTranslation(theMove);
		t3d.mul(toMove);
		targetTG.setTransform(t3d);
	}


	private void rotateY(double radians) {
		targetTG.getTransform(t3d);
		toRot.rotY(radians);
		t3d.mul(toRot);
		targetTG.setTransform(t3d);
	}
}
