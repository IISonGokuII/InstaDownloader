package com.instadownloader;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class InstaDownloaderApp_MembersInjector implements MembersInjector<InstaDownloaderApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public InstaDownloaderApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<InstaDownloaderApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new InstaDownloaderApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(InstaDownloaderApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.instadownloader.InstaDownloaderApp.workerFactory")
  public static void injectWorkerFactory(InstaDownloaderApp instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
