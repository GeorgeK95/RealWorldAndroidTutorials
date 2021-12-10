

package com.example.android.petsave.search.domain.usecases

import com.example.android.petsave.core.domain.model.animal.Animal
import com.example.android.petsave.core.domain.repositories.AnimalRepository
import com.example.android.petsave.search.domain.model.SearchParameters
import com.example.android.petsave.search.domain.model.SearchResults
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchAnimals @Inject constructor(
    private val animalRepository: AnimalRepository
) {

  operator fun invoke(
      querySubject: BehaviorSubject<String>,
      ageSubject: BehaviorSubject<String>,
      typeSubject: BehaviorSubject<String>
  ): Flowable<SearchResults> {
    val query = querySubject
        .debounce(500L, TimeUnit.MILLISECONDS)
        .filter { it.length >= 2 }
        .distinctUntilChanged()

    return Observable.combineLatest(query, ageSubject, typeSubject, combiningFunction)
        .toFlowable(BackpressureStrategy.BUFFER)
        .switchMap {
            animalRepository.searchCachedAnimalsBy(SearchParameters(it.first, it.second, it.third))
        }
  }

  private val combiningFunction: Function3<String, String, String, Triple<String, String, String>>
    get() = Function3 {query, age, type -> Triple(query, age, type) }
}
