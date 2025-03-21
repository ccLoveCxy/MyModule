package com.imes.base.network.okhttp;

import org.json.JSONException;

import java.io.File;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * 上传文件的请求
 * @param <T>
 */
public abstract class FileRequest<T> extends BaseRequest<T> {
    /**
     *
     * @return 上传时，文件对应的key
     * @throws JSONException
     */
    protected abstract String uploadFileKey();

    /**
     *
     * @return 上传的文件
     * @throws JSONException
     */
    protected abstract File body() throws JSONException;
//
//    /**
//     *
//     * @return 上传时，json数据对应的key
//     * @throws JSONException
//     */
//    protected abstract String uploadDataKey();

//    /**
//     *
//     * @return 上传时，json数据
//     * @throws JSONException
//     */
//    protected abstract String uploadData() throws JSONException;

    @Override
    protected Headers buildHeader(BaseClientOption mOption) throws Exception {
        return mOption.headers(body());
    }

    @Override
    protected abstract Method method();

    @Override
    protected String info() throws Exception {
        return "upload image:"+body().getName();
    }

    @Override
    protected RequestBody buildBody() throws Exception {
        String fileName = toAscii(body().getName());
//        String fileDisposition = "form-data; name=\"" + uploadFileKey() + "\"; filename=\"" + fileName + "\"";
//        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), body());
//
//        MultipartBody.Builder b = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addPart(Headers.of("Content-Disposition", fileDisposition), fileBody);
//        if (!TextUtils.isEmpty(uploadDataKey()) && !TextUtils.isEmpty(uploadData())) {
//            String StringDisposition = "form-data; name=\"" + uploadDataKey() + "\"";
//            RequestBody StringBody = RequestBody.create(null, uploadData());
//            b.addPart(Headers.of("Content-Disposition", StringDisposition), StringBody);
//        }
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(uploadFileKey(),fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"),body()))
                .build();
        return body;
    }


    /**
     * 替换文件名中的中文
     * @param s
     * @return
     */
    public static String toAscii(String s) {
        for (int i = 0, length = s.length(), c; i < length; i += Character.charCount(c)) {
            c = s.codePointAt(i);
            if (!((c <= '\u001f' && c != '\t') || c >= '\u007f')) {
                continue;
            }

            Buffer buffer = new Buffer();
            buffer.writeUtf8(s, 0, i);
            for (int j = i; j < length; j += Character.charCount(c)) {
                c = s.codePointAt(j);
                buffer.writeUtf8CodePoint(c > '\u001f' && c < '\u007f' ? c : '?');
            }
            return buffer.readUtf8();
        }
        return s;
    }
}
