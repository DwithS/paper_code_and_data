package test.andapplication;

/**
 * Created by HH on 2016-09-21.
 */
public class smallsensordata {

    public static Boolean gyroEnable = true;
    public static Boolean accEnable = true;
    public static Boolean magEnable = true;

    int[] mag;
    int[] gyro;
    int[] accl;

    public void init(){
        mag = new int[3];
        gyro = new int[3];
        accl = new int[3];
        System.out.println("Done");
    }

    public smallsensordata(int[] InputGyro,int[] inputAccl, int[] inputMag){
        init();

    }
    public smallsensordata(int g1,int g2,int g3,int a1,int a2,int a3,int m1,int m2,int m3){

        init();

        gyro[0] = g1;
        gyro[1] = g2;
        gyro[2] = g3;
        mag[0] = m1;
        mag[1] = m2;
        mag[2] = m3;
        accl[0] = a1;
        accl[1] = a2;
        accl[2] = a3;

    }


    public String getDataByLine(){
        String answer;
        answer = "";
        if(gyroEnable) {
            for (int i = 0; i < 3; i++) {
                answer = answer  + Integer.toString(gyro[i])+ "\t";
            }
        }
        if(accEnable) {
            for (int i = 0; i < 3; i++) {
                answer = answer + Integer.toString(accl[i]) + "\t";
            }
        }
        if(magEnable) {
            for (int i = 0; i < 3; i++) {
                answer = answer + Integer.toString(mag[i]) + "\t";
            }
        }
        answer = answer.substring(0,answer.length()-1);
        return answer;
    }
}
