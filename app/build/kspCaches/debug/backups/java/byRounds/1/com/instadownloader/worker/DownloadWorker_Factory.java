package com.instadownloader.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.instadownloader.service.DownloadManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class DownloadWorker_Factory {
  private final Provider<DownloadManager> downloadManagerProvider;

  public DownloadWorker_Factory(Provider<DownloadManager> downloadManagerProvider) {
    this.downloadManagerProvider = downloadManagerProvider;
  }

  public DownloadWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, downloadManagerProvider.get());
  }

  public static DownloadWorker_Factory create(Provider<DownloadManager> downloadManagerProvider) {
    return new DownloadWorker_Factory(downloadManagerProvider);
  }

  public static DownloadWorker newInstance(Context context, WorkerParameters params,
      DownloadManager downloadManager) {
    return new DownloadWorker(context, params, downloadManager);
  }
}
