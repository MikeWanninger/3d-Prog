package Main;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.Enumeration;

public class LineParticles extends Shape3D {
	private final static int LINEWIDTH = 3;

	private static final float GRAVITY = 9.8f;
	private static final float TIMESTEP = 0.05f;
	private static final float XZ_VELOCITY = 2.0f;
	private static final float Y_VELOCITY = 16.0f;

	private final static Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
	private final static Color3f yellow = new Color3f(1.0f, 1.0f, 0.6f);

	private LineArray lineParts; 
	private PartclesControl partBeh;

	private float[] cs, vels, accs, cols;
	private int numPoints;

	public LineParticles(int nps, int delay) {
		if (nps % 2 == 1)
			nps++;
		numPoints = nps;

		lineParts = new LineArray(numPoints, LineArray.COORDINATES
				| LineArray.COLOR_3 | LineArray.BY_REFERENCE);

		lineParts.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
		lineParts.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);

		LinesUpdater updater = new LinesUpdater();
		partBeh = new PartclesControl(delay, updater);

		createGeometry();
		createAppearance();
	}

	public Behavior getParticleBeh() {
		return partBeh;
	}

	private void createGeometry() {
		cs = new float[numPoints * 3];
		vels = new float[numPoints * 3];
		accs = new float[numPoints * 3];
		cols = new float[numPoints * 3];

		for (int i = 0; i < numPoints * 3; i = i + 6)
			initTwoParticles(i);

		lineParts.setCoordRefFloat(cs);
		lineParts.setColorRefFloat(cols);

		setGeometry(lineParts);
	}

	private void initTwoParticles(int i) {

		cs[i] = 0.0f;
		cs[i + 1] = 0.0f;
		cs[i + 2] = 0.0f;

		double xvel = Math.random() * XZ_VELOCITY;
		double zvel = Math.sqrt((XZ_VELOCITY * XZ_VELOCITY) - (xvel * xvel));
		vels[i] = (float) ((Math.random() < 0.5) ? -xvel : xvel);
		vels[i + 2] = (float) ((Math.random() < 0.5) ? -zvel : zvel);
		vels[i + 1] = (float) (Math.random() * Y_VELOCITY);

		accs[i] = 0.0f;
		accs[i + 1] = -GRAVITY;
		accs[i + 2] = 0.0f;

		// next particle starts the same, but is one update advanced
		cs[i + 3] = cs[i];
		cs[i + 4] = cs[i + 1];
		cs[i + 5] = cs[i + 2];
		vels[i + 3] = vels[i];
		vels[i + 4] = vels[i + 1];
		vels[i + 5] = vels[i + 2];
		accs[i + 3] = accs[i];
		accs[i + 4] = accs[i + 1];
		accs[i + 5] = accs[i + 2];
		updateParticle(i + 3);

		Color3f col = (Math.random() < 0.5) ? yellow : red;
		cols[i] = col.x;
		cols[i + 1] = col.y;
		cols[i + 2] = col.z;
		cols[i + 3] = col.x;
		cols[i + 4] = col.y;
		cols[i + 5] = col.z;

	}

	private void updateParticle(int i) {
		cs[i] += vels[i] * TIMESTEP + 0.5 * accs[i] * TIMESTEP * TIMESTEP;
		cs[i + 1] += vels[i + 1] * TIMESTEP + 0.5 * accs[i + 1] * TIMESTEP
				* TIMESTEP;
		cs[i + 2] += vels[i + 2] * TIMESTEP + 0.5 * accs[i + 2] * TIMESTEP
				* TIMESTEP;

		vels[i] += accs[i] * TIMESTEP;
		vels[i + 1] += accs[i + 1] * TIMESTEP;
		vels[i + 2] += accs[i + 2] * TIMESTEP;
	}

	private void createAppearance() {
		Appearance app = new Appearance();

		LineAttributes la = new LineAttributes();
		app.setLineAttributes(la);

		setAppearance(app);
	}


	public class LinesUpdater implements GeometryUpdater {
		public void updateData(Geometry geo) {
			for (int i = 0; i < numPoints * 3; i = i + 6) {
				if ((cs[i + 1] < 0.0f) && (cs[i + 4] < 0.0f))
					initTwoParticles(i);
				else {
					updateParticle(i);
					updateParticle(i + 3);
				}
			}
		}

	}


	public class PartclesControl extends Behavior {
		private WakeupCondition timedelay;
		private LinesUpdater updater;

		public PartclesControl(int delay, LinesUpdater updt) {
			timedelay = new WakeupOnElapsedTime(delay);
			updater = updt;
		}

		public void initialize() {
			wakeupOn(timedelay);
		}

		public void processStimulus(Enumeration criteria) { 
			lineParts.updateData(updater);
			wakeupOn(timedelay);
		}

	}
}
