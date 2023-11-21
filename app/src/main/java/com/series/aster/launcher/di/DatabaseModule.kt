package com.series.aster.launcher.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.series.aster.launcher.data.AppDatabase
import com.series.aster.launcher.data.dao.AppInfoDAO
import com.series.aster.launcher.helper.FingerprintHelper
import com.series.aster.launcher.helper.PreferenceHelper
import com.series.aster.launcher.helper.AppHelper
import com.series.aster.launcher.accessibility.MyAccessibilityService
import com.series.aster.launcher.helper.BottomDialogHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object DatabaseModule {
    @Provides
    @ViewModelScoped
    fun provideLocalDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()

    @Provides
    @ViewModelScoped
    fun provideAppDao(appDatabase: AppDatabase): AppInfoDAO = appDatabase.appDao()

    @Provides
    @ViewModelScoped
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @ViewModelScoped
    fun providePreferenceHelper(@ApplicationContext context: Context): PreferenceHelper {
        return PreferenceHelper(context)
    }

    @Provides
    @ViewModelScoped
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @ViewModelScoped
    fun provideAppHelper(): AppHelper {
        return AppHelper()
    }

    @Provides
    @ViewModelScoped
    fun provideBottomDialogHelper(): BottomDialogHelper {
        return BottomDialogHelper()
    }
}