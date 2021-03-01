package cn.cnic.collide;

import java.util.Arrays;

/**
 * @Description:
 * @autor Shi Guoyun<sgy-421205643@163.com>
 * @date 2021.02.25 025 8:47:06
 */
public class CoordinateConversion {

    /**
     * 桌边角的转换矩阵编号如下图所示。
     * 以原点位置为基准，其它所有位置均转到原点位置。
     *
     * -------------------------
     * |  3                 4  |
     * |2                     5|
     * |                       |
     * |                       |
     * |1                     6|
     * |o 8                 7  |
     * -------------------------
     */
    public double[][] MAP_CORNER_1;
    public double[][] MAP_CORNER_2;
    public double[][] MAP_CORNER_3;
    public double[][] MAP_CORNER_4;
    public double[][] MAP_CORNER_5;
    public double[][] MAP_CORNER_6;
    public double[][] MAP_CORNER_7;
    public double[][] MAP_CORNER_8;

    /**
     * 桌角的转换矩阵编号如下图所示。
     * 以o点位置为基准，其它所有位置均转到o点位置。
     *
     * -------------------------
     * |        4   3          |
     * |                       |
     * |                       |
     * |                       |
     * |                       |
     * |        1 o 2          |
     * -------------------------
     */
    public double[][] MAP_MIDDLE_1;
    public double[][] MAP_MIDDLE_2;
    public double[][] MAP_MIDDLE_3;
    public double[][] MAP_MIDDLE_4;

    public double[][][] MAP;

