package com.chengxiang.gpiodemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.StandaloneActionMode;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    //输入和输出GPIO引脚名称
    private static final String GPIO_IN_NAME = "BCM21";
    private static final String GPIO_OUT_NAME = "BCM6";

    //输入和输出Gpio
    private Gpio mGpioIn;
    private Gpio mGpioOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PeripheralManagerService manager = new PeripheralManagerService();
        try {
            //打开并设置输入Gpio，监听输入信号变化（开关按钮的开关）
            mGpioIn = manager.openGpio(GPIO_IN_NAME);
            mGpioIn.setDirection(Gpio.DIRECTION_IN);
            mGpioIn.setEdgeTriggerType(Gpio.EDGE_FALLING);
            mGpioIn.setActiveType(Gpio.ACTIVE_HIGH);
            mGpioIn.registerGpioCallback(mGpioCallback);

            //打开并设置输出Gpio
            mGpioOut = manager.openGpio(GPIO_OUT_NAME);
            mGpioOut.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GpioCallback mGpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                //当按开关按钮的时候，改变输出Gpio的信号，从而控制LED灯的亮和灭
                mGpioOut.setValue(!mGpioOut.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭Gpio
        if (mGpioIn != null) {
            try {
                mGpioIn.unregisterGpioCallback(mGpioCallback);
                mGpioIn.close();
                mGpioIn = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mGpioOut != null) {
            try {
                mGpioOut.close();
                mGpioOut = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
