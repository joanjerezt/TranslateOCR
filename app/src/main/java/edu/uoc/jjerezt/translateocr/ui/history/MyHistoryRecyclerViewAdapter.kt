package edu.uoc.jjerezt.translateocr.ui.history

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import edu.uoc.jjerezt.translateocr.R
import edu.uoc.jjerezt.translateocr.databinding.FragmentHistoryBinding
import edu.uoc.jjerezt.translateocr.runtime.db.AppDatabase
import edu.uoc.jjerezt.translateocr.runtime.db.Entry
import edu.uoc.jjerezt.translateocr.runtime.db.EntryRepository
import edu.uoc.jjerezt.translateocr.runtime.db.EntryViewModel
import edu.uoc.jjerezt.translateocr.runtime.dict.Language
import edu.uoc.jjerezt.translateocr.ui.history.HistoryFragment.HistoryItem
import java.text.DateFormat
import java.util.Locale


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

        /**
         * S'inicialitzen les variables per a cada registre de l'historial
         */

        holder.origText.text = item.origText
        val spanned = HtmlCompat.fromHtml(item.destText, HtmlCompat.FROM_HTML_MODE_COMPACT)
        holder.destText.text = spanned
        val df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.ENGLISH)
        val date = df.format(item.timestamp)
        holder.date.text = date
        holder.date.setTypeface(holder.date.typeface, Typeface.BOLD)
        favoriteStatus.add(index=position, element = item.favorite)

        /**
         * S'obté la bandera del país del idioma d'origen i de destí
         */

        val firstLanguage = item.mode.subSequence(0,2).toString()
        val secondLanguage = item.mode.subSequence(3,5).toString()

        val firstCountry = Language().getCountryByLanguageCode(firstLanguage)
        val secondCountry = Language().getCountryByLanguageCode(secondLanguage)

        var firstFlag = "🏳"
        if (firstLanguage != "gl"){
            firstFlag = Language().getFlagByCountryCode(firstCountry[0]) + Language().getFlagByCountryCode(firstCountry[1])
        }
        var secondFlag = "🏳"
        if(secondLanguage != "gl"){
            secondFlag = Language().getFlagByCountryCode(secondCountry[0]) + Language().getFlagByCountryCode(secondCountry[1])
        }

        holder.origFlag.text = firstFlag
        holder.destFlag.text = secondFlag

        /**
         * S'inicialitza l'estat de favorit
         */

        if(favoriteStatus[position]){
            holder.favorite.setBackgroundResource(R.drawable.baseline_favorite_24)
        }
        else{
            holder.favorite.setBackgroundResource(R.drawable.baseline_favorite_border_24)
        }

        /**
         * Si es prem el botó de favorit, es marcarà el registre com a favorit i es desplaçarà al capdamunt de la llista
         */

        holder.favorite.setOnClickListener {
            val db = Room.databaseBuilder(holder.itemView.context, AppDatabase::class.java, "translateocr").build()
            val entry = Entry(item.id, item.timestamp, item.origText, item.destText, item.mode, item.code, !item.favorite)
            EntryViewModel(entryRepository = EntryRepository()).edit(db, entry)
            if(favoriteStatus[position]){
                holder.favorite.setBackgroundResource(R.drawable.baseline_favorite_border_24)
                values[position].favorite = !values[position].favorite
                values.sortBy { it.timestamp }
                for(i in values.indices){
                    favoriteStatus[position] = values[i].favorite
                }
                notifyItemRangeChanged(0, values.size)
            }
            else{
                holder.favorite.setBackgroundResource(R.drawable.baseline_favorite_24)
                favoriteStatus[position] = !favoriteStatus[position]
                values.add(0, values[position])
                values.removeAt(position+1)
                notifyItemMoved(position, 0)
                favoriteStatus.add(0, favoriteStatus[position])
                favoriteStatus.removeAt(position+1)
            }
            EntryViewModel(entryRepository = EntryRepository()).close(db)

        }

        /**
         * Si es prem el botó d'esborrar, s'esborrarà el registre de la pantalla i la base de dades
         */

        holder.remove.setOnClickListener {
            val db = Room.databaseBuilder(holder.itemView.context, AppDatabase::class.java, "translateocr").build()
            val entry = Entry(item.id, item.timestamp, item.origText, item.destText, item.mode, item.code, !item.favorite)
            EntryViewModel(entryRepository = EntryRepository()).remove(db, entry)
            values.removeAt(position)
            notifyItemRemoved(position)
        }

        /**
         * Es mostrarà un missatge emergent amb el text complet del camp
         */

        holder.origText.setOnClickListener {
            val message = Snackbar.make(holder.itemView, holder.origText.text, 5000)
            message.show()
        }

        holder.destText.setOnClickListener {
            val message = Snackbar.make(holder.itemView, holder.destText.text, 5000)
            message.show()
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val origText: TextView = binding.originalText
        val destText: TextView = binding.translatedText
        val favorite: Button = binding.favourite
        val date : TextView = binding.dateEntry
        val remove: Button = binding.remove
        val origFlag: TextView = binding.flagOrig
        val destFlag: TextView = binding.flagDest

        override fun toString(): String {
            return super.toString() + " '" + destText.text + "'"
        }
    }

}