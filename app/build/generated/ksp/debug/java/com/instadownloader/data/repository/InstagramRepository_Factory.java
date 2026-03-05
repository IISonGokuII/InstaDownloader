package com.instadownloader.data.repository;

import com.instadownloader.service.InstagramService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class InstagramRepository_Factory implements Factory<InstagramRepository> {
  private final Provider<InstagramService> apiProvider;

  public InstagramRepository_Factory(Provider<InstagramService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public InstagramRepository get() {
    return newInstance(apiProvider.get());
  }

  public static InstagramRepository_Factory create(Provider<InstagramService> apiProvider) {
    return new InstagramRepository_Factory(apiProvider);
  }

  public static InstagramRepository newInstance(InstagramService api) {
    return new InstagramRepository(api);
  }
}
