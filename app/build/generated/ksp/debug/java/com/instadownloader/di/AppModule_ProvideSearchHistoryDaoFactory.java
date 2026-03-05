package com.instadownloader.di;

import com.instadownloader.data.local.AppDatabase;
import com.instadownloader.data.local.SearchHistoryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideSearchHistoryDaoFactory implements Factory<SearchHistoryDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideSearchHistoryDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SearchHistoryDao get() {
    return provideSearchHistoryDao(databaseProvider.get());
  }

  public static AppModule_ProvideSearchHistoryDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideSearchHistoryDaoFactory(databaseProvider);
  }

  public static SearchHistoryDao provideSearchHistoryDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSearchHistoryDao(database));
  }
}
