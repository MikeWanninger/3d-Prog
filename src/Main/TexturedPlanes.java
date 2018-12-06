package Main;

import java.util.ArrayList;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;
import com.sun.j3d.utils.picking.PickTool;

public class TexturedPlanes extends Shape3D {

	public TexturedPlanes(ArrayList coords, String fnm) {
		System.out.println(fnm + "; numPoints: " + coords.size());
		createGeometry(coords);
		createAppearance(fnm);

		PickTool.setCapabilities(this, PickTool.INTERSECT_COORD);
	}

	private void createGeometry(ArrayList coords) {
		int numPoints = coords.size();
		QuadArray plane = new QuadArray(numPoints, GeometryArray.COORDINATES
				| GeometryArray.TEXTURE_COORDINATE_2 | GeometryArray.NORMALS);

		Point3d[] points = new Point3d[numPoints];
		coords.toArray(points);

		
		TexCoord2f[] tcoords = new TexCoord2f[numPoints];
		for (int i = 0; i < numPoints; i = i + 4) {
			tcoords[i] = new TexCoord2f(0.0f, 0.0f); // for 1 point
			tcoords[i + 1] = new TexCoord2f(1.0f, 0.0f);
			tcoords[i + 2] = new TexCoord2f(1.0f, 1.0f);
			tcoords[i + 3] = new TexCoord2f(0.0f, 1.0f);
		}

		// create geometryInfo
		GeometryInfo gi = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
		gi.setCoordinates(points);
		gi.setTextureCoordinateParams(1, 2);
		gi.setTextureCoordinates(0, tcoords);

		// normalen mit weichen kanten berechnen
		NormalGenerator ng = new NormalGenerator();
		ng.setCreaseAngle((float) Math.toRadians(150)); // default is 44
		ng.generateNormals(gi);

		Stripifier st = new Stripifier();
		st.stripify(gi);

		setGeometry(gi.getGeometryArray());
	}

	private void createAppearance(String fnm) {
		Appearance app = new Appearance();

		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		app.setTextureAttributes(ta);

		TextureLoader loader = new TextureLoader(fnm,
				TextureLoader.GENERATE_MIPMAP, null);

		Texture2D texture = (Texture2D) loader.getTexture();
		texture.setMinFilter(Texture2D.MULTI_LEVEL_LINEAR);

		app.setTexture(texture);

		Material mat = new Material();
		mat.setLightingEnable(true);
		app.setMaterial(mat);

		setAppearance(app);
	}
}