    public CoordinateConversion(Table table) {
        MAP_CORNER_1 = new double[3][3];
        MAP_CORNER_1[0][0] = MAP_CORNER_1[1][1] = MAP_CORNER_1[2][2] = 1;
        MAP_CORNER_2 = new double[3][3];
        MAP_CORNER_2[2][2] = 1;
        MAP_CORNER_2[0][0] = 1;
        MAP_CORNER_2[1][1] = -1;
        MAP_CORNER_2[1][2] = table.width;
        MAP_CORNER_3 = new double[3][3];
        MAP_CORNER_3[2][2] = 1;
        MAP_CORNER_3[0][0] = 0;
        MAP_CORNER_3[0][1] = -1;
        MAP_CORNER_3[1][0] = 1;
        MAP_CORNER_3[1][1] = 0;
        MAP_CORNER_3[0][2] = table.width;
        MAP_CORNER_4 = new double[3][3];
        MAP_CORNER_4[2][2] = 1;
        MAP_CORNER_4[0][0] = 0;
        MAP_CORNER_4[0][1] = -1;
        MAP_CORNER_4[1][0] = -1;
        MAP_CORNER_4[1][1] = 0;
        MAP_CORNER_4[0][2] = table.width;
        MAP_CORNER_4[1][2] = table.length;
        MAP_CORNER_5 = new double[3][3];
        MAP_CORNER_5[2][2] = 1;
        MAP_CORNER_5[0][0] = -1;
        MAP_CORNER_5[0][1] = 0;
        MAP_CORNER_5[1][0] = 0;
        MAP_CORNER_5[1][1] = -1;
        MAP_CORNER_5[0][2] = table.length;
        MAP_CORNER_5[1][2] = table.width;
        MAP_CORNER_6 = new double[3][3];
        MAP_CORNER_6[2][2] = 1;
        MAP_CORNER_6[0][0] = -1;
        MAP_CORNER_6[0][1] = 0;
        MAP_CORNER_6[1][0] = 0;
        MAP_CORNER_6[1][1] = 1;
        MAP_CORNER_6[0][2] = table.length;
        MAP_CORNER_6[1][2] = 0;
        MAP_CORNER_7 = new double[3][3];
        MAP_CORNER_7[2][2] = 1;
        MAP_CORNER_7[0][0] = 0;
        MAP_CORNER_7[0][1] = 1;
        MAP_CORNER_7[1][0] = -1;
        MAP_CORNER_7[1][1] = 0;
        MAP_CORNER_7[0][2] = 0;
        MAP_CORNER_7[1][2] = table.length;
        MAP_CORNER_8 = new double[3][3];
        MAP_CORNER_8[2][2] = 1;
        MAP_CORNER_8[0][0] = 0;
        MAP_CORNER_8[0][1] = 1;
        MAP_CORNER_8[1][0] = 1;
        MAP_CORNER_8[1][1] = 0;
        MAP_CORNER_8[0][2] = 0;
        MAP_CORNER_8[1][2] = 0;

        MAP_MIDDLE_1 = new double[3][3];
        MAP_MIDDLE_1[0][0] = MAP_MIDDLE_1[1][1] = MAP_MIDDLE_1[2][2] = 1;
        MAP_MIDDLE_1[0][2] = -table.length / 2;
        MAP_MIDDLE_2 = new double[3][3];
        MAP_MIDDLE_2[2][2] = 1;
        MAP_MIDDLE_2[0][0] = -1;
        MAP_MIDDLE_2[0][1] = 0;
        MAP_MIDDLE_2[1][0] = 0;
        MAP_MIDDLE_2[1][1] = 1;
        MAP_MIDDLE_2[0][2] = table.length / 2;
        MAP_MIDDLE_2[1][2] = 0;
        MAP_MIDDLE_3 = new double[3][3];
        MAP_MIDDLE_3[2][2] = 1;
        MAP_MIDDLE_3[0][0] = -1;
        MAP_MIDDLE_3[0][1] = 0;
        MAP_MIDDLE_3[1][0] = 0;
        MAP_MIDDLE_3[1][1] = -1;
        MAP_MIDDLE_3[0][2] = table.length / 2;
        MAP_MIDDLE_3[1][2] = table.width;
        MAP_MIDDLE_4 = new double[3][3];
        MAP_MIDDLE_4[2][2] = 1;
        MAP_MIDDLE_4[0][0] = 1;
        MAP_MIDDLE_4[0][1] = 0;
        MAP_MIDDLE_4[1][0] = 0;
        MAP_MIDDLE_4[1][1] = -1;
        MAP_MIDDLE_4[0][2] = -table.length / 2;
        MAP_MIDDLE_4[1][2] = table.width;

        MAP = new double[12][][];
        MAP[0] = MAP_CORNER_1;
        MAP[1] = MAP_CORNER_2;
        MAP[2] = MAP_CORNER_3;
        MAP[3] = MAP_CORNER_4;
        MAP[4] = MAP_CORNER_5;
        MAP[5] = MAP_CORNER_6;
        MAP[6] = MAP_CORNER_7;
        MAP[7] = MAP_CORNER_8;
        MAP[8] = MAP_MIDDLE_1;
        MAP[9] = MAP_MIDDLE_2;
        MAP[10] = MAP_MIDDLE_3;
        MAP[11] = MAP_MIDDLE_4;
    }

    public double[] forwardPoint(double[] point, int loc) {
        double[] result = new double[3];
        double[][] map = MAP[loc];
        for (int i = 0; i < 3; i++) {
            double sum = 0;
            double[] mapr = map[i];
            for (int j = 0; j < 3; j++) {
                sum += mapr[j] * point[j];
            }
            result[i] = sum;
        }
        return result;
    }

    public double[] backforwForce(double[] force, int loc) {
        double[] result = new double[3];
        double[][] map = MAP[loc];
        double det = map[0][0] * map[1][1] - map[1][0] * map[0][1];
        double[][] mapf = new double[2][2];
        mapf[0][0] = map[1][1] / det;
        mapf[1][1] = map[0][0] / det;
        mapf[1][0] = -map[1][0] / det;
        mapf[0][1] = -map[0][1] / det;
        for (int i = 0; i < 2; i++) {
            double sum = 0;
            double[] mapr = mapf[i];
            for (int j = 0; j < 2; j++) {
                sum += mapr[j] * force[j];
            }
            result[i] = sum;
        }
        result[2] = det * force[2];
        return result;
    }

