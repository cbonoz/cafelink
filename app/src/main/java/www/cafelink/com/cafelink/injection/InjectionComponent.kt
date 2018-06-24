package www.cafelink.com.cafelink.injection

import javax.inject.Singleton

import dagger.Component
import www.cafelink.com.cafelink.activities.MainActivity
import www.cafelink.com.cafelink.activities.SplashActivity
import www.cafelink.com.cafelink.injection.CafeModule

@Singleton
@Component(modules = arrayOf(CafeModule::class))
interface InjectionComponent {

    // Activities
//    fun inject(activity: LoginActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: SplashActivity)

    // Fragments
//    fun inject(favoritesFragment: FavoritesFragment)
//    fun inject(genomeFragment: CafeFragment)
//    fun inject(recipeFragment: RecipeFragment)
}
