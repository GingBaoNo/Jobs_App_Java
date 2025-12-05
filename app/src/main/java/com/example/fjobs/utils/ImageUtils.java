package com.example.fjobs.utils;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    
    public static File uriToFile(Context context, Uri uri) throws IOException {
        // Lấy tên file từ URI
        String fileName = getFileName(context, uri);
        if (fileName == null) {
            fileName = "temp_file_" + System.currentTimeMillis();
        }

        // Tạo file tạm thời
        File file = new File(context.getCacheDir(), fileName);
        
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            
            if (inputStream != null) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        }
        
        return file;
    }
    
    private static String getFileName(Context context, Uri uri) {
        if (uri == null) return null;
        
        String fileName = null;
        String scheme = uri.getScheme();
        
        if (scheme != null && scheme.equals("content")) {
            try {
                String[] projection = { MediaStore.Images.Media.DISPLAY_NAME };
                android.database.Cursor cursor = context.getContentResolver().query(
                    uri, projection, null, null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(columnIndex);
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }
        
        return fileName;
    }
}