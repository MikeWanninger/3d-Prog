package Main;

import java.util.*;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.picking.*;

public class Landscape {
	private static final int WORLD_LEN = 64;

	private static final int NUM_TEXTURES = 5;
	private static final String textureFns[] = { "water-shallow.jpg",
			"sand.jpg", "grass.jpg", "dryEarth.jpg", "stone.jpg" };

	private final static double MIN_HEIGHT = -2.0;
	private final static double MAX_HEIGHT = 8.0;

	private final static Vector3d DOWN_VEC = new Vector3d(0.0, -1.0, 0.0);

	private BranchGroup landBG, floorBG;
	private Point3d vertices[];
	private double textureBoundaries[];

	private Vector3d originVec = new Vector3d();
	private boolean foundOrigin = false;
	private PickTool picker;

	public Landscape(double flatness) {
		landBG = new BranchGroup();
		floorBG = new BranchGroup();
		landBG.addChild(floorBG);

		setTexBoundaries();

		picker = new PickTool(floorBG);
		picker.setMode(PickTool.GEOMETRY_INTERSECT_INFO);

		Mesh fm = new Mesh(flatness);
		vertices = fm.getVertices();
		platifyFloor();
		addWalls();

	}

	private void setTexBoundaries() {
		textureBoundaries = new double[NUM_TEXTURES];
		double boundStep = (MAX_HEIGHT - MIN_HEIGHT) / NUM_TEXTURES;
		double boundary = MIN_HEIGHT + boundStep;
		for (int j = 0; j < NUM_TEXTURES; j++) {
			textureBoundaries[j] = boundary;
			boundary += boundStep;
		}
	}


	private void platifyFloor() {
		ArrayList[] coordsList = new ArrayList[NUM_TEXTURES];
		for (int i = 0; i < NUM_TEXTURES; i++)
			coordsList[i] = new ArrayList();

		int heightIndex;
		for (int j = 0; j < vertices.length; j = j + 4) {
			heightIndex = findHeightIndex(j); // höhe der fläche finden
			addCoords(coordsList[heightIndex], j);
			checkForOrigin(j);
		}

		for (int i = 0; i < NUM_TEXTURES; i++)
			if (coordsList[i].size() > 0) // if used
				floorBG.addChild(new TexturedPlanes(coordsList[i], "images/"
						+ textureFns[i]));
	}

	private int findHeightIndex(int vertIndex) {
		double ah = avgHeight(vertIndex);
		for (int i = 0; i < textureBoundaries.length; i++)
			if (ah < textureBoundaries[i])
				return i;
		return NUM_TEXTURES - 1; // default
	}

	private double avgHeight(int vi) {
		return (vertices[vi].y + vertices[vi + 1].y + vertices[vi + 2].y + vertices[vi + 3].y) / 4.0;
	}

	//einfügen der 4 punkte (quadrat)
	private void addCoords(ArrayList coords, int vi) {
		coords.add(vertices[vi]);
		coords.add(vertices[vi + 1]);
		coords.add(vertices[vi + 2]);
		coords.add(vertices[vi + 3]);
	}

	private void checkForOrigin(int vi) {
		if (!foundOrigin) {
			if ((vertices[vi].x == 0.0) && (vertices[vi].z == 0.0)) {
				originVec.y = vertices[vi].y;
				foundOrigin = true;
			}
		}
	}

	private void addWalls() {
		Color3f eveningBlue = new Color3f(0.17f, 0.07f, 0.45f); // wall colour

		// back, left
		Point3d p1 = new Point3d(-WORLD_LEN / 2.0f, MIN_HEIGHT,
				-WORLD_LEN / 2.0f);
		Point3d p2 = new Point3d(-WORLD_LEN / 2.0f, MAX_HEIGHT,
				-WORLD_LEN / 2.0f);

		// front, left
		Point3d p3 = new Point3d(-WORLD_LEN / 2.0f, MIN_HEIGHT,
				WORLD_LEN / 2.0f);
		Point3d p4 = new Point3d(-WORLD_LEN / 2.0f, MAX_HEIGHT,
				WORLD_LEN / 2.0f);

		// front, right
		Point3d p5 = new Point3d(WORLD_LEN / 2.0f, MIN_HEIGHT, WORLD_LEN / 2.0f);
		Point3d p6 = new Point3d(WORLD_LEN / 2.0f, MAX_HEIGHT, WORLD_LEN / 2.0f);

		// back, right
		Point3d p7 = new Point3d(WORLD_LEN / 2.0f, MIN_HEIGHT,
				-WORLD_LEN / 2.0f);
		Point3d p8 = new Point3d(WORLD_LEN / 2.0f, MAX_HEIGHT,
				-WORLD_LEN / 2.0f);

		// left wall
		landBG.addChild(new ColouredPlane(p3, p1, p2, p4,
				new Vector3f(-1, 0, 0), eveningBlue));
		// front wall
		landBG.addChild(new ColouredPlane(p5, p3, p4, p6,
				new Vector3f(0, 0, -1), eveningBlue));
		// right wall
		landBG.addChild(new ColouredPlane(p7, p5, p6, p8,
				new Vector3f(-1, 0, 0), eveningBlue));
		// back wall
		landBG.addChild(new ColouredPlane(p7, p8, p2, p1,
				new Vector3f(0, 0, 1), eveningBlue));
	}


	public BranchGroup getLandBG() {
		return landBG;
	}


	public boolean inLandscape(double xPosn, double zPosn) {
		int x = (int) Math.round(xPosn); // to deal with dp errors
		int z = (int) Math.round(zPosn);

		if ((x <= -WORLD_LEN / 2) || (x >= WORLD_LEN / 2)
				|| (z <= -WORLD_LEN / 2) || (z >= WORLD_LEN / 2))
			return false;
		return true;
	}

	public Vector3d getOriginVec() {
		return originVec;
	}


	public double getLandHeight(double x, double z, double currHeight) {
		Point3d pickStart = new Point3d(x, MAX_HEIGHT * 2, z);
		picker.setShapeRay(pickStart, DOWN_VEC);

		PickResult picked = picker.pickClosest();
		if (picked != null) {
			if (picked.numIntersections() != 0) { 
				PickIntersection pi = picked.getIntersection(0);
				Point3d nextPt;
				try {
					nextPt = pi.getPointCoordinates();
				} catch (Exception e) {
					return currHeight;
				}
				return nextPt.y;
			}
		}
		return currHeight;
	}

}
