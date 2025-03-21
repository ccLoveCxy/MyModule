package com.imes.base.scanner.photo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class PhotoUtils {

    public static String getPathFromIntent(Activity activity, Intent data) throws Exception {
        String[] proj = {MediaStore.Images.Media.DATA};
        Uri uri = null;
        try {
            uri = getUri(activity, data);
        } catch (Exception e) {
            uri = data.getData();
        }
        Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
        String photoPath = null;
        if (cursor.moveToFirst()) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            photoPath = cursor.getString(column_index);
            if (TextUtils.isEmpty(photoPath)) {
                photoPath = getPath(activity.getApplicationContext(),
                        data.getData());
            }
        }
        cursor.close();
        return photoPath;
    }

    /***
     * 获取从相册选择的图片路径
     * @param context
     * @param uri
     * @return 图片路径
     */
    public static String getImagePath(Context context, final Uri uri) {
        if(uri == null) {
            return "";
        }
        String uriStr = uri.toString();
        String path = "";
        if(uriStr.length() > 10) {
            path = uriStr.substring(10, uriStr.length());
        }
        if (path.startsWith("com.sec.android.gallery3d")) {
            Log.e("TAG", "It's auto backup pic path:" + uri.toString());
            return "";
        }
        String imagePath = "";
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        } else {
            imagePath = getPath(context, uri);
        }
        return imagePath;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        String result = null;
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                result = cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     *
     * @param intent
     * @return
     */
    public static Uri getUri(Context context, Intent intent) throws Exception {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                }
                if (index == 0) {
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

}