import org.ejml.simple.SimpleMatrix;

public class Player {
    public boolean hasMoved;
    public SimpleMatrix position;
    public SimpleMatrix facing;
    public SimpleMatrix displayDx;
    public SimpleMatrix displayDy;
    public SimpleMatrix displayDxDy;

    public Player(SimpleMatrix position, SimpleMatrix facing, boolean hasMoved, SimpleMatrix displayDx,
            SimpleMatrix displayDy) {
        this.hasMoved = hasMoved;
        this.position = position;
        this.facing = facing;
        this.displayDx = displayDx;
        this.displayDy = displayDy;
        this.displayDxDy = displayDx.concatColumns(displayDy);
    }

    public void updateDisplay(double projWidth, double projHeight, int displayWidth, int displayHeight) {
        this.displayDx = matrixMath.crossProduct3x3(this.facing,
                new SimpleMatrix(new double[][] { { 0 }, { 1 }, { 0 } }));
        this.displayDy = matrixMath.crossProduct3x3(this.facing, this.displayDx);
        this.displayDx = this.displayDx.scale(projWidth / displayWidth);
        this.displayDy = this.displayDy.scale(projHeight / displayHeight);
        this.displayDxDy = this.displayDx.concatColumns(this.displayDy);
    }
}