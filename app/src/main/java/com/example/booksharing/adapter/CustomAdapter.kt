import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.booksharing.R
import com.example.booksharing.ui.firebase_data.ExchangeHistory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomAdapter(
    context: Context,
    private val resource: Int,
    private val data: List<ExchangeHistory>
) : ArrayAdapter<ExchangeHistory>(context, resource, data) {
    private val exchangeHistoryCollection = Firebase.firestore.collection("exchange_history")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(resource, parent, false)

            viewHolder = ViewHolder()
            viewHolder.textView = view.findViewById(R.id.textViewItem)
            viewHolder.buttonAccept = view.findViewById(R.id.buttonAccept)
            viewHolder.buttonReject = view.findViewById(R.id.buttonReject)

            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val exchangeHistory = getItem(position)

        viewHolder.textView.text = exchangeHistory?.getExchangeDescription()
        viewHolder.buttonAccept.setOnClickListener {
            exchangeHistory?.let { acceptExchange(it) }
        }
        viewHolder.buttonReject.setOnClickListener {
            exchangeHistory?.let { rejectExchange(it) }
        }

        return view
    }

    private fun rejectExchange(exchangeHistory: ExchangeHistory)= CoroutineScope(Dispatchers.IO).launch {
        exchangeHistoryCollection
            .whereEqualTo("title", exchangeHistory.title)
            .whereEqualTo("author", exchangeHistory.author)
            .whereEqualTo("date", exchangeHistory.date)
            .whereEqualTo("bookOwner", exchangeHistory.bookOwner)
            .whereEqualTo("bookborrower", exchangeHistory.bookborrower)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    exchangeHistoryCollection.document(document.id).update("state", "odrzucono")
                }
            }
            .addOnFailureListener { e ->
                Log.e("CustomAdapter", "Błąd podczas akceptowania wymiany: ${e.message}")
            }
    }

    private fun acceptExchange(exchangeHistory: ExchangeHistory)= CoroutineScope(Dispatchers.IO).launch {
        exchangeHistoryCollection
            .whereEqualTo("title", exchangeHistory.title)
            .whereEqualTo("author", exchangeHistory.author)
            .whereEqualTo("date", exchangeHistory.date)
            .whereEqualTo("bookOwner", exchangeHistory.bookOwner)
            .whereEqualTo("bookborrower", exchangeHistory.bookborrower)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    exchangeHistoryCollection.document(document.id).update("state", "pożyczona")
                }
            }
            .addOnFailureListener { e ->
                Log.e("CustomAdapter", "Błąd podczas akceptowania wymiany: ${e.message}")
            }
    }

    private class ViewHolder {
        lateinit var textView: TextView
        lateinit var buttonAccept: Button
        lateinit var buttonReject: Button
    }
}