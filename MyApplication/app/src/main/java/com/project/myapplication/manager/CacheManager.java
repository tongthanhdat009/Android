package com.project.myapplication.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.database.DatabaseProvider;
import androidx.media3.database.ExoDatabaseProvider;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;

import java.io.File;
import java.io.IOException;

public class CacheManager {
    @SuppressLint("UnsafeOptInUsageError")
    private static SimpleCache simpleCache;
    private static final String CACHE_DIRECTORY_NAME = "media_cache";
    private static final long MAX_CACHE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final String TAG = "CacheManager";
    private static final Object lock = new Object(); // Thread safety lock

    @OptIn(markerClass = UnstableApi.class)
    public static SimpleCache getInstance(Context context) {
        if (simpleCache == null) {
            synchronized (lock) {
                if (simpleCache == null) {
                    try {
                        // Tạo thư mục cache
                        File cacheDir = new File(context.getCacheDir(), CACHE_DIRECTORY_NAME);
                        if (!cacheDir.exists()) {
                            boolean dirCreated = cacheDir.mkdirs();
                            if (!dirCreated) {
                                Log.e(TAG, "Failed to create cache directory, using main cache dir");
                                cacheDir = context.getCacheDir();
                            }
                        }

                        // Xóa cache cũ nếu có vấn đề
                        clearCacheIfNeeded(cacheDir);

                        // Tạo các thành phần cache
                        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE);
                        DatabaseProvider databaseProvider = new ExoDatabaseProvider(context);

                        // Tạo SimpleCache
                        simpleCache = new SimpleCache(cacheDir, evictor, databaseProvider);

                    } catch (Exception e) {
                        Log.e(TAG, "Error creating SimpleCache", e);
                        // Trong trường hợp lỗi, trả về null và xử lý trong code gọi
                        simpleCache = null;
                    }
                }
            }
        }
        return simpleCache;
    }

    // Xóa cache nếu cần thiết
    private static void clearCacheIfNeeded(File cacheDir) {
        if (cacheDir.exists()) {
            try {
                // Kiểm tra xem có file cache đang bị hỏng không
                File[] cacheFiles = cacheDir.listFiles();
                if (cacheFiles != null) {
                    for (File file : cacheFiles) {
                        if (file.length() == 0 || file.getName().contains("temp")) {
                            // Xóa file có kích thước 0 hoặc tên chứa "temp"
                            boolean deleted = file.delete();
                            if (!deleted) {
                                Log.w(TAG, "Failed to delete file: " + file.getAbsolutePath());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error clearing cache", e);
            }
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    public static synchronized void releaseCache() {
        synchronized (lock) {
            if (simpleCache != null) {
                try {
                    simpleCache.release();
                } catch (Exception e) {
                    Log.e(TAG, "Error releasing SimpleCache", e);
                } finally {
                    simpleCache = null;
                }
            }
        }
    }

    // Phương thức xóa toàn bộ cache
    @OptIn(markerClass = UnstableApi.class)
    public static void clearCache(Context context) {
        synchronized (lock) {
            // Giải phóng cache hiện tại
            if (simpleCache != null) {
                try {
                    simpleCache.release();
                    simpleCache = null;
                } catch (Exception e) {
                    Log.e(TAG, "Error releasing cache", e);
                }
            }

            // Xóa thư mục cache
            File cacheDir = new File(context.getCacheDir(), CACHE_DIRECTORY_NAME);
            if (cacheDir.exists()) {
                try {
                    deleteDirectory(cacheDir);
                } catch (IOException e) {
                    Log.e(TAG, "Error deleting cache directory", e);
                }
            }
        }
    }

    // Phương thức đệ quy để xóa thư mục và nội dung
    private static void deleteDirectory(File fileOrDirectory) throws IOException {
        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteDirectory(child);
                }
            }
        }

        boolean deleted = fileOrDirectory.delete();
        if (!deleted && fileOrDirectory.exists()) {
            throw new IOException("Failed to delete " + fileOrDirectory.getAbsolutePath());
        }
    }
}