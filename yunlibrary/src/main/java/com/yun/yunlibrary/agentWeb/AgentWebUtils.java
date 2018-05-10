package  com.yun.yunlibrary.agentWeb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static com.yun.yunlibrary.agentWeb.AgentWebConfig.DOWNLOAD_FILE_PATH;


/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b><br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 * source CODE  https://github.com/Justson/AgentWeb
 */

public class AgentWebUtils {

    private static final String TAG = AgentWebUtils.class.getSimpleName();

    public static int px2dp(Context context, float pxValue) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dipValue) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    public static final void clearWebView(WebView m) {

        if (m == null)
            return;
        if (Looper.myLooper() != Looper.getMainLooper())
            return;
        m.loadUrl("about:blank");
        m.stopLoading();
        if (m.getHandler() != null)
            m.getHandler().removeCallbacksAndMessages(null);
        m.removeAllViews();
        ViewGroup mViewGroup = null;
        if ((mViewGroup = ((ViewGroup) m.getParent())) != null)
            mViewGroup.removeView(m);
        m.setWebChromeClient(null);
        m.setWebViewClient(null);
        m.setTag(null);
        m.clearHistory();
        m.destroy();
        m = null;


    }

    public static boolean checkWifi(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean checkNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }


    public static int checkNetworkType(Context context) {

        int netType = 0;
        //连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null)
            return netType;
        switch (networkInfo.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                return 1;

            case ConnectivityManager.TYPE_MOBILE:
                switch (networkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return 2;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        return 3;

                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return 4;

                    default:
                        return netType;
                }

            default:

                return netType;
        }

    }

    public static long getAvailableStorage() {
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            } else {
                return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            }
        } catch (RuntimeException ex) {
            return 0;
        }
    }


    public static Uri getUriFromFile(Context context, File file) {
        Uri uri = null;

//        LogUtils.i("Info", "::" + context.getApplicationInfo().targetSdkVersion + "   INT:" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriFromFileForN(context, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static Uri getUriFromFileForN(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".AgentWebFileProvider", file);
        return fileUri;
    }


    public static void setIntentDataAndType(Context context,
                                            Intent intent,
                                            String type,
                                            File file,
                                            boolean writeAble) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriFromFile(context, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }


    public static void setIntentData(Context context,
                                     Intent intent,
                                     File file,
                                     boolean writeAble) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setData(getUriFromFile(context, file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setData(Uri.fromFile(file));
        }
    }


    public static void grantPermissions(Context context, Intent intent, Uri uri, boolean writeAble) {

        int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        if (writeAble) {
            flag |= Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        }
        intent.addFlags(flag);
        List<ResolveInfo> resInfoList = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, flag);
        }
    }


    private static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
      /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

      /* 依扩展名的类型决定MimeType */
        if (end.equals("pdf")) {
            type = "application/pdf";//
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
                end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video/*";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
                end.equals("jpeg") || end.equals("bmp")) {
            type = "image/*";
        } else if (end.equals("apk")) {
        /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        }
//      else if(end.equals("pptx")||end.equals("ppt")){
//        type = "application/vnd.ms-powerpoint";
//      }else if(end.equals("docx")||end.equals("doc")){
//        type = "application/vnd.ms-word";
//      }else if(end.equals("xlsx")||end.equals("xls")){
//        type = "application/vnd.ms-excel";
//      }
        else {
//        /*如果无法直接打开，就跳出软件列表给用户选择 */
            type = "*/*";
        }
        return type;
    }


    private static WeakReference<Snackbar> snackbarWeakReference;

    public static void show(View parent,
                            CharSequence text,
                            int duration,
                            @ColorInt int textColor,
                            @ColorInt int bgColor,
                            CharSequence actionText,
                            @ColorInt int actionTextColor,
                            View.OnClickListener listener) {
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(textColor);
        spannableString.setSpan(colorSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        snackbarWeakReference = new WeakReference<>(Snackbar.make(parent, spannableString, duration));
        Snackbar snackbar = snackbarWeakReference.get();
        View view = snackbar.getView();
        view.setBackgroundColor(bgColor);
        if (actionText != null && actionText.length() > 0 && listener != null) {
            snackbar.setActionTextColor(actionTextColor);
            snackbar.setAction(actionText, listener);
        }
        snackbar.show();

    }

    public static void dismiss() {
        if (snackbarWeakReference != null && snackbarWeakReference.get() != null) {
            snackbarWeakReference.get().dismiss();
            snackbarWeakReference = null;
        }
    }

    public static boolean isOverriedMethod(Object currentObject, String methodName, String method, Class... clazzs) {
        LogUtils.i("Info", "currentObject:" + currentObject + "  methodName:" + methodName + "   method:" + method);
        boolean tag = false;
        if (currentObject == null)
            return tag;

        try {

            Class clazz = currentObject.getClass();
            Method mMethod = clazz.getMethod(methodName, clazzs);
            String gStr = mMethod.toGenericString();


            tag = !gStr.contains(method);
        } catch (Exception igonre) {
            igonre.printStackTrace();
        }

        LogUtils.i("Info", "isOverriedMethod:" + tag);
        return tag;
    }


    public static void clearWebViewAllCache(Context context, WebView webView) {

        try {

            AgentWebConfig.removeAllCookies(null);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setJavaScriptEnabled(false);
            context.deleteDatabase("webviewCache.db");
            context.deleteDatabase("webview.db");
            //clearCache(context,0);
            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();

        } catch (Exception ignore) {
            //ignore.printStackTrace();
        }
    }

    public static List<String> getDeniedPermissions(Activity activity, String[] permissions) {

        if (permissions == null || permissions.length == 0)
            return null;
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {

            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        return deniedPermissions;

    }

    public static void clearWebViewAllCache(Context context) {

        try {

            clearWebViewAllCache(context, new WebView(context.getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //// 来源stackflow
    static int clearCacheFolder(final File dir, final int numDays) {

        int deletedFiles = 0;
        if (dir != null) {
            Log.i("Info", "dir:" + dir.getAbsolutePath());
        }
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {

                    //first delete subdirectories recursively
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }

                    //then delete the files and subdirectories in this dir
                    //only empty directories can be deleted, so subdirs have been done first
                    if (child.lastModified() < new Date().getTime() - numDays * DateUtils.DAY_IN_MILLIS) {
                        Log.i("Info", "getName:" + child.getName());
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Info", String.format("Failed to clean the cache, error %s", e.getMessage()));
            }
        }
        return deletedFiles;
    }


    /*
     * Delete the files older than numDays days from the application cache
     * 0 means all files.
     *
     * // 来源stackflow
     */
    public static void clearCache(final Context context, final int numDays) {
        Log.i("Info", String.format("Starting cache prune, deleting files older than %d days", numDays));
        int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
        Log.i("Info", String.format("Cache pruning completed, %d files deleted", numDeletedFiles));
    }


    public static String[] uriToPath(Activity activity, Uri[] uris) {

        if (activity == null || uris == null || uris.length == 0) {
            return null;
        }
        try {

            String[] paths = new String[uris.length];
            int i = 0;
            for (Uri mUri : uris) {
                paths[i++] = Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 ? getFileAbsolutePath(activity, mUri) : getRealPathBelowVersion(activity, mUri);

            }
            return paths;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            if (LogUtils.isDebug())
                    throwable.printStackTrace();
        }
        return null;

    }

    private static String getRealPathBelowVersion(Context context, Uri uri) {
        String filePath = null;
        LogUtils.i(TAG, "method -> getRealPathBelowVersion " + uri + "   path:" + uri.getPath() + "    getAuthority:" + uri.getAuthority());
        String[] projection = {MediaStore.Images.Media.DATA};

        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        if (filePath == null) {
//            filePath="file:".concat(uri.getPath());
            filePath = uri.getPath();

        }
        return filePath;
    }

    //必须执行在子线程, 会阻塞 直到文件转换完成;
    public static Queue<FileParcel> convertFile(String[] paths) throws Exception {

        if (paths == null || paths.length == 0)
            return null;
        int tmp = Runtime.getRuntime().availableProcessors() + 1;
        int result = paths.length > tmp ? tmp : paths.length;
        Executor mExecutor = Executors.newFixedThreadPool(result);
        final Queue<FileParcel> mQueue = new LinkedBlockingQueue<>();
        CountDownLatch mCountDownLatch = new CountDownLatch(paths.length);

        int i = 1;
        for (String path : paths) {

            LogUtils.i(TAG, "path:" + path);
            if (TextUtils.isEmpty(path)) {
                mCountDownLatch.countDown();
                continue;
            }

            mExecutor.execute(new EncodeFileRunnable(path, mQueue, mCountDownLatch, i++));

        }
        mCountDownLatch.await();

        if (!((ThreadPoolExecutor) mExecutor).isShutdown())
            ((ThreadPoolExecutor) mExecutor).shutdownNow();

        LogUtils.i("Info", "isShutDown:" + (((ThreadPoolExecutor) mExecutor).isShutdown()));
        return mQueue;
    }

    public static File createFile() {
        File mFile = null;
        try {

            mFile = new File(DOWNLOAD_FILE_PATH, System.currentTimeMillis() + "");
            mFile.createNewFile();
        } catch (Throwable e) {

        }
        return mFile;
    }


    static class EncodeFileRunnable implements Runnable {

        private String filePath;
        private Queue<FileParcel> mQueue;
        private CountDownLatch mCountDownLatch;
        private int id;

        public EncodeFileRunnable(String filePath, Queue<FileParcel> queue, CountDownLatch countDownLatch, int id) {
            this.filePath = filePath;
            this.mQueue = queue;
            this.mCountDownLatch = countDownLatch;
            this.id = id;
        }


        @Override
        public void run() {
            InputStream is = null;
            ByteArrayOutputStream os = null;
            try {
                File mFile = new File(filePath);
                if (mFile.exists()) {

                    is = new FileInputStream(mFile);
                    if (is == null)
                        return;

                    os = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = is.read(b, 0, 1024)) != -1) {
                        os.write(b, 0, len);
                    }
                    mQueue.offer(new FileParcel(id, mFile.getAbsolutePath(), Base64.encodeToString(os.toByteArray(), Base64.DEFAULT)));
                    LogUtils.i(TAG, "enqueu");
                } else {
                    LogUtils.i(TAG, "File no exists");
                }

            } catch (Throwable e) {
                LogUtils.i(TAG, "throwwable");
                e.printStackTrace();
            } finally {
                CloseUtils.closeIO(is);
                CloseUtils.closeIO(os);
                mCountDownLatch.countDown();
            }


        }
    }


    @TargetApi(19)
    public static String getFileAbsolutePath(Activity context, Uri fileUri) {

        if (context == null || fileUri == null)
            return null;

        LogUtils.i("Info", "getAuthority:" + fileUri.getAuthority() + "  getHost:" + fileUri.getHost() + "   getPath:" + fileUri.getPath() + "  getScheme:" + fileUri.getScheme() + "  query:" + fileUri.getQuery());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if (fileUri.getAuthority().equalsIgnoreCase(context.getPackageName() + ".AgentWebFileProvider")) {

            String path = fileUri.getPath();
            int index = path.lastIndexOf("/");
            return Environment.getExternalStorageDirectory() + File.separator + AgentWebConfig.DOWNLOAD_PATH + File.separator + path.substring(index + 1, path.length());
        } else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static Intent getInstallApkIntentCompat(Context context, File file) {

        Intent mIntent = new Intent().setAction(Intent.ACTION_VIEW).setType("application/vnd.android.package-archive");
        setIntentData(context, mIntent, file, false);
        return mIntent;
    }

    public static Intent getCommonFileIntentCompat(Context context, File file) {
        Intent mIntent = new Intent().setAction(Intent.ACTION_VIEW);
        mIntent.setType(getMIMEType(file));
        setIntentData(context, mIntent, file, false);
        return mIntent;
    }

    public static Intent getIntentCaptureCompat(Context context, File file) {
        Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri mUri = getUriFromFile(context, file);
        mIntent.addCategory(Intent.CATEGORY_DEFAULT);
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        return mIntent;
    }


    public static boolean isJson(String target) {
        if (TextUtils.isEmpty(target))
            return false;

        boolean tag = false;
        try {
            if (target.startsWith("["))
                new JSONArray(target);
            else
                new JSONObject(target);

            tag = true;
        } catch (JSONException igonre) {
//            igonre.printStackTrace();
            tag = false;
        }

        return tag;

    }

    public static String FileParcetoJson(Collection<FileParcel> collection) {

        if (collection == null || collection.size() == 0)
            return null;


        Iterator<FileParcel> mFileParcels = collection.iterator();
        JSONArray mJSONArray = new JSONArray();
        try {
            while (mFileParcels.hasNext()) {
                JSONObject jo = new JSONObject();
                FileParcel mFileParcel = mFileParcels.next();

                jo.put("contentPath", mFileParcel.getContentPath());
                jo.put("fileBase64", mFileParcel.getFileBase64());
                jo.put("id", mFileParcel.getId());
                mJSONArray.put(jo);
            }

        } catch (Exception e) {

        }


//        Log.i("Info","json:"+mJSONArray);
        return mJSONArray + "";


    }

    public static boolean isMainProcess(Context context) {

        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        boolean tag = false;
        int id = android.os.Process.myPid();
        String processName = "";

        String packgeName = context.getPackageName();
        List<ActivityManager.RunningAppProcessInfo> mInfos = mActivityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo mRunningAppProcessInfo : mInfos) {

            if (mRunningAppProcessInfo.pid == id) {
                processName = mRunningAppProcessInfo.processName;
                break;
            }
        }

        if (packgeName.equals(processName))
            tag = true;

        return tag;

    }


    public static boolean isUIThread() {

        return Looper.myLooper() == Looper.getMainLooper();

    }

    public static boolean isEmptyCollection(Collection collection) {

        return collection == null || collection.isEmpty();
    }

    public static boolean isEmptyMap(Map map) {

        return map == null || map.isEmpty();
    }

    private static Toast mToast = null;

    public static void toastShowShort(Context context, String msg) {

        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {

            mToast.setText(msg);
        }
        mToast.show();

    }

    private static Handler mHandler = null;


    public static void runInUiThread(Runnable runnable) {
        if (mHandler == null)
            mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(runnable);
    }

}
