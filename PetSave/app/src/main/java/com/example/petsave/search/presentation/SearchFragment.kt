package com.example.petsave.search.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.petsave.R
import com.example.petsave.common.domain.model.NoMoreAnimalsException
import com.example.petsave.common.presentation.AnimalsAdapter
import com.example.petsave.common.presentation.Event
import com.example.petsave.databinding.FragmentSearchBinding
import com.example.petsave.search.usecases.GetSearchFilters
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okio.IOException
import retrofit2.HttpException

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: FragmentSearchBinding? = null

    private val viewModel: SearchFragmentViewModel by viewModels()

    companion object {
        private const val ITEMS_PER_ROW = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        prepareForSearch()
    }

    private fun prepareForSearch() {
        setupFilterListeners()
        setupSearchViewListener()
        viewModel.onEvent(SearchEvent.PrepareForSearch)
    }

    private fun setupUI() {
        val adapter = createAdapter()
        setupRecyclerView(adapter)
        observeViewStateUpdates(adapter)
    }

    private fun createAdapter(): AnimalsAdapter {
        return AnimalsAdapter()
    }

    private fun setupRecyclerView(searchAdapter: AnimalsAdapter) {
        binding.searchRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(requireContext(), ITEMS_PER_ROW)
            setHasFixedSize(true)
        }
    }

    private fun setupSearchViewListener() {
        val searchView = binding.searchWidget.search
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.onEvent(SearchEvent.QueryInput(query.orEmpty()))
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.onEvent(SearchEvent.QueryInput(newText.orEmpty()))
                    return true
                }
            }
        )
    }

    private fun createFilterAdapter(
        adapterValues: List<String>
    ): ArrayAdapter<String> {
        return ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            adapterValues
        )
    }

    private fun setupFilterListeners() {
        with(binding.searchWidget) {
            setupFilterListenerFor(age) { item ->
                viewModel.onEvent(SearchEvent.AgeValueSelected(item))
            }
            setupFilterListenerFor(type) { item ->
                viewModel.onEvent(SearchEvent.TypeValueSelected(item))
            }
        }
    }

    private fun updateScreenState(newState: SearchViewState, searchAdapter: AnimalsAdapter) {
        val (
            inInitialState,
            searchResults,
            ageFilterValues,
            typeFilterValues,
            searchingRemotely,
            noResultsState,
            failure
        ) = newState

        updateInitialStateViews(inInitialState)
        searchAdapter.submitList(searchResults)

        with(binding.searchWidget) {
            setupFilterValues(age, ageFilterValues.getContentIfNotHandled())
            setupFilterValues(type, typeFilterValues.getContentIfNotHandled())
        }

        updateRemoteSearchViews(searchingRemotely)
        updateNoResultsViews(noResultsState)
        handleFailures(failure)
    }

    private fun setupFilterValues(
        filter: AutoCompleteTextView,
        filterValues: List<String>?
    ) {
        if (filterValues == null || filterValues.isEmpty()) return

        filter.setAdapter(createFilterAdapter(filterValues))
        filter.setText(GetSearchFilters.NO_FILTER_SELECTED, false)
    }

    private fun updateRemoteSearchViews(searchingRemotely: Boolean) {
        binding.searchRemotelyProgressBar.isVisible = searchingRemotely
        binding.searchRemotelyText.isVisible = searchingRemotely
    }

    private fun updateNoResultsViews(noResultsState: Boolean) {
        binding.noSearchResultsImageView.isVisible = noResultsState
        binding.noSearchResultsText.isVisible = noResultsState
    }

    private fun updateInitialStateViews(inInitialState: Boolean) {
        binding.initialSearchImageView.isVisible = inInitialState
        binding.initialSearchText.isVisible = inInitialState
    }

    private fun setupFilterListenerFor(
        filter: AutoCompleteTextView,
        block: (item: String) -> Unit
    ) {

        filter.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                parent?.let {
                    block(it.adapter.getItem(position) as String)
                }
            }
    }

    private fun observeViewStateUpdates(searchAdapter: AnimalsAdapter) {
        viewModel.state.observe(viewLifecycleOwner) { searchViewState ->
            updateScreenState(searchViewState, searchAdapter)
        }
    }

    private fun handleFailures(failure: Event<Throwable>?) {
        val unhandledFailure = failure?.getContentIfNotHandled() ?: return

        handleThrowable(unhandledFailure)
    }

    private fun handleThrowable(exception: Throwable) {
        val fallbackMessage = getString(R.string.an_error_occurred)
        val snackbarMessage = when (exception) {
            is NoMoreAnimalsException -> exception.message ?: fallbackMessage
            is IOException, is HttpException -> fallbackMessage
            else -> ""
        }

        if (snackbarMessage.isNotEmpty()) {
            Snackbar.make(requireView(), snackbarMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
