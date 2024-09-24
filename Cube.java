import org.ejml.simple.SimpleMatrix;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.JComponent;

public class Cube {

    private final double[] shadingFactor = { 0.85, 1.0, 0.7 };
    final int[] topVertexIDXs = { 2, 3, 7, 6 };
    final int[] bottomVertexIDXs = { 0, 1, 5, 4 };
    final int[] northVertexIDXs = { 0, 2, 6, 4 };
    final int[] eastVertexIDXs = { 0, 1, 3, 2 };
    final int[] southVertexIDXs = { 1, 3, 7, 5 };
    final int[] westVertexIDXs = { 4, 5, 7, 6 };

    double size;
    Color colour;
    SimpleMatrix[] vertices = new SimpleMatrix[8];

    JComponent topFace;
    JComponent bottomFace;
    JComponent northFace;
    JComponent eastFace;
    JComponent southFace;
    JComponent westFace;

    public Cube(SimpleMatrix positionInput, double sizeInput, Color colourInput, Rectangle fullscreen) {
        this.size = sizeInput;
        this.colour = colourInput;
        this.vertices[0] = positionInput;

        // Calculate each vertex point on the cube
        this.vertices[1] = this.vertices[0].copy();
        this.vertices[1].set(0, this.vertices[1].get(0) + this.size);
        this.vertices[2] = this.vertices[0].copy();
        this.vertices[3] = this.vertices[1].copy();
        this.vertices[2].set(1, this.vertices[2].get(1) + this.size);
        this.vertices[3].set(1, this.vertices[3].get(1) + this.size);
        this.vertices[4] = this.vertices[0].copy();
        this.vertices[5] = this.vertices[1].copy();
        this.vertices[6] = this.vertices[2].copy();
        this.vertices[7] = this.vertices[3].copy();
        this.vertices[4].set(2, this.vertices[4].get(2) + this.size);
        this.vertices[5].set(2, this.vertices[5].get(2) + this.size);
        this.vertices[6].set(2, this.vertices[6].get(2) + this.size);
        this.vertices[7].set(2, this.vertices[7].get(2) + this.size);

        this.topFace = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(adjustBrightness(colourInput, shadingFactor[1]));
            }
        };
        this.bottomFace = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(adjustBrightness(colourInput, shadingFactor[1]));
            }
        };
        this.northFace = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(adjustBrightness(colourInput, shadingFactor[0]));
            }
        };
        this.southFace = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(adjustBrightness(colourInput, shadingFactor[0]));
            }
        };
        this.eastFace = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(adjustBrightness(colourInput, shadingFactor[2]));
            }
        };
        this.westFace = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(adjustBrightness(colourInput, shadingFactor[2]));
            }
        };
        this.topFace.setBounds(fullscreen);
        this.bottomFace.setBounds(fullscreen);
        this.northFace.setBounds(fullscreen);
        this.southFace.setBounds(fullscreen);
        this.eastFace.setBounds(fullscreen);
        this.westFace.setBounds(fullscreen);
    }

    public SimpleMatrix[] projectCube(SimpleMatrix position, SimpleMatrix facing, double dist, SimpleMatrix dxdy,
            SimpleMatrix centre) {

        SimpleMatrix[] faces = new SimpleMatrix[6];
        SimpleMatrix[] projectedVertices = new SimpleMatrix[8];
        for (int i = 0; i < 8; i++) {
            projectedVertices[i] = projectPoint(facing, position, vertices[i], dist, dxdy, centre);
        }

        SimpleMatrix projVerticesTop = projectedVertices[this.topVertexIDXs[0]];
        SimpleMatrix projVerticesBottom = projectedVertices[this.bottomVertexIDXs[0]];
        SimpleMatrix projVerticesNorth = projectedVertices[this.northVertexIDXs[0]];
        SimpleMatrix projVerticesSouth = projectedVertices[this.southVertexIDXs[0]];
        SimpleMatrix projVerticesEast = projectedVertices[this.eastVertexIDXs[0]];
        SimpleMatrix projVerticesWest = projectedVertices[this.westVertexIDXs[0]];
        for (int i = 1; i < 4; i++) {
            projVerticesTop = projVerticesTop.concatColumns(projectedVertices[this.topVertexIDXs[i]]);
            projVerticesBottom = projVerticesBottom.concatColumns(projectedVertices[this.bottomVertexIDXs[i]]);
            projVerticesNorth = projVerticesNorth.concatColumns(projectedVertices[this.northVertexIDXs[i]]);
            projVerticesSouth = projVerticesSouth.concatColumns(projectedVertices[this.southVertexIDXs[i]]);
            projVerticesEast = projVerticesEast.concatColumns(projectedVertices[this.eastVertexIDXs[i]]);
            projVerticesWest = projVerticesWest.concatColumns(projectedVertices[this.westVertexIDXs[i]]);
        }
        faces[0] = projVerticesTop;
        faces[1] = projVerticesBottom;
        faces[2] = projVerticesNorth;
        faces[3] = projVerticesSouth;
        faces[4] = projVerticesEast;
        faces[5] = projVerticesWest;
        return faces;
    }

    public static SimpleMatrix projectPoint(SimpleMatrix facing, SimpleMatrix position, SimpleMatrix point,
            double dist, SimpleMatrix dxdy, SimpleMatrix center) {

        SimpleMatrix relPoint = point.minus(position);
        SimpleMatrix ScreenCentre3D = facing.scale(dist);

        double lambda = (ScreenCentre3D.dot(facing)) / (relPoint.dot(facing));
        SimpleMatrix point3DPlane = relPoint.scale(lambda);
        SimpleMatrix point2DPlane = dxdy.solve(point3DPlane.minus(ScreenCentre3D));

        SimpleMatrix Coordinates = point2DPlane;
        Coordinates = Coordinates.plus(center);

        return Coordinates;
    }

    public double getDistance(SimpleMatrix position) {
        double xPos = this.vertices[0].get(0) + (this.size / 2);
        double yPos = this.vertices[0].get(1) + (this.size / 2);
        double zPos = this.vertices[0].get(2) + (this.size / 2);

        double xTemp = Math.pow(xPos - position.get(0), 2);
        double yTemp = Math.pow(yPos - position.get(1), 2);
        double zTemp = Math.pow(zPos - position.get(2), 2);

        return Math.sqrt(xTemp + yTemp + zTemp);
    }

    public void updateRendering(SimpleMatrix facing, SimpleMatrix position, double dist, SimpleMatrix dxdy,
            SimpleMatrix center, Rectangle fullscreen) {

        // Work in progress function
        // SimpleMatrix[] projectedFaces = projectCube(position, facing, dist, dxdy,
        // center);

        if (position.get(0) < this.vertices[0].get(0)) {
            // Render NORTH face
            this.northFace.setOpaque(true);
            this.southFace.setOpaque(false);
            // Update this.northFace contents
        } else if (position.get(0) > this.vertices[0].get(0) + this.size) {
            // Render SOUTH face
            this.northFace.setOpaque(false);
            this.southFace.setOpaque(true);
            // Update this.southFace contents
        } else {
            this.northFace.setOpaque(false);
            this.southFace.setOpaque(false);
        }

        if (position.get(1) < this.vertices[0].get(1)) {
            // Render BOTTOM face
            this.topFace.setOpaque(false);
            this.bottomFace.setOpaque(true);
            // Update this.bottomFace contents
        } else if (position.get(1) > this.vertices[0].get(1) + this.size) {
            // Render TOP face
            this.topFace.setOpaque(true);
            this.bottomFace.setOpaque(false);
            // Update this.topFace contents
        } else {
            this.topFace.setOpaque(false);
            this.bottomFace.setOpaque(false);
        }

        if (position.get(2) < this.vertices[0].get(2)) {
            // Render EAST face
            this.westFace.setOpaque(false);
            this.eastFace.setOpaque(true);
            // Update this.eastFace contents
        } else if (position.get(2) > this.vertices[0].get(2) + this.size) {
            // Render WEST face
            this.westFace.setOpaque(true);
            this.eastFace.setOpaque(false);
            // Update this.westFace contents
        } else {
            this.westFace.setOpaque(false);
            this.eastFace.setOpaque(false);
        }
    }

    public JComponent[] renderProcessing(SimpleMatrix facing, SimpleMatrix position, double dist,
            SimpleMatrix dxdy, SimpleMatrix center, Rectangle fullscreen) {

        ArrayList<JComponent> Faces = new ArrayList<JComponent>();

        SimpleMatrix[] projectedFaces = projectCube(position, facing, dist, dxdy, center);

        if (position.get(1) > this.vertices[0].get(1) + this.size) {
            // Render TOP face
            Faces.add(renderFace(projectedFaces[0], adjustBrightness(colour, this.shadingFactor[1]), fullscreen));
        } else if (position.get(1) < this.vertices[0].get(1)) {
            // Render BOTTOM face
            Faces.add(renderFace(projectedFaces[1], adjustBrightness(colour, this.shadingFactor[1]), fullscreen));
        }

        if (position.get(0) < this.vertices[0].get(0)) {
            // Render NORTH face
            Faces.add(renderFace(projectedFaces[2], adjustBrightness(colour, this.shadingFactor[0]), fullscreen));
        } else if (position.get(0) > this.vertices[0].get(0) + this.size) {
            // Render SOUTH face
            Faces.add(renderFace(projectedFaces[3], adjustBrightness(colour, this.shadingFactor[0]), fullscreen));
        }

        if (position.get(2) < this.vertices[0].get(2)) {
            // Render EAST face
            Faces.add(renderFace(projectedFaces[4], adjustBrightness(colour, this.shadingFactor[2]), fullscreen));
        } else if (position.get(2) > this.vertices[0].get(2) + this.size) {
            // Render WEST face
            Faces.add(renderFace(projectedFaces[5], adjustBrightness(colour, this.shadingFactor[2]), fullscreen));
        }
        JComponent[] FacesArray = Faces.toArray(new JComponent[Faces.size()]);
        return FacesArray;
    }

    private static Color adjustBrightness(Color colour, double factor) {

        int red = (int) Math.round(colour.getRed() * factor);
        int green = (int) Math.round(colour.getGreen() * factor);
        int blue = (int) Math.round(colour.getBlue() * factor);

        return new Color(red, green, blue);
    }

    private static JComponent renderFace(SimpleMatrix faceVertices, Color colour, Rectangle fullscreen) {

        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        for (int i = 0; i < 4; i++) {
            xPoints[i] = (int) faceVertices.get(0, i);
            yPoints[i] = (int) faceVertices.get(1, i);
        }
        Polygon shape = new Polygon(xPoints, yPoints, 4);
        JComponent component = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(colour);
                g.fillPolygon(shape);
            }
        };
        component.setOpaque(false);
        component.setBounds(fullscreen);
        return component;
    }
}
