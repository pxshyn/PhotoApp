package thanhdnh.ueh.edu.article_app;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class Downloader {
    public static String cached_file_path = "";

    public interface DownloadCallback {
        void onDownloadSuccess(File downloadedFile);
        void onDownloadFailed(Exception e);
    }

    public static File downloadFile(String url, File cached) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) return null;
            String contentType = response.header("Content-Type", "");
            String extension = getExtensionFromMimeType(contentType);
            File file = File.createTempFile("downloaded_file", extension, cached);
            if (response.body() != null) {
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(response.body().source());
                sink.close();
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void downloadWithProgress(String url, File cacheDir, ProgressBar progressBar, DownloadCallback callback) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        if (progressBar != null) {
            mainHandler.post(() -> {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            });
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (callback != null) {
                        callback.onDownloadFailed(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    mainHandler.post(() -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        if (callback != null) {
                            callback.onDownloadFailed(new IOException("Response failed: " + response.code()));
                        }
                    });
                    return;
                }

                long totalBytes = response.body().contentLength();
                InputStream inputStream = response.body().byteStream();
                String contentType = response.header("Content-Type", "");
                String extension = getExtensionFromMimeType(contentType);

                File outputFile = File.createTempFile("download_progress_", extension, cacheDir);

                try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[2048];
                    long downloadedBytes = 0;
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        downloadedBytes += bytesRead;

                        if (totalBytes > 0) {
                            int progress = (int) ((downloadedBytes * 100) / totalBytes);
                            mainHandler.post(() -> {
                                if (progressBar != null) {
                                    progressBar.setProgress(progress);
                                }
                            });
                        }
                    }
                    outputStream.flush();

                    mainHandler.post(() -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        cached_file_path = outputFile.getAbsolutePath();
                        if (callback != null) {
                            callback.onDownloadSuccess(outputFile);
                        }
                    });
                } catch (Exception e) {
                    mainHandler.post(() -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        if (callback != null) {
                            callback.onDownloadFailed(e);
                        }
                    });
                }
            }
        });
    }

    private static String getExtensionFromMimeType(String mimeType) {
        Map<String, String> mimeMap = new HashMap<>();
        mimeMap.put("image/jpeg", ".jpg");
        mimeMap.put("image/png", ".png");
        mimeMap.put("application/json", ".json");
        return mimeMap.getOrDefault(mimeType, ".json");
    }
}
