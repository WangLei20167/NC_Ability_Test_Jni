package nc;


import java.util.Random;

/**
 * 矩阵乘法耗时太大  使用的jni函数执行
 */
public class NCUtils {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //初始化有限域
    //app运行时就应该调用
    public static native void InitGalois();

    //app退出时调用
    //释放jni申请的空间
    public static native void UninitGalois();

    public static native byte[] Multiply(byte[] matrix1, int row1, int col1, byte[] matrix2, int row2, int col2);

    public static native byte[] InverseMatrix(byte[] arrayData, int nK);

    public static native int getRank(byte[] matrix, int nRow, int nCol);

}