    public static double[] panAndRotate(double x, double y, double[] pan, double theta) {
        double[] result = new double[2];
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        double px = pan[0];
        double py = pan[1];
        result[0] = cos * x + sin * y - px * cos - py * sin;
        result[1] = -sin * x + cos * y + px * sin - py * cos;
        return result;
    }

    public static double[] symmetryXY(double x, double y) {
        return new double[]{y, x};
    }

    public static double[] pan(double x, double y, double[] pan) {
        double[] result = new double[2];
        double px = pan[0];
        double py = pan[1];
        result[0] = x - px;
        result[1] = y - py;
        return result;
    }

    public static double[] rotate(double x, double y, double theta) {
        double[] result = new double[2];
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        result[0] = cos * x + sin * y;
        result[1] = -sin * x + cos * y;
        return result;
    }

    public static int arcRotate(int start, int theta) {
        return start + theta;
    }

    public static int[] arcSymmetryY(int start, int arc) {
        return new int[]{180 - start, -arc};
    }

    public static int[] arcSymmetryX(int start, int arc) {
        return new int[]{-start, -arc};
    }

    public static int[] arcSymmetryXY(int start, int arc) {
        return new int[]{90 - start, -arc};
    }

    public static double[][] createPanMatrix(double xPan, double yPan) {
        double[][] pan = new double[3][3];
        pan[0][0] = pan[1][1] = pan[2][2] = 1;
        pan[0][2] = -xPan;
        pan[1][2] = -yPan;
        return pan;
    }

    public static double[][] createRotateMatrix(double theta) {
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        double[][] rotate = new double[3][3];
        rotate[2][2] = 1;
        rotate[0][0] = rotate[1][1] = cos;
        rotate[0][1] = sin;
        rotate[1][0] = -sin;
        return rotate;
    }

    public static double[][] createSymmetryXY() {
        double[][] symmetry = new double[3][3];
        symmetry[0][1] = symmetry[1][0] = symmetry[2][2] = 1;
        return symmetry;
    }

    /**
     * @return 翻转y轴
     */
    public static double[][] createFlipY() {
        double[][] flip = new double[3][3];
        flip[0][0] = flip[2][2] = 1;
        flip[1][1] = -1;
        return flip;
    }

    public static double[][] createFlipX() {
        double[][] flip = new double[3][3];
        flip[1][1] = flip[2][2] = 1;
        flip[0][0] = -1;
        return flip;
    }

    /**
     * @param b 后转换
     * @param a 先转换
     * @return b * a
     */
    public static double[][] converseCombine(double[][] b, double[][] a) {
        double[][] result = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    result[i][j] += b[i][k] * a[k][j];
                }
            }
        }
        return result;
    }

    public static void print(double[][] a) {
        for (int i = 0; i < 3; i++) {
            System.out.printf("%f\t%f\t%f\n", a[i][0], a[i][1], a[i][2]);
        }
    }

    public static void main(String[] args) {
        Table table = new Table(1);
        CoordinateConversion cc = new CoordinateConversion(table);
        double l = table.length;
        double w = table.width;
        double x = l + 0.1, y = w - 0.1;

        double[][] pan = CoordinateConversion.createPanMatrix(l, 0);
        double[][] flipy = CoordinateConversion.createFlipY();
//        double[][] comb = CoordinateConversion.converseCombine(flipy, pan);
        double[][] flipx = CoordinateConversion.createFlipX();
        double[][] comb = CoordinateConversion.converseCombine(flipx, pan);
        double[][] symm = CoordinateConversion.createSymmetryXY();
        comb = CoordinateConversion.converseCombine(symm, comb);
        CoordinateConversion.print(comb);

//        double[] r = cc.forwardPoint(new double[]{x, y, 1}, 4);
//        System.out.println(Arrays.toString(r));

//        double[] p = CoordinateConversion.pan(x, y, new double[]{l, w});
//        System.out.println("x = " + (-p[0]) + ",\ty = " + (-p[1]));
//        double[] r = CoordinateConversion.panAndRotate(2, 1, new double[]{1, 0}, Math.PI / 4);
//        System.out.println(Arrays.toString(r));
    }
}
