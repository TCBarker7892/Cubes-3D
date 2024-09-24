import org.ejml.simple.SimpleMatrix;

public class matrixMath {
    public matrixMath() {

    }

    public static SimpleMatrix crossProduct3x3(SimpleMatrix A, SimpleMatrix B) {
        SimpleMatrix tempMatrix = A.concatColumns(B);
        SimpleMatrix row1 = tempMatrix.extractVector(true, 0);
        SimpleMatrix row2 = tempMatrix.extractVector(true, 1);
        SimpleMatrix row3 = tempMatrix.extractVector(true, 2);

        double res1 = row2.concatRows(row3).determinant();
        double res2 = row1.concatRows(row3).determinant() * (-1);
        double res3 = row1.concatRows(row2).determinant();

        return new SimpleMatrix(new double[][] { { res1 }, { res2 }, { res3 } });
    }
}
