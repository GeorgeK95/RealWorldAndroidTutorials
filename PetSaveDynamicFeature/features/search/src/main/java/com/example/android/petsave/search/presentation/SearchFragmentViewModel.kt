package com.example.android.petsave.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.logging.Logger
import com.example.android.petsave.common.domain.model.NoMoreAnimalsException
import com.example.android.petsave.common.domain.model.animal.Animal
import com.example.android.petsave.common.domain.model.pagination.Pagination
import com.example.android.petsave.common.domain.model.search.SearchParameters
import com.example.android.petsave.common.domain.model.search.SearchResults
import com.example.android.petsave.common.presentation.model.mappers.UiAnimalMapper
import com.example.android.petsave.common.utils.DispatchersProvider
import com.example.android.petsave.common.utils.createExceptionHandler
import com.example.android.petsave.search.domain.usecases.GetSearchFilters
import com.example.android.petsave.search.domain.usecases.SearchAnimals
import com.example.android.petsave.search.domain.usecases.SearchAnimalsRemotely
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val searchAnimalsRemotely: SearchAnimalsRemotely,
    private val searchAnimals: SearchAnimals,
    private val getSearchFilters: GetSearchFilters,
    private val uiAnimalMapper: UiAnimalMapper,
    private val dispatchersProvider: DispatchersProvider,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    val state: LiveData<SearchViewState> get() = _state

    private val _state: MutableLiveData<SearchViewState> = MutableLiveData()
    private val querySubject = BehaviorSubject.create<String>()
    private val ageSubject = BehaviorSubject.createDefault("")
    private val typeSubject = BehaviorSubject.createDefault("")

    private var remoteSearchJob: Job = Job()
    private var currentPage = 0

    init {
        _state.value = SearchViewState()
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.PrepareForSearch -> prepareForSearch()
            else -> onSearchParametersUpdate(event)
        }
    }

    private fun onSearchParametersUpdate(event: SearchEvent) {
        remoteSearchJob.cancel(
            CancellationException("New search parameters incoming!")
        )

        when (event) {
            is SearchEvent.QueryInput -> updateQuery(event.input)
            is SearchEvent.AgeValueSelected -> updateAgeValue(event.age)
            is SearchEvent.TypeValueSelected -> updateTypeValue(event.type)
        }
    }

    private fun prepareForSearch() {
        loadFilterValues()
        setupSearchSubscription()
    }

    private fun loadFilterValues() {
        val exceptionHandler =
            createExceptionHandler(
                message = "Failed to get filter values!"
            )

        viewModelScope.launch(exceptionHandler) {
            val (ages, types) = withContext(dispatchersProvider.io()) { getSearchFilters() }

            updateStateWithFilterValues(ages, types)
        }
    }

    private fun createExceptionHandler(message: String): CoroutineExceptionHandler {
        return viewModelScope.createExceptionHandler(message) {
            onFailure(it)
        }
    }

    private fun updateStateWithFilterValues(ages: List<String>, types: List<String>) {
        _state.value = state.value!!.updateToReadyToSearch(ages, types)
    }

    private fun setupSearchSubscription() {
        searchAnimals(querySubject, ageSubject, typeSubject)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                { onSearchResults(it) },
                { onFailure(it) }
            )
            .addTo(compositeDisposable)
    }

    private fun onSearchResults(searchResults: SearchResults) {
        val (animals, searchParameters) = searchResults
        if (animals.isEmpty()) {
            onEmptyCacheResults(searchParameters)
        } else {
            onAnimalList(animals)
        }
    }

    private fun onEmptyCacheResults(searchParameters: SearchParameters) {
        _state.value = state.value!!.updateToSearchingRemotely()
        searchRemotely(searchParameters)
    }

    private fun searchRemotely(searchParameters: SearchParameters) {
        val exceptionHandler = createExceptionHandler(message = "Failed to search remotely.")

        remoteSearchJob = viewModelScope.launch(exceptionHandler) {
            val pagination = withContext(dispatchersProvider.io()) {
                Logger.d("Searching remotely...")

                searchAnimalsRemotely(++currentPage, searchParameters)
            }

            onPaginationInfoObtained(pagination)
        }

        remoteSearchJob.invokeOnCompletion { it?.printStackTrace() }
    }

    private fun updateQuery(input: String) {
        resetPagination()
        querySubject.onNext(input)

        if (input.isEmpty()) {
            setNoSearchQueryState()
        } else {
            setSearchingState()
        }
    }

    private fun updateAgeValue(age: String) {
        ageSubject.onNext(age)
    }

    private fun updateTypeValue(type: String) {
        typeSubject.onNext(type)
    }

    private fun setSearchingState() {
        _state.value = state.value!!.updateToSearching()
    }

    private fun setNoSearchQueryState() {
        _state.value = state.value!!.updateToNoSearchQuery()
    }

    private fun onAnimalList(animals: List<Animal>) {
        _state.value =
            state.value!!.updateToHasSearchResults(animals.map { uiAnimalMapper.mapToView(it) })
    }

    private fun resetPagination() {
        currentPage = 0
    }

    private fun onPaginationInfoObtained(pagination: Pagination) {
        currentPage = pagination.currentPage
    }

    private fun onFailure(throwable: Throwable) {
        _state.value = if (throwable is NoMoreAnimalsException) {
            state.value!!.updateToNoResultsAvailable()
        } else {
            state.value!!.updateToHasFailure(throwable)
        }

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
