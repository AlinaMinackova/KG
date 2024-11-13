package com.cgvsu.math;

// Это заготовка для собственной библиотеки для работы с линейной алгеброй
public class Vector3f {
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Vector3f other) {
        // todo: желательно, чтобы это была глобальная константа
        final float eps = 1e-7f;
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps;
    }

    public final void mul(Vector3f var1, Vector3f var2) {
        this.x = var1.y * var2.z - var1.z * var2.y;
        this.y = -(var1.x * var2.z - var1.z * var2.x);
        this.z = var1.x * var2.y - var1.y * var2.x;
    }

    public final void sub(Vector3f var1, Vector3f var2) {
        this.x = var2.x - var1.x;
        this.y = var2.y - var1.y;
        this.z = var2.z - var1.z;
    }

    public final void add(Vector3f var1) {
        this.x += var1.x;
        this.y += var1.y;
        this.z += var1.z;
    }

    public final void div(double n) {
        this.x /= n;
        this.y /= n;
        this.z /= n;
    }

    public float x, y, z;
}
