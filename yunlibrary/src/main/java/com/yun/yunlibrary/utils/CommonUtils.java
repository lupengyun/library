package com.yun.yunlibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wangzhengji on 2016/1/26.
 */
public class CommonUtils {


    /**
     * 获取安装包信息
     *
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo packageInfo = new PackageInfo();
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 判断Sdcard是否存在
     *
     * @return
     */
    public static boolean detectSdcardIsExist() {
        String extStorageState = Environment.getExternalStorageState();
        File file = Environment.getExternalStorageDirectory();
        return !(!Environment.MEDIA_MOUNTED.equals(extStorageState)
                || !file.exists() || !file.canWrite()
                || file.getFreeSpace() <= 0);
    }

    /**
     * 判断指定路径下的文件是否存在
     */
    public static boolean detectFileIsExist(String path) {
        if (null != path) {
            File file = new File(path);
            return file.exists();
        }
        return false;
    }

    /**
     * 获得当前时间
     *
     * @return
     */
    public static String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    /**
     * 格式化时间
     * @param time yyyy-MM-dd HH:mm
     * @return yyyy-MM-dd HH:mm
     */
    public static String formatLongTime(long time){
        if(time == 0){
            return getTime();
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA).format(time);
    }

    /**
     * 格式化时间
     * @param time yyyy-MM-dd HH:mm
     * @return yyyy-MM-dd HH:mm
     */
    public static String formatLongTime2(long time){
        if(time == 0){
            return getTime();
        }
        return new SimpleDateFormat("mm分ss秒",Locale.CHINA).format(time);
    }

    /**
     * 只获取年
     * @param timeStamp
     * @return
     */
    public static int getYearByTimeStamp(long timeStamp){
        String date = formatLongTime(timeStamp);
        String year = date.substring(0, 4);
        return Integer.parseInt(year);
    }

    /**
     * 只获取月
     * @param timeStamp
     * @return
     */
    public static int getMonthByTimeStamp(long timeStamp){
        String date = formatLongTime(timeStamp);
        String month = date.substring(5, 7);
        return Integer.parseInt(month);
    }

    /**
     * 只获取日
     * @param timeStamp
     * @return
     */
    public static int getDayByTimeStamp(long timeStamp){
        String date = formatLongTime(timeStamp);
        String day = date.substring(8, 10);
        return Integer.parseInt(day);
    }

    /**
     * InputStream to String
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }


    /**
     * 获取应用版本名
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String versionName = "";
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }




    /**
     * 设置图片路径，缩略图最大宽度，从文件中读取图像数据并返回Bitmap对象
     *
     * @param filePath
     * @param maxWeight
     * @return
     */
    public static Bitmap reduce(String filePath, int maxWeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        float realWidth = options.outWidth;
        float realHeight = options.outHeight;
        float larger = (realWidth > realHeight) ? realWidth : realHeight;
        int scale = (int) (larger / maxWeight);
        if (scale <= 0) {
            scale = 1;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;

        bitmap = BitmapFactory.decodeFile(filePath, options);
        return bitmap;
    }

    public static String getNumFromString(String str) {
        str = str.trim();
        String str2 = "";
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2 += str.charAt(i);
                }
            }
        }
        return str2;
    }


    /**
     * 格式化为金钱格式的 "#,###.00"
     * @param count
     * @return
     */
    public static String formatMoneyNum(double count){
        if(count == 0){
            return "0.00";
        }
        return formatMoneyNum("#,###.00",count);
    }

    /**
     * 格式化为金钱格式的 "#,###.00"
     * @param count
     * @return
     */
    public static String formatMoneyNum(int count){
        if(count == 0){
            return "0.00";
        }
        return formatMoneyNum("#,###.00",count);
    }

    /**
     * 格式化为金钱格式的 "#,###.00"
     * @param count
     * @return
     */
    public static String formatNum(int count){
        if(count == 0){
            return "0";
        }
        return formatMoneyNum("#,###",count);
    }

    /**
     * 格式化为金钱格式的 "￥##.00"
     * @param count
     * @return
     */
    public static String formatMoneyNumS(double count){
        if(count == 0){
            return "￥0.00";
        }
        return formatMoneyNum("￥##.00",count);
    }
    /**
     * 根据格式格式化金钱
     * @param format
     * @param count
     * @return
     */
    public static String formatMoneyNum(String format,double count){
        NumberFormat nf = new DecimalFormat(format);
        String result = nf.format(count);
        return result;
    }

    /**
     * 隐藏手机中间四位
     * @param phone
     * @return
     */
    public static String formatPhone(String phone){
        if(TextUtils.isEmpty(phone)){
            return "";
        }
        StringBuilder sb = new StringBuilder(phone);
        String result = sb.replace(3,7,"****").toString();
        return result;
    }


    /**
     * 判断是否又摄像头权限
     * @return
     */
    public static boolean isCameraUseable() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
            mCamera = null;
        }

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

        return canUse;

    }




    private static final String TAG = "AudioPermissionCheckUtils";

    // 音频获取源
    public static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int bufferSizeInBytes = 0;

    /**
     * 判断是是否有录音权限
     */
    public static boolean checkAudioPermission(final Context context) {
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord =  new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try{
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING
                && audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
            return false;
        }

        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
            //如果短时间内频繁检测，会造成audioRecord还未销毁完成，此时检测会返回RECORDSTATE_STOPPED状态，再去read，会读到0的size，所以此时默认权限通过
            return true;
        }

        byte[] bytes = new byte[1024];
        int readSize = audioRecord.read(bytes, 0, 1024);
        if (readSize == AudioRecord.ERROR_INVALID_OPERATION || readSize <= 0) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        return true;
    }



}
