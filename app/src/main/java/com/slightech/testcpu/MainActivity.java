package com.slightech.testcpu;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private TextView cpu;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                //cpu使用情况
                getCPU();
                //内存使用情况
                String usePercent = getUsePercentNum(MainActivity.this);
                cpu.append(usePercent + "\n");

                handler.sendEmptyMessageDelayed(1, 100);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    private TextView cpu1;
    private MySurface mySurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cpu = (TextView) findViewById(R.id.cpu);
        cpu1 = (TextView) findViewById(R.id.cpu1);

//        MySurface mySurface = new MySurface(this);
//        setContentView(mySurface);

        handler.sendEmptyMessageDelayed(1, 100);
        new Thread(new Runnable() {
            @Override
            public void run() {


            }
        }).start();
    }

    private String getCPU() throws IOException {
        cpu.setText("");
        Log.i("rokey", "----------------------------------");
        String result;
        Process p = Runtime.getRuntime().exec("top -n 1 -d 1");

        BufferedReader br = new BufferedReader(new InputStreamReader
                (p.getInputStream()));
        while ((result = br.readLine()) != null) {
            if (result.trim().length() < 1) {
                continue;
            } else {
                String[] CPUusr = result.split("%");
                cpu.append("USER:" + CPUusr[0] + "\n");
                String[] CPUusage = CPUusr[0].split("User");
                String[] SYSusage = CPUusr[1].split("System");
                cpu.append("CPU:" + CPUusage[1].trim() + " length:" + CPUusage[1].trim().length() + "\n");
                cpu.append("SYS:" + SYSusage[1].trim() + " length:" + SYSusage[1].trim().length() + "\n");
                cpu.append(result + "\n");
                Log.i("rokey", result);
                break;
            }
        }
        return result;
    }

    public String getUsePercentNum(Context context) {
        //内存信息文件(CPU信息文件：/pro/cpuinfo)
        String dir = "/proc/meminfo";
        BufferedReader bufferedReader = null;
        try {
            FileReader fileReader = new FileReader(dir);
            bufferedReader = new BufferedReader(fileReader, 2048);
            String memoryLine = bufferedReader.readLine();  //读取第一行字符
            //截取字符串长度 至"MemTotal:"第一次出现的时候
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            //parseInt解析字符串返回整数   replaceAll基于正则表达式替换所有非数字(\\D)符号
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            //获取当前可用内存
            long availableSize = getAvailableMemory(context) / 1024; //字节转换为m
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent + "%";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return " ！";

    }

    //获取当前可用内存，返回数据以字节为单位
    private long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }


    class MyRender implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.i("GPU", "GL_RENDERER:" + gl.glGetString(GL10.GL_RENDERER));
            Log.i("GPU", "GL_VENDOR:" + gl.glGetString(GL10.GL_VENDOR));
            Log.i("GPU", "GL_VERSION:" + gl.glGetString(GL10.GL_VERSION));
            Log.i("GPU", "GL_EXTENSIONS:" + gl.glGetString(GL10.GL_EXTENSIONS));
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }
    }

    class MySurface extends GLSurfaceView {

        public MySurface(Context context) {
            super(context);
            setEGLConfigChooser(8, 8, 8, 8, 0, 0);
            MyRender myRender = new MyRender();
            setRenderer(myRender);
        }
    }
}



