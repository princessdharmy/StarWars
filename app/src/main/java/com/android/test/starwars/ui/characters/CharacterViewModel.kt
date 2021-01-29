package com.android.test.starwars.ui.characters

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.test.starwars.data.model.CharacterWithStarships
import com.android.test.starwars.data.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class CharacterViewModel @ViewModelInject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _starshipsLiveData = MutableLiveData<StarshipsRequest>()
    val starshipsLiveData = _starshipsLiveData.switchMap {
        liveData {
            emitSource(characterRepository.getStarships(it.starshipsUrl, it.characterId))
        }
    }

    suspend fun fetchCharacters() : Flow<PagingData<CharacterWithStarships>> {
        return characterRepository.getCharacterWithStarshipsFlowDb().cachedIn(viewModelScope)
    }

    fun fetchStarships(starshipsUrl: List<String>, characterId: Int) {
        _starshipsLiveData.value = StarshipsRequest(starshipsUrl, characterId)
    }
}

data class StarshipsRequest(val starshipsUrl: List<String>, val characterId: Int)