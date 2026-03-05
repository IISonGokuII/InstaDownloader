package com.instadownloader.ui.viewmodel;

import com.instadownloader.data.preferences.UserPreferences;
import com.instadownloader.data.repository.InstagramRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<InstagramRepository> repositoryProvider;

  private final Provider<UserPreferences> prefsProvider;

  public AuthViewModel_Factory(Provider<InstagramRepository> repositoryProvider,
      Provider<UserPreferences> prefsProvider) {
    this.repositoryProvider = repositoryProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(repositoryProvider.get(), prefsProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<InstagramRepository> repositoryProvider,
      Provider<UserPreferences> prefsProvider) {
    return new AuthViewModel_Factory(repositoryProvider, prefsProvider);
  }

  public static AuthViewModel newInstance(InstagramRepository repository, UserPreferences prefs) {
    return new AuthViewModel(repository, prefs);
  }
}
