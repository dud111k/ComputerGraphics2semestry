package math.engine.matrix;

import math.engine.vector.Vector;

public interface Matrix<T extends Matrix<T, V>, V extends Vector<V>> {

    T add(T other);
    T subtract(T other);
    T multiply(float scalar);
    T multiply(T other);
    V multiply(V vector);
    T transpose();

    float determinant();
    T inverse();
    V solveLinearSystem(V vector);

    int getRows();
    int getCols();
    float get(int row, int col);

    boolean equals(Object obj);
    String toString();
}