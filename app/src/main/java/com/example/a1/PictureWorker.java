package com.example.a1;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;


 class Bridge {
    static private final String LOG_TAG = "Bridge";
    static private final String BASE_URL = "https://api2.newsminer.net/svc/news/queryNewsList";
    static private final String ENCODING = "UTF-8";
    static private final String PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    static private final int TIME_OUT = 2500;

    static private File systemCacheDir = null;

    static void setSystemCacheDir(File path) {
        systemCacheDir = path;
    }



    static Uri generateImageUri(String newsId, Context context, int id) {
        File imageFile = new File(systemCacheDir, "1");
        return FileProvider.getUriForFile(context, PROVIDER_AUTHORITY, imageFile);
    }

    static ArrayList<Uri> generateAllImagesUri(String newsId, Context context, int count) {
        ArrayList<Uri> imageUris = new ArrayList<>();
        imageUris.add(generateImageUri(newsId, context, 1));
        return imageUris;
    }



    static private Bitmap getImageFromUrl(String urlAddress) throws Exception {
        InputStream input = new URL(urlAddress).openStream();
        return BitmapFactory.decodeStream(input);
    }

    static private void saveToInternalStorage(File dir, String newsImageId, Bitmap bitmap) throws FileNotFoundException {
        File imagePath = new File(dir, newsImageId);
        FileOutputStream fileOutputStream = null;
        fileOutputStream = new FileOutputStream(imagePath);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
    }

    static private Bitmap loadImageFromStorage(File dir, String newsImageId) throws FileNotFoundException {
        Bitmap bitmap = null;
        File imagePath = new File(dir, newsImageId);
        bitmap = BitmapFactory.decodeStream(new FileInputStream(imagePath));
        return bitmap;
    }

    static Bitmap loadResourceFromPath(String newsImageId, String webPath) throws Exception {
        Bitmap bitmap = null;
        try {
            bitmap = loadImageFromStorage(systemCacheDir, newsImageId);
        } catch (FileNotFoundException exception) {
            bitmap = getImageFromUrl(webPath);
            saveToInternalStorage(systemCacheDir, newsImageId, bitmap);
        }
        return bitmap;
    }
     static Bitmap loadResourceFromPath1(String newsImageId, String webPath) throws Exception {
         Bitmap bitmap = null;

         bitmap = getImageFromUrl(webPath);
         saveToInternalStorage(systemCacheDir, newsImageId, bitmap);

         return bitmap;
     }



}




class Share{
    private String title="123";
    private Activity activity;
    private String text,url;
    private Intent shareIntent;
    private boolean hasImage;
    static private final String WECHAT_EXTRA_TEXT = "Kdescription";
    static private final String SMS_EXTRA_TEXT = "sms_body";

    public void share(Context context,String shareContent,String title) {
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bridge.loadResourceFromPath1("1", url);
                }catch(Exception e){}
            }
        });t.start();
        try{t.join();}catch (Exception e){}
        Intent shareIntent;
        if (hasImage) {
            shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setType("image/*");

            ArrayList<Uri> imageUris = Bridge.generateAllImagesUri("1", context, 1);



            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        } else {
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
        }

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TITLE, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        shareIntent.putExtra(WECHAT_EXTRA_TEXT, shareContent);
        shareIntent.putExtra(SMS_EXTRA_TEXT, shareContent);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "分享"));
    }


    public Share(Activity activity,String text,String url,boolean hasImage){
        this.hasImage=hasImage;
        this.activity=activity;
        this.text=text;
        this.url=url;
    }
    public void work(){
        Log.d("hererere","qweqwe");

        shareIntent= new Intent();
        Thread tt=new Thread(new Runnable() {
            @Override
            public void run() {
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.addCategory("android.intent.category.DEFAULT");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.setType("image/*");
                if(url!=null) {
                    Log.d("hererere11111","qweqwe");
                    PictureWorker tmp = new PictureWorker();
                    Log.d("hererere77","qweqwe");

                    File shareFile = new File(tmp.saveImages(tmp.getbitmap(url), "1.jpg"));
                    Log.d("hererere66","qweqwe");
                    Uri fileUri = Uri.fromFile(shareFile);
                    fileUri=file2Content(fileUri);
                    Log.d(fileUri.toString(),"qweqwe");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    Log.d("hererere55","qweqwe");
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                        List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            activity.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    }

                }
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                shareIntent = Intent.createChooser(shareIntent, title);
                Log.d("hererere44","qweqwe");
            }
        });
        tt.start();
        try{
        tt.join();}catch(Exception e){}

        if (shareIntent.resolveActivity(activity.getPackageManager()) != null) {
            try {
                Log.d("hererere33","qweqwe");

                activity.startActivity(shareIntent);
                Log.d("hererere22","qweqwe");

            } catch (Exception e) {
            }
        }



    }

    private Uri file2Content(Uri uri) {
        if (uri.getScheme().equals("file")) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = activity.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(")
                        .append(MediaStore.Images.ImageColumns.DATA)
                        .append("=")
                        .append("'" + path + "'")
                        .append(")");
                Cursor cur = cr.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    //do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }
}
public class PictureWorker  {

    public  Bitmap getbitmap(String imageUri) {
        // 显示网络上的图片
        Bitmap bitmap = null;
        Log.d("104","qweqwe");
        try {
            URL myFileUrl = new URL(imageUri);
            Log.d(imageUri,"qweqwe");
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            //conn.setDoInput(true);
            Log.d("105","qweqwe");
            conn.setRequestMethod("GET");
            //使用输入流
            conn.setDoInput(true);
            //GET 方式，不需要使用输出流
            conn.setDoOutput(false);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            Log.d("105.2","qweqwe");
            conn.connect();
            Log.d("105.5","qweqwe");
            InputStream is = conn.getInputStream();
            Log.d("106","qweqwe");
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            Log.d("107","qweqwe");

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    public String saveImages(Bitmap bitmap,String url) {
        String strPath = Environment.getExternalStorageDirectory().getPath();
        String filePaths=null;
        String strFileName = url ;
        FileOutputStream fos = null;
        Log.d("100","qweqwe");
        try {
            File destDir = new File(strPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            filePaths = strPath + "/" + strFileName;
            File imageFile = new File(filePaths);
            Log.d("101","qweqwe");
            imageFile.getParentFile().mkdirs();
            imageFile.createNewFile();
            Log.d("101.5","qweqwe");
            fos = new FileOutputStream(imageFile);
            Log.d("102","qweqwe");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            Log.d("103","qweqwe");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePaths;
    }



}
