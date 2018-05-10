package com.yun.yunlibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Base64;

import com.orhanobut.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangzhengji on 2015/4/10.
 */
public class BitmapUtils {
    /**
     * 设置图片路径，从文件流中读取图像数据并返回Bitmap对象
     *
     * @param filePath
     * @return
     */
    public static Bitmap pathToBitmap(String filePath) {
        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inDither = false;
        bfOptions.inPurgeable = true;
        bfOptions.inTempStorage = new byte[12 * 1024];
        File file = new File(filePath);
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFileDescriptor(fin.getFD(), null,
                    bfOptions);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    /**
     * 将字符串转换成Bitmap类型
     *
     * @param string
     * @return
     */
    public static Bitmap base64ToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.NO_WRAP);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将Bitmap转换成字符串
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return string;
    }

    /**
     * 设置图片路径，缩略图最大宽度，从文件中读取图像数据并返回Bitmap对象
     *
     * @param filePath
     * @param maxWeight
     * @return
     */
    public static Bitmap pathToBitmap(String filePath, int maxWeight) {
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

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    /**
     * 旋转和缩放图片到合适的模式，并转成Base64编码，以供上传服务器
     *
     * @param imgPath 图片路径
     * @return base64编码的字符串
     */
    public static String imgToBase64WithRoteteAndScale(String imgPath) {
        byte[] b = getByte(bitmapRoteteAndScale(imgPath));
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    /**
     * 将输入流转换为byte数组
     *
     * @param in
     * @return
     */
    public static byte[] getByte(Bitmap in) {
        if (in == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            in.compress(Bitmap.CompressFormat.JPEG, 95, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 对图片进行质量和尺寸压缩 然后转为base64 尽量保证不失真
     *
     * @param imgPath 图片的路径
     * @return 返回base64
     */
    public static String imgToBase64WithRolateAndSimpleScale(String imgPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int simpleSize = 1;
        if (outHeight > 1280 || outWidth > 720) {
            int heightScale = Math.round(outHeight / 1280);
            int widthScale = Math.round(outWidth / 720);
            simpleSize = heightScale < widthScale ? heightScale : widthScale;
        }
        simpleSize = simpleSize < 1 ? 1 : simpleSize;
        options.inJustDecodeBounds = false;
        options.inSampleSize = simpleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);

        int rotationAngle = readPictureDegree(imgPath);
        if (0 != rotationAngle) {
            bitmap = rotatingBitmap(rotationAngle, bitmap);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }

    /**
     * 旋转 缩放
     *
     * @param imgPath
     * @param size    最大尺寸
     * @return
     */
    public static Bitmap bitmapRoteteAndScale(String imgPath, int size) {
        // 先判断图片旋转角度属性
        int rotationAngle = readPictureDegree(imgPath);
        //获取对应的bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
        //如果图片旋转角度属性非0，对bitmap旋转相应角度
        if (0 != rotationAngle) {
            //进行旋转
            bitmap = rotatingBitmap(rotationAngle, bitmap);
        }

        //进行缩放
        Bitmap resizebitmap = reduceBitmap(bitmap, 720);

        return resizebitmap;
    }


    /**
     * 旋转和缩放图片
     *
     * @param imgPath 图片路径
     * @return 旋转和缩放后的位图
     */
    public static Bitmap bitmapRoteteAndScale(String imgPath) {
        // 先判断图片旋转角度属性
        int rotationAngle = readPictureDegree(imgPath);
        //获取对应的bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
        //如果图片旋转角度属性非0，对bitmap旋转相应角度
        if (0 != rotationAngle) {
            //进行旋转
            bitmap = rotatingBitmap(rotationAngle, bitmap);
        }

        //进行缩放
        Bitmap resizebitmap = reduceBitmap(bitmap, 720);

        return resizebitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotatingBitmap(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public static Bitmap reduceBitmap(Bitmap bitmap, int maxWeight) {
        Matrix matrix = new Matrix();
        float realWidth = bitmap.getWidth();
        float realHeight = bitmap.getHeight();
        float larger = (realWidth > realHeight) ? realWidth : realHeight;
        float scale = maxWeight / larger;
        if (scale >= 1) {
            scale = 1;
        }
        matrix.postScale(scale, scale);

        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 手持照片拍照时旋转图片
     *
     * @param angle
     * @param picturePath
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, String picturePath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
        opts.inSampleSize = 2;
        Bitmap cbitmap = BitmapFactory.decodeFile(picturePath, opts);
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(cbitmap, 0, 0,
                cbitmap.getWidth(), cbitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 手持照片加载时Base64旋转
     *
     * @param angle
     * @param string
     * @return Bitmap
     */
    public static Bitmap Base64RotaingImageView(int angle, String string) {
        //把图片旋转为正的方向
        BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
        opts.inSampleSize = 2;
        Bitmap cbitmap = BitmapUtils.base64ToBitmap(string);
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(cbitmap, 0, 0,
                cbitmap.getWidth(), cbitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 矩形图片转为圆形图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap makeRoundCorner(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    public static File getOssFile(String url) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int simpleSize = 1;
        if (outHeight > 1280 || outWidth > 720) {
            int heightScale = Math.round(outHeight / 1280);
            int widthScale = Math.round(outWidth / 720);
            simpleSize = heightScale < widthScale ? heightScale : widthScale;
        }
        simpleSize = simpleSize < 1 ? 1 : simpleSize;
        options.inJustDecodeBounds = false;
        options.inSampleSize = simpleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(url, options);

        int rotationAngle = readPictureDegree(url);
        if (0 != rotationAngle) {
            bitmap = rotatingBitmap(rotationAngle, bitmap);
        }

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
//        Bitmap.
//        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
        StringBuilder filePath = new StringBuilder();
        filePath.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        filePath.append(File.separator);
//        filePath.append("sailormanager/image");
//        filePath.append(File.separator);
        filePath.append(new File(url).getName());
        return saveBitmapFile(bitmap, filePath.toString());
    }


    /**
     * 把batmap 转file
     *
     * @param bitmap
     * @param filepath
     */
    public static File saveBitmapFile(Bitmap bitmap, String filepath) {
        File file = new File(filepath);//将要保存图片的路径
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            boolean isSuccess = parentFile.mkdirs();
            if (!isSuccess) {
                Logger.t("okhttp");
                Logger.d("文件夹创建失败");
            }
        } else {
            if (file.exists()) {
                file.delete();
            }
//            file.mkdir();
        }
        Logger.t("okhttp");
        Logger.d("保存图片的路径：%s", filepath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
