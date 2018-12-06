package Main;

import java.util.Enumeration;

import javax.media.j3d.Alpha;
import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Vector3d;

public class CircleAnimation extends Behavior {

	private final Alpha alpha;
	private final Alpha alpha2;
	private final TransformGroup tg;
	private final Transform3D t3d = new Transform3D();
	private final Vector3d v3d = new Vector3d();
	private final WakeupCondition wakeup;
	private double a;
	private double b;
	private double x = 0;
	private double z = 0;
	private double y = 10;

	public CircleAnimation(Alpha alpha, TransformGroup tg, float a, float b) {
		this.alpha = alpha;
		this.tg = tg;
		this.a = a;
		this.b = b;
		this.alpha2 = new Alpha();
		
		wakeup = new WakeupOnElapsedFrames(0);
	}

	public void initialize() {
		long t = System.currentTimeMillis();
		alpha.setStartTime(t);
		alpha.setLoopCount(-1);
		alpha.setIncreasingAlphaDuration(3000);
		alpha2.setStartTime(t + alpha.getIncreasingAlphaDuration());
		wakeupOn(wakeup);
	}

	public void processStimulus(Enumeration arg0) {
		x = Math.cos(alpha.value()*2*Math.PI)*2;
		z = Math.sin(alpha.value()*2*Math.PI)*2;
		v3d.x = x;
		v3d.z = z;
		v3d.y = y;
		t3d.setTranslation(v3d);
		tg.setTransform(t3d);
		wakeupOn(wakeup);
	}

}
