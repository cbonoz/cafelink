package www.cafelink.com.cafelink.injection

import com.google.gson.Gson

import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.util.CafeService
import www.cafelink.com.cafelink.util.PrefManager

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import www.cafelink.com.cafelink.util.Datastore
import www.cafelink.com.cafelink.util.UserSessionManager


@Module
class CafeModule(private val mApplication: CafeApplication) {

    @Provides
    @Singleton
    internal fun providesApplication(): CafeApplication {
        return mApplication
    }

    @Provides
    @Singleton
    internal fun providesGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    internal fun providesUserSessionManager(prefManager: PrefManager, gson: Gson): UserSessionManager {
        return UserSessionManager(prefManager, gson)
    }

    @Provides
    @Singleton
    internal fun providesCafeService(prefManager: PrefManager): CafeService {
        return CafeService(prefManager)
    }

    @Provides
    @Singleton
    internal fun providesDatastore(): Datastore {
        return Datastore()
    }

    @Provides
    @Singleton
    internal fun providesPrefManager(app: CafeApplication, gson: Gson): PrefManager {
        return PrefManager(app, gson)
    }

}
