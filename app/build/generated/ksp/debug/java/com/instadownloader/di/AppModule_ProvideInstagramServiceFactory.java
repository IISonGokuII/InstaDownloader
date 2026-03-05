package com.instadownloader.di;

import com.instadownloader.service.InstagramService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideInstagramServiceFactory implements Factory<InstagramService> {
  @Override
  public InstagramService get() {
    return provideInstagramService();
  }

  public static AppModule_ProvideInstagramServiceFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static InstagramService provideInstagramService() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideInstagramService());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideInstagramServiceFactory INSTANCE = new AppModule_ProvideInstagramServiceFactory();
  }
}
