package com.example.administrator.nc_ability_test_jni;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import nc.NCUtils;

public class MainActivity extends AppCompatActivity {


    private EditText ed_dataSize;   //数据大小
    private EditText ed_gnr_K;      //分成的份数
    private EditText ed_showTime;   //显示执行时间
    private Button bt_test;       //解码测试

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());


        ed_dataSize = (EditText) findViewById(R.id.editText_data_size);
        ed_gnr_K = (EditText) findViewById(R.id.editText_gnr_K);
        ed_showTime = (EditText) findViewById(R.id.editText_showTime);

        bt_test = (Button) findViewById(R.id.button_test);
        bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str_data_size = ed_dataSize.getText().toString();
                String str_K = ed_gnr_K.getText().toString();

                if (str_data_size.equals("")) {
                    SendMessage(PROMPTINFOR, 0, 0, "please input the size of data!");
                    return;
                }
                if (str_K.equals("")) {
                    SendMessage(PROMPTINFOR, 0, 0, "please input the size of generations!");
                    return;
                }

                int data_size = Integer.parseInt(str_data_size);   //注意此处单位是KB
                int K = Integer.parseInt(str_K);
                if (data_size < 0) {
                    SendMessage(PROMPTINFOR, 0, 0, "Data Size is unreasonable.");
                    return;
                }
                if (K <= 0 || K > 64) {
                    SendMessage(PROMPTINFOR, 0, 0, "Generation Size should be 1-64.");
                    return;
                }
                //生成随机编码矩阵
                Random random0 = new Random();
               int random_matrix_size = K * K;
                byte[]random_matrix = new byte[random_matrix_size];
                for (int i = 0; i < random_matrix_size; ++i) {
                    random_matrix[i] = (byte) (random0.nextInt(256));

                }


                //生成虚拟数据
                int nSubDataSize = (data_size * 1024) / K + ((data_size * 1024) % K != 0 ? 1 : 0);
                int len = K * nSubDataSize;
                byte[] data = new byte[len];

                Random random1 = new Random();
                for (int i = 0; i < len; i++) {
                    data[i] = (byte) (random1.nextInt(256));
                }

                //初始化有限域
                NCUtils.InitGalois();
                // Starting time.
                long startMili = System.currentTimeMillis();


                byte[] encodeData = NCUtils.Multiply(random_matrix,K,K,data,K,nSubDataSize);

                // Ending time.
                long endMili = System.currentTimeMillis();

                //释放有限域
                NCUtils.UninitGalois();
                float valueC = 0;
                valueC = ((float) (endMili - startMili)) / 1000;
                SendMessage(SHOWTIME, 0, 0, valueC + "");
                // The decoding operation has been finished.
                SendMessage(PROMPTINFOR, 0, 0, "The operation is completed.");


            }
        });


    }

    /**
     * 处理消息
     */
    public static final int PROMPTINFOR = 1;
    public static final int SHOWTIME = 2;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROMPTINFOR:
                    Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case SHOWTIME:
                    ed_showTime.setText(msg.obj.toString());
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public void SendMessage(int what, int arg1, int arg2, Object obj) {
        if (handler != null) {
            Message.obtain(handler, what, arg1, arg2, obj).sendToTarget();
        }
    }


}
