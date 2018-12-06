package Main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class Utils {
	public static SimpleUniverse createUniverse(Container container) {
		// Canvas und ein zugehoeriges Universum erzeugen
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();

		Canvas3D canvas3D = new Canvas3D(config);

		container.setLayout(new BorderLayout());
		container.add("Center", canvas3D);

		SimpleUniverse universe = new SimpleUniverse(canvas3D);

		// Sichtpyramide des Universums initialisieren
		ViewingPlatform viewingPlatform = universe.getViewingPlatform();
		viewingPlatform.setNominalViewingTransform();

		// Interaktion fuer Maus aufsetzen
		OrbitBehavior orbit = new OrbitBehavior(canvas3D,
				OrbitBehavior.REVERSE_ALL);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);
		orbit.setSchedulingBounds(bounds);
		viewingPlatform.setViewPlatformBehavior(orbit);

		return universe;
	}

	public static TransformGroup createAxis() {

		final Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
		final Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
		final Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
		// Erzeuge die Wurzel der Szene
		TransformGroup root = new TransformGroup();
		// erzeuge x-Achse
		LineArray axisX = new LineArray(2, LineArray.COORDINATES
				| LineArray.COLOR_3);
		axisX.setCoordinate(0, new Point3f(-1.0f, 0.0f, 0.0f));
		axisX.setCoordinate(1, new Point3f(1.0f, 0.0f, 0.0f));
		axisX.setColor(0, green);
		axisX.setColor(1, green);
		root.addChild(new Shape3D(axisX));

		// set arrowX
		Transform3D arrowrotx = new Transform3D();
		arrowrotx.rotZ(Math.PI * 1.5f);
		TransformGroup arrowXTrans = new TransformGroup(arrowrotx);
		Transform3D arrowPosX = new Transform3D();
		Vector3f posXArrow = new Vector3f(0, 1f, 0);
		arrowPosX.setTranslation(posXArrow);
		TransformGroup arrowXTransPos = new TransformGroup(arrowPosX);
		arrowXTrans.addChild(arrowXTransPos);
		Cone arrowX = new Cone(0.01f, .05f);
		Appearance xapp = new Appearance();
		xapp.setColoringAttributes(new ColoringAttributes(new Color3f(0, 1, 0),
				1));
		arrowX.setAppearance(xapp);
		arrowXTransPos.addChild(arrowX);
		root.addChild(arrowXTrans);
		
		String fontName = "Helvetica";
		int fontSize = 1;  int fontStyle = Font.BOLD;
		Font font = new Font(fontName, fontStyle, fontSize);
		FontExtrusion extrusion = new FontExtrusion();
		Font3D font3D = new Font3D(font, extrusion);
		String textString = "X";
		Text3D text3D = new Text3D(font3D, textString);
		Shape3D xtext = new Shape3D(text3D, xapp);
		
		
		TransformGroup btgX = new TransformGroup();
		btgX.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		TransformGroup xTextTg = new TransformGroup();
		xTextTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		btgX.addChild(xTextTg);
		Transform3D xTextT3d = new Transform3D();
		xTextT3d.setTranslation(new Vector3f(1.1f,-0.04f,0));
		xTextT3d.setScale(0.1f);
		xTextTg.setTransform(xTextT3d);
		xTextTg.addChild(xtext);
		root.addChild(btgX);
		Billboard bbX = new Billboard(xTextTg);
		bbX.setSchedulingBounds(new BoundingSphere());
		btgX.addChild(bbX);

		// erzeuge y-Achse
		LineArray axisY = new LineArray(2, LineArray.COORDINATES
				| LineArray.COLOR_3);
		axisY.setCoordinate(0, new Point3f(0.0f, -1.0f, 0.0f));
		axisY.setCoordinate(1, new Point3f(0.0f, 1.0f, 0.0f));
		// Achsenfarbe: blau
		axisY.setColor(0, blue);
		axisY.setColor(1, blue);
		root.addChild(new Shape3D(axisY));

		// set arrowY
		Transform3D arrowrotY = new Transform3D();
		arrowrotY.rotY(Math.PI * 0.5f);
		TransformGroup arrowYTrans = new TransformGroup(arrowrotY);
		Transform3D arrowPosY = new Transform3D();
		Vector3f posYArrow = new Vector3f(0, 1f, 0);
		arrowPosY.setTranslation(posYArrow);
		TransformGroup arrowYTransPos = new TransformGroup(arrowPosY);
		arrowYTrans.addChild(arrowYTransPos);
		Cone arrowY = new Cone(0.01f, .05f);
		Appearance yapp = new Appearance();
		yapp.setColoringAttributes(new ColoringAttributes(new Color3f(0, 0, 1),
				1));
		arrowY.setAppearance(yapp);
		arrowYTransPos.addChild(arrowY);
		root.addChild(arrowYTrans);
		
		textString = "Y";
		text3D = new Text3D(font3D, textString);
		xtext = new Shape3D(text3D, yapp);
		xTextTg = new TransformGroup();
		xTextTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		xTextT3d = new Transform3D();
		xTextT3d.setTranslation(new Vector3f(-0.035f,1.1f,0));
		xTextT3d.setScale(0.1f);
		xTextTg.setTransform(xTextT3d);
		xTextTg.addChild(xtext);
		root.addChild(xTextTg);
		Billboard bbY = new Billboard(xTextTg);
		bbY.setSchedulingBounds(new BoundingSphere());
		root.addChild(bbY);

		// erzeuge z-Achse
		// Punkte stehen im Array
		Point3f[] zPoints = { new Point3f(0.0f, 0.0f, -1.0f),
				new Point3f(0.0f, 0.0f, 1.0f) };
		LineArray axisZ = new LineArray(2, LineArray.COORDINATES
				| LineArray.COLOR_3);
		axisZ.setCoordinates(0, zPoints);
		// z-Achse ist zweifarbig?
		axisZ.setColor(0, red);
		axisZ.setColor(1, red);
		root.addChild(new Shape3D(axisZ));

		// set arrowZ
		Transform3D arrowrotZ = new Transform3D();
		arrowrotZ.rotX(Math.PI * 0.5f);
		TransformGroup arrowZTrans = new TransformGroup(arrowrotZ);
		Transform3D arrowPosZ = new Transform3D();
		Vector3f posZArrow = new Vector3f(0, 1f, 0);
		arrowPosZ.setTranslation(posZArrow);
		TransformGroup arrowZTransPos = new TransformGroup(arrowPosZ);
		arrowZTrans.addChild(arrowZTransPos);
		Cone arrowZ = new Cone(0.01f, .05f);
		Appearance zapp = new Appearance();
		zapp.setColoringAttributes(new ColoringAttributes(new Color3f(1, 0, 0),
				1));
		arrowZ.setAppearance(zapp);
		arrowZTransPos.addChild(arrowZ);
		root.addChild(arrowZTrans);
		
		textString = "Z";
		text3D = new Text3D(font3D, textString);
		xtext = new Shape3D(text3D, zapp);
		xTextTg = new TransformGroup();
		xTextTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		xTextT3d = new Transform3D();
		xTextT3d.setTranslation(new Vector3f(-0.035f,-0.04f,1.1f));
		xTextT3d.setScale(0.1f);
		xTextTg.setTransform(xTextT3d);
		xTextTg.addChild(xtext);
		root.addChild(xTextTg);
		Billboard bbZ = new Billboard(xTextTg);
		bbZ.setSchedulingBounds(new BoundingSphere());
		root.addChild(bbZ);
		
		
		return root;
	}

	public static TransformGroup createAxis(int length) {

		final Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
		final Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
		final Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
		// Erzeuge die Wurzel der Szene
		TransformGroup root = new TransformGroup();
		// erzeuge x-Achse
		LineArray axisX = new LineArray(2, LineArray.COORDINATES
				| LineArray.COLOR_3);
		axisX.setCoordinate(0, new Point3f(((float) -(length * 0.5)), 0.0f,
				0.0f));
		axisX.setCoordinate(1,
				new Point3f(((float) (length * 0.5)), 0.0f, 0.0f));
		axisX.setColor(0, green);
		axisX.setColor(1, green);
		root.addChild(new Shape3D(axisX));

		// set arrowX
		Transform3D arrowrotx = new Transform3D();
		arrowrotx.rotZ(Math.PI * 1.5f);
		TransformGroup arrowXTrans = new TransformGroup(arrowrotx);
		Transform3D arrowPosX = new Transform3D();
		Vector3f posXArrow = new Vector3f(0, length*.5f, 0);
		arrowPosX.setTranslation(posXArrow);
		TransformGroup arrowXTransPos = new TransformGroup(arrowPosX);
		arrowXTrans.addChild(arrowXTransPos);
		Cone arrowX = new Cone(0.01f, .05f);
		Appearance xapp = new Appearance();
		xapp.setColoringAttributes(new ColoringAttributes(new Color3f(0, 1, 0),
				1));
		arrowX.setAppearance(xapp);
		arrowXTransPos.addChild(arrowX);
		root.addChild(arrowXTrans);

		// erzeuge y-Achse
		LineArray axisY = new LineArray(2, LineArray.COORDINATES
				| LineArray.COLOR_3);
		axisY.setCoordinate(0, new Point3f(0.0f, ((float) -(length * 0.5)),
				0.0f));
		axisY.setCoordinate(1,
				new Point3f(0.0f, ((float) (length * 0.5)), 0.0f));
		// Achsenfarbe: blau
		axisY.setColor(0, blue);
		axisY.setColor(1, blue);
		root.addChild(new Shape3D(axisY));
		
		String fontName = "Helvetica";
		int fontSize = 1;  int fontStyle = Font.BOLD;
		Font font = new Font(fontName, fontStyle, fontSize);
		FontExtrusion extrusion = new FontExtrusion();
		Font3D font3D = new Font3D(font, extrusion);
		String textString = "X";
		Text3D text3D = new Text3D(font3D, textString);
		Shape3D xtext = new Shape3D(text3D, xapp);
		TransformGroup xTextTg = new TransformGroup();
		Transform3D xTextT3d = new Transform3D();
		xTextT3d.setTranslation(new Vector3f(length*0.5f+0.1f,-0.04f,0));
		xTextT3d.setScale(0.1f);
		xTextTg.setTransform(xTextT3d);
		xTextTg.addChild(xtext);
		root.addChild(xTextTg);
		

		// set arrowY
		Transform3D arrowrotY = new Transform3D();
		arrowrotY.rotY(Math.PI * 0.5f);
		TransformGroup arrowYTrans = new TransformGroup(arrowrotY);
		Transform3D arrowPosY = new Transform3D();
		Vector3f posYArrow = new Vector3f(0, length*.5f, 0);
		arrowPosY.setTranslation(posYArrow);
		TransformGroup arrowYTransPos = new TransformGroup(arrowPosY);
		arrowYTrans.addChild(arrowYTransPos);
		Cone arrowY = new Cone(0.01f, .05f);
		Appearance yapp = new Appearance();
		yapp.setColoringAttributes(new ColoringAttributes(new Color3f(0, 0, 1),
				1));
		arrowY.setAppearance(yapp);
		arrowYTransPos.addChild(arrowY);
		root.addChild(arrowYTrans);
		
		textString = "Y";
		text3D = new Text3D(font3D, textString);
		xtext = new Shape3D(text3D, yapp);
		xTextTg = new TransformGroup();
		xTextT3d = new Transform3D();
		xTextT3d.setTranslation(new Vector3f(-0.035f,length*0.5f+0.1f,0));
		xTextT3d.setScale(0.1f);
		xTextTg.setTransform(xTextT3d);
		xTextTg.addChild(xtext);
		root.addChild(xTextTg);
		

		// erzeuge z-Achse
		// Punkte stehen im Array
		Point3f[] zPoints = {
				new Point3f(0.0f, 0.0f, ((float) -(length * 0.5))),
				new Point3f(0.0f, 0.0f, ((float) (length * 0.5))) };
		LineArray axisZ = new LineArray(2, LineArray.COORDINATES
				| LineArray.COLOR_3);
		axisZ.setCoordinates(0, zPoints);
		// z-Achse ist zweifarbig?
		axisZ.setColor(0, red);
		axisZ.setColor(1, red);
		root.addChild(new Shape3D(axisZ));

		// set arrowZ
		Transform3D arrowrotZ = new Transform3D();
		arrowrotZ.rotX(Math.PI * 0.5f);
		TransformGroup arrowZTrans = new TransformGroup(arrowrotZ);
		Transform3D arrowPosZ = new Transform3D();
		Vector3f posZArrow = new Vector3f(0, length*.5f, 0);
		arrowPosZ.setTranslation(posZArrow);
		TransformGroup arrowZTransPos = new TransformGroup(arrowPosZ);
		arrowZTrans.addChild(arrowZTransPos);
		Cone arrowZ = new Cone(0.01f, .05f);
		Appearance zapp = new Appearance();
		zapp.setColoringAttributes(new ColoringAttributes(new Color3f(1, 0, 0),
				1));
		arrowZ.setAppearance(zapp);
		arrowZTransPos.addChild(arrowZ);
		root.addChild(arrowZTrans);
		
		textString = "Z";
		text3D = new Text3D(font3D, textString);
		xtext = new Shape3D(text3D, zapp);
		xTextTg = new TransformGroup();
		xTextT3d = new Transform3D();
		xTextT3d.setTranslation(new Vector3f(-0.035f,-0.04f,length*0.5f+0.1f));
		xTextT3d.setScale(0.1f);
		xTextTg.setTransform(xTextT3d);
		xTextTg.addChild(xtext);
		root.addChild(xTextTg);

		return root;
	}
}
