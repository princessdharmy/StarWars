package com.android.test.starwars.ui.characterdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.test.starwars.R
import com.android.test.starwars.data.model.CharacterWithStarships
import com.android.test.starwars.data.networkresource.NetworkStatus
import com.android.test.starwars.databinding.FragmentCharacterDetailsBinding
import com.android.test.starwars.ui.characters.CharacterViewModel
import com.android.test.starwars.utils.Utils.hasInternetConnection
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CharacterDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCharacterDetailsBinding
    private val viewModel: CharacterViewModel by viewModels()
    private var characterWithStarships: CharacterWithStarships? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCharacterDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        clickListeners()
        setUpObservers()
    }

    private fun getArgs() {
        val data: CharacterDetailsFragmentArgs by navArgs()
        characterWithStarships = data.characterWithStarships
    }

    private fun setupView() {
        binding.characterName.text = characterWithStarships?.character?.name
        binding.characterGender.text = characterWithStarships?.character?.gender
        binding.characterHeight.text = characterWithStarships?.character?.height

        validateNetworkState()
    }

    private fun fetchStarships() {
        characterWithStarships?.character?.starships?.let {
            viewModel.fetchStarships(it, characterWithStarships?.character?.id!!)
        }
    }

    private fun setUpObservers() {
        viewModel.starshipsLiveData.observe(viewLifecycleOwner, { result ->
            when (result) {
                is NetworkStatus.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is NetworkStatus.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showError(result.message)
                }
                is NetworkStatus.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showFormattedStarshipsNames(result.data)
                }
            }
        })
    }

    private fun clickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.retryButton.setOnClickListener {
            if (hasInternetConnection(requireContext())) {
                fetchStarships()
                binding.errorContainer.visibility = View.GONE
            }
        }
    }

    private fun showFormattedStarshipsNames(data: CharacterWithStarships? = null) {
        val list = data?.starShips
        val starshipNames = list?.map { it.name }?.joinToString(",")
        binding.characterStarships.text = ""
        binding.characterStarships.text =
            if (starshipNames.isNullOrEmpty()) getString(R.string.not_available)
            else starshipNames
    }

    private fun showError(errorMessage: String? = null) {
        binding.errorContainer.visibility = View.VISIBLE
        binding.errorMsg.text = if (errorMessage.isNullOrEmpty()) getString(R.string.internet_connection_message)
        else errorMessage
    }

    private fun validateNetworkState() {
        if (characterWithStarships?.character?.starships.isNullOrEmpty()) {
            binding.characterStarships.text = getString(R.string.not_available)
        } else {
            if (characterWithStarships?.starShips.isNullOrEmpty() && hasInternetConnection(requireContext())) {
                // shows that the room is empty and the remote data needs to be fetched
                fetchStarships()
            }
            else if (characterWithStarships?.starShips.isNullOrEmpty() && !hasInternetConnection(requireContext())) {
                // shows an error message to connect to the internet
                // show a retry button and click it
                showError()
            }
            else {
                // show the available data whether there is network or not
                showFormattedStarshipsNames(characterWithStarships)
            }
        }
    }
}