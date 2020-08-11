/*
Copyright 2018 Alex Redaelli <a.redaelli at gmail dot com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.amilcar.laura.childrentrack.utils.fallDetect;

import android.os.Process;

import java.util.Arrays;

public class FallDetectAlgorithm extends Thread {

    private int buffer_index = 0;
    private int algo_index = 0;
    private int F1_index = 0;
    private boolean flag_fall = false;
    private boolean flag_enable_algo = true;
    private final int PEAKREPTHRESH = 4;
    private final int TSLEEP = 10; //ms time to wait for every read
    private final int F0SIZE = 50;
    private final int F1SIZE = 80;
    private final int BUFSIZE = F0SIZE * 10;
    private double[] buffer_ax = new double[BUFSIZE];
    private double[] buffer_ay = new double[BUFSIZE];
    private double[] buffer_az = new double[BUFSIZE];
    private int count = 0;
    private int ui_reset_flag = 0;
    private boolean buffer_ready = false;

    public FallDetectAlgorithm() {

        Arrays.fill(buffer_ax, 0);
        Arrays.fill(buffer_ay, 0);
        Arrays.fill(buffer_az, 0);
    }

    public void run() {

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        while (true) {

            if ((buffer_index > (F0SIZE + F1SIZE)) && flag_enable_algo) {
                buffer_ready = true;
                if (detect_fall_phase_2(algo_index, algo_index + F0SIZE, 3.0)) {
                    if (detect_fall_phase_3(algo_index + F0SIZE, algo_index + F0SIZE + F1SIZE, 2.0)) {
                        flag_enable_algo = false;
                        flag_fall = true;
                        clear_data();
                        buffer_index = 0;
                        algo_index = 0;
                    }
                }
                algo_index += 1;
                if (algo_index >= (BUFSIZE - (F0SIZE + F1SIZE))) algo_index = 0;
            }

            if (flag_fall == true) {
                buffer_ready = false;
                if (ui_reset_flag > 50) { // reset ui
                    ui_reset_flag = 0;
                    flag_enable_algo = true;
                    flag_fall = false;
                }
                ui_reset_flag++;
            }

            try {
                Thread.sleep(TSLEEP, 0); // ms,ns
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean get_buffer_ready() {
        return buffer_ready;
    }

    public boolean set_data(float[] linear_acceleration) {

        if (flag_enable_algo) {
            buffer_ax[buffer_index] = linear_acceleration[0];
            buffer_ay[buffer_index] = linear_acceleration[1];
            buffer_az[buffer_index] = linear_acceleration[2];

            buffer_index++;

            if (buffer_index > BUFSIZE - 1) {
                buffer_index = 0;
            }
        }
        return flag_fall;
    }

    private void clear_data() {
        for (int i = 0; i < BUFSIZE; i++) {
            buffer_ax[i] = 0.0;
            buffer_ay[i] = 0.0;
            buffer_az[i] = 0.0;
        }
    }


    // peak detector
    private boolean detect_fall_phase_2(int start, int stop, double threshold) {
        double min = 10000.0;
        double max = 0.0;
        double x2, y2, z2, t;
        for (int i = start; i < stop; i += 1) {
            x2 = buffer_ax[i];
            y2 = buffer_ay[i];
            z2 = buffer_az[i];
            t = (Math.sqrt(Math.abs(x2) * Math.abs(x2) +
                    Math.abs(y2) * Math.abs(y2) + Math.abs(z2) * Math.abs(z2)));
            if (t > max) max = t;
            if (t < min) min = t;
        }
        //System.out.println("-> "+ min + "/" + max + " " + (max-min));
        if ((max - min) > threshold) {
            System.out.println("*********** FALL P2 **********");
            return true;
        }

        return false;
    }

    private boolean detect_fall_phase_3(int start, int stop, double threshold) {
        double min = 10000.0;
        double max = 0.0;
        double x2, y2, z2, t;
        for (int i = start; i < stop; i += 1) {
            x2 = buffer_ax[i];
            y2 = buffer_ay[i];
            z2 = buffer_az[i];
            t = (Math.sqrt(Math.abs(x2) * Math.abs(x2) +
                    Math.abs(y2) * Math.abs(y2) + Math.abs(z2) * Math.abs(z2)));
            if (t > max) max = t;
            if (t < min) min = t;
        }
        //System.out.println("-> "+ i +"/" + " " + min + "/" + max + " " + (max-min));
        if ((max - min) < threshold) {
            System.out.println("*********** FALL P3 **********");
            return true;
        }

        return false;
    }
}

