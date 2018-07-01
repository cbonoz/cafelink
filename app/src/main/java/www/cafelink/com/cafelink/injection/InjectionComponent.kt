package www.cafelink.com.cafelink.injection

import javax.inject.Singleton

import dagger.Component
import www.cafelink.com.cafelink.activities.MainActivity
import www.cafelink.com.cafelink.activities.SplashActivity
import www.cafelink.com.cafelink.fragments.CafeMessageFragment
import www.cafelink.com.cafelink.fragments.MessageFragment
import www.cafelink.com.cafelink.fragments.ProfileFragment
import www.cafelink.com.cafelink.injection.CafeModule

@Singleton
@Component(modules = arrayOf(CafeModule::class))
interface InjectionComponent {

    // Activities
//    fun inject(activity: LoginActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: SplashActivity)
    fun inject(cafeMessageFragment: CafeMessageFragment)
    fun inject(messageFragment: MessageFragment)
    fun inject(profileFragment: ProfileFragment) {

    }

    // Fragments
//    fun inject(favoritesFragment: FavoritesFragment)
//    fun inject(genomeFragment: CafeFragment)
//    fun inject(recipeFragment: RecipeFragment)
}
