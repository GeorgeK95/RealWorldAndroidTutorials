

package com.example.android.petsave.common.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.example.android.petsave.common.data.api.PetFinderApi
import com.example.android.petsave.common.data.api.model.mappers.ApiAnimalMapper
import com.example.android.petsave.common.data.api.model.mappers.ApiPaginationMapper
import com.example.android.petsave.common.data.api.utils.FakeServer
import com.example.android.petsave.common.data.cache.Cache
import com.example.android.petsave.common.data.cache.PetSaveDatabase
import com.example.android.petsave.common.data.cache.RoomCache
import com.example.android.petsave.common.data.di.CacheModule
import com.example.android.petsave.common.data.di.PreferencesModule
import com.example.android.petsave.common.data.di.TestPreferencesModule
import com.example.android.petsave.common.data.preferences.FakePreferences
import com.example.android.petsave.common.data.preferences.Preferences
import com.example.android.petsave.common.di.ActivityRetainedModule
import com.example.android.petsave.common.domain.repositories.AnimalRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.Instant
import retrofit2.Retrofit
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(
    PreferencesModule::class,
    TestPreferencesModule::class,
    CacheModule::class,
    ActivityRetainedModule::class
)
class PetFinderAnimalRepositoryTest {

    private val fakeServer = FakeServer()
    private lateinit var repository: AnimalRepository
    private lateinit var api: PetFinderApi
    private lateinit var cache: Cache

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var database: PetSaveDatabase

    @Inject
    lateinit var retrofitBuilder: Retrofit.Builder

    @Inject
    lateinit var apiAnimalMapper: ApiAnimalMapper

    @Inject
    lateinit var apiPaginationMapper: ApiPaginationMapper

    @BindValue
    @JvmField
    val preferences: Preferences = FakePreferences()

    @Before
    fun setup() {
        fakeServer.start()

        with(preferences) {
            deleteTokenInfo()
            putToken("validToken")
            putTokenExpirationTime(Instant.now().plusSeconds(3600).epochSecond)
            putTokenType("Bearer")
            putPostcode("09097")
            putMaxDistanceAllowedToGetAnimals(100)
        }

        hiltRule.inject()

        api = retrofitBuilder
            .baseUrl(fakeServer.baseEndpoint)
            .build()
            .create(PetFinderApi::class.java)

        cache = RoomCache(database.animalsDao(), database.organizationsDao())

        repository = PetFinderAnimalRepository(
            api,
            cache,
            preferences,
            apiAnimalMapper,
            apiPaginationMapper
        )
    }

    @After
    fun teardown() {
        fakeServer.shutdown()
    }

    @Test
    fun requestMoreAnimals_success() = runBlocking {
        // Given
        val expectedAnimalId = 124L
        fakeServer.setHappyPathDispatcher()

        // When
        val paginatedAnimals = repository.requestMoreAnimals(1, 100)

        // Then
        val animal = paginatedAnimals.animals.first()
        assertThat(animal.id).isEqualTo(expectedAnimalId)
    }

    @Test
    fun insertAnimals_success() {
        // Given
        val expectedAnimalId = 124L

        runBlocking {
            fakeServer.setHappyPathDispatcher()

            val paginatedAnimals = repository.requestMoreAnimals(1, 100)
            val animal = paginatedAnimals.animals.first()

            // When
            repository.storeAnimals(listOf(animal))
        }

        // Then
        val testObserver = repository.getAnimals().test()

        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
        testObserver.assertValue { it.first().id == expectedAnimalId }
    }
}