package www.cafelink.com.cafelink.injection

import com.google.gson.Gson

import www.cafelink.com.cafelink.CafeApplication
import www.cafelink.com.cafelink.util.CafeService
import www.cafelink.com.cafelink.util.PrefManager

import javax.inject.Singleton

import dagger.Module
import dagger.Provides


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
    internal fun providesCafeService(prefManager: PrefManager): CafeService {
        return CafeService(prefManager)
    }

    @Provides
    @Singleton
    internal fun providesPrefManager(app: CafeApplication, gson: Gson): PrefManager {
        return PrefManager(app, gson)
    }

}
