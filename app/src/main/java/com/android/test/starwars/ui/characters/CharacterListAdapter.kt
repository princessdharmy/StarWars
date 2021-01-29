package com.android.test.starwars.ui.characters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.test.starwars.R
import com.android.test.starwars.data.model.CharacterWithStarships
import kotlinx.android.synthetic.main.character_item_list.view.*

class CharacterListAdapter(private val onCharacterClickListener: OnCharacterClickListener) :
    PagingDataAdapter<CharacterWithStarships, CharacterListAdapter.CharacterViewHolder>(
        DATA_COMPARATOR
    ) {

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val characterItem = getItem(position)
        if (characterItem != null) {
            holder.bind(characterItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.character_item_list, parent, false)
        return CharacterViewHolder(view)
    }


    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(data: CharacterWithStarships) = with(itemView) {
            character_name.text = data.character.name

            setOnClickListener { onCharacterClickListener.onCharacterClickListener(data) }
        }

    }

    interface OnCharacterClickListener {
        fun onCharacterClickListener(data: CharacterWithStarships)
    }

    companion object {
        private val DATA_COMPARATOR = object : DiffUtil.ItemCallback<CharacterWithStarships>() {
            override fun areItemsTheSame(
                oldItem: CharacterWithStarships,
                newItem: CharacterWithStarships
            ) =
                oldItem.character.name == newItem.character.name

            override fun areContentsTheSame(
                oldItem: CharacterWithStarships,
                newItem: CharacterWithStarships
            ) =
                oldItem.character.name == newItem.character.name
        }
    }
}