package com.cgvsu.math.vector;

import static com.cgvsu.math.Global.EPS;

// Это заготовка для собственной библиотеки для работы с линейной алгеброй
public class Vector2f implements Vector<Vector2f> {
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x, y;

    public static Vector2f addition(final Vector2f v1, final Vector2f v2) {
        return new Vector2f(v1.x + v2.x, v1.y + v2.y);
    }

    @Override
    public void add(final Vector2f v) {
        x += v.x;
        y += v.y;
    }

    public static Vector2f subtraction(final Vector2f v1, final Vector2f v2) {
        return new Vector2f(v1.x - v2.x, v1.y - v2.y);
    }

    @Override
    public void sub(final Vector2f v) {
        x -= v.x;
        y -= v.y;
    }

    @Override
    public void sub(Vector3f var1, Vector3f var2) {

    }

    @Override
    public Vector2f multiply(float c) {
        return new Vector2f(c * x, c * y);
    }

    @Override
    public void mult(float c) {
        x *= c;
        y *= c;
    }

    public Vector2f divide(float c) {
        if (c < EPS) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        return new Vector2f(x / c, y / c);
    }

    @Override
    public void div(float c) {
        if (c < EPS) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        x /= c;
        y /= c;
    }

    @Override
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public Vector2f normal() {
        final float length = this.length();
        if (length < EPS) {
            throw new ArithmeticException("Normalization of a zero vector is not allowed.");
        }
        return this.divide(length);
    }
    //eigen посмотреть библиотеку для названий

    public static float dotProduct(final Vector2f v1, final Vector2f v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    @Override
    public boolean equals(final Vector2f other) {
        return Math.abs(x - other.x) < EPS
                && Math.abs(y - other.y) < EPS;
    }
}
