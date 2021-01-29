package com.android.test.starwars.ui.characters

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.android.test.starwars.R
import com.android.test.starwars.data.model.CharacterWithStarships
import com.android.test.starwars.databinding.FragmentCharactersBinding
import com.android.test.starwars.utils.Utils.hasInternetConnection
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CharactersFragment : Fragment(), CharacterListAdapter.OnCharacterClickListener {

    private lateinit var binding: FragmentCharactersBinding
    private val viewModel: CharacterViewModel by viewModels()
    private lateinit var adapter: CharacterListAdapter
    private var characterRecyclerViewState: Parcelable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCharactersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        fetchCharacter()
        clickListeners()
    }

    private fun setUpAdapter() {
        adapter = CharacterListAdapter(this)
        binding.characterRecyclerview.adapter = adapter

        // add dividers between RecyclerView's row items
        binding.characterRecyclerview.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                .apply {
                    this.setDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.custom_line_divider
                        )!!
                    )
                }
        )
        binding.characterRecyclerview.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoaderStateAdapter { adapter.retry() },
            footer = LoaderStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            // Only show the list if refresh succeeds.
            binding.characterRecyclerview.isVisible =
                loadState.source.refresh is LoadState.NotLoading
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "Oops! ${it.error}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            adapter.loadStateFlow
                // Only emit when REFRESH LoadState for RemoteMediator changes.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.characterRecyclerview.scrollToPosition(0) }
        }
    }

    private fun fetchCharacter() {
        if (hasInternetConnection(requireContext())) {
            lifecycleScope.launch {
                viewModel.fetchCharacters().collectLatest {
                    adapter.submitData(it)

                    // Restore the state of the list
                    binding.characterRecyclerview.post {
                        if (characterRecyclerViewState != null)
                            binding.characterRecyclerview.layoutManager?.onRestoreInstanceState(characterRecyclerViewState)
                        characterRecyclerViewState = null
                    }
                }
            }
        } else {
            showNoInternet()
        }
    }

    private fun clickListeners() {
        binding.retryButton.setOnClickListener { adapter.retry() }
    }

    private fun showNoInternet() {
        Snackbar.make(
            binding.constraintLayout,
            getString(R.string.internet_connection_message), Snackbar.LENGTH_INDEFINITE
        )
            .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.yellow))
            .setAction(getString(R.string.button_retry)) {
                fetchCharacter()
            }.show()
    }

    override fun onCharacterClickListener(data: CharacterWithStarships) {
        // Save the state of the list
        characterRecyclerViewState = binding.characterRecyclerview.layoutManager?.onSaveInstanceState()

        findNavController().navigate(
            CharactersFragmentDirections.toCharacterDetailsFragment(data)
        )
    }

}