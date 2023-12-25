package edu.uoc.jjerezt.translateocr.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import edu.uoc.jjerezt.translateocr.R
import edu.uoc.jjerezt.translateocr.databinding.FragmentHistoryBinding
import edu.uoc.jjerezt.translateocr.runtime.db.AppDatabase
import edu.uoc.jjerezt.translateocr.runtime.db.Entry
import edu.uoc.jjerezt.translateocr.runtime.db.EntryRepository
import edu.uoc.jjerezt.translateocr.runtime.db.EntryViewModel
import edu.uoc.jjerezt.translateocr.ui.history.HistoryFragment.HistoryItem

/**
 * [RecyclerView.Adapter] that can display a [HistoryItem].
 */
class MyHistoryRecyclerViewAdapter(
    private val values: MutableList<HistoryItem>
) : RecyclerView.Adapter<MyHistoryRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private val favoriteStatus : MutableList<Boolean> = ArrayList()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.origText.text = item.origText
        val spanned = HtmlCompat.fromHtml(item.destText, HtmlCompat.FROM_HTML_MODE_COMPACT)
        holder.destText.text = spanned
        holder.date.text = item.timestamp.toString()
        favoriteStatus.add(index=position, element = item.favorite)

        if(favoriteStatus[position]){
            holder.favorite.setBackgroundResource(R.drawable.baseline_favorite_24)
        }
        else{
            holder.favorite.setBackgroundResource(R.drawable.baseline_favorite_border_24)
        }
        holder.favorite.setOnClickListener {
            val db = Room.databaseBuilder(holder.itemView.context, AppDatabase::class.java, "translateocr").build()
            val entry = Entry(item.id, item.timestamp, item.origText, item.destText, item.mode, item.code, !item.favorite)
            EntryViewModel(entryRepository = EntryRepository()).edit(db, entry)
            if(favoriteStatus[position]){
                holder.favorite.setBackgroundResource(R.drawable.baseline_favorite_border_24)
            }
            else{
                holder.favorite.setBackgroundResource(R.drawable.baseline_favorite_24)
            }
            favoriteStatus[position] = !favoriteStatus[position]
            EntryViewModel(entryRepository = EntryRepository()).close(db)
        }


    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val origText: TextView = binding.originalText
        val destText: TextView = binding.translatedText
        val favorite: Button = binding.favourite
        val date : TextView = binding.dateEntry

        override fun toString(): String {
            return super.toString() + " '" + destText.text + "'"
        }
    }

}