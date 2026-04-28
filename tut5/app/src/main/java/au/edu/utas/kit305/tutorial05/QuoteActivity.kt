// the majority of this page was done with the assistance of AI to make the bulk of the boiler plate
// i gave it my previous work which was majority my own work so it uses tool i have used throughout
// this project, I made sure to read through it all and undertand it and make the changes to get it
// working properly. The checkbox logic for example
package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityQuoteBinding
import au.edu.utas.kit305.tutorial05.databinding.QuoteHouseItemBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

const val ROOM_FLAT_COST = 200

class QuoteActivity : AppCompatActivity() {

    private lateinit var ui: ActivityQuoteBinding
    private val db = Firebase.firestore
    private val houseList = mutableListOf<HouseQuoteData>()
    private lateinit var adapter: QuoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityQuoteBinding.inflate(layoutInflater)
        setContentView(ui.root)

        adapter = QuoteAdapter(houseList) { recalculateTotal() }
        ui.quoteRecyclerView.layoutManager = LinearLayoutManager(this)
        ui.quoteRecyclerView.adapter = adapter

        ui.lblTotal.text = "Total: $0"
        loadAllData()
    }

    private fun recalculateTotal() {
        var total = 0
        for (house in houseList) {
            for (room in house.rooms) {
                if (room.included) {
                    total += ROOM_FLAT_COST
                    for (space in room.spaces) {
                        if (space.included) {
                            total += space.space.s_price
                        }
                    }
                }
            }
        }
        ui.lblTotal.text = "Total: $$total"
    }

    private fun loadAllData() {
        ui.lblTotal.text = "Loading..."
        db.collection("houses")
            .get()
            .addOnSuccessListener { houseDocs ->
                houseList.clear()
                var housesRemaining = houseDocs.size()

                if (housesRemaining == 0) {
                    ui.lblTotal.text = "Total: $0"
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                for (houseDoc in houseDocs) {
                    val house = houseDoc.toObject<House>()
                    house.id = houseDoc.id
                    val houseQuote = HouseQuoteData(house = house)

                    db.collection("houses").document(house.id)
                        .collection("rooms").get()
                        .addOnSuccessListener { roomDocs ->
                            var roomsRemaining = roomDocs.size()

                            if (roomsRemaining == 0) {
                                houseList.add(houseQuote)
                                housesRemaining--
                                if (housesRemaining == 0) finaliseLoad()
                                return@addOnSuccessListener
                            }

                            for (roomDoc in roomDocs) {
                                val room = roomDoc.toObject<Room>()
                                room.id = roomDoc.id
                                val roomQuote = RoomQuoteData(room = room)

                                roomDoc.reference.collection("spaces").get()
                                    .addOnSuccessListener { spaceDocs ->
                                        for (spaceDoc in spaceDocs) {
                                            val space = spaceDoc.toObject<Space>()
                                            space.id = spaceDoc.id
                                            roomQuote.spaces.add(SpaceQuoteData(space = space))
                                        }
                                        houseQuote.rooms.add(roomQuote)
                                        roomsRemaining--
                                        if (roomsRemaining == 0) {
                                            houseList.add(houseQuote)
                                            housesRemaining--
                                            if (housesRemaining == 0) finaliseLoad()
                                        }
                                    }
                                    .addOnFailureListener {
                                        Log.e(FIREBASE_TAG, "Failed to load spaces for room ${room.id}")
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Log.e(FIREBASE_TAG, "Failed to load rooms for house ${house.id}")
                        }
                }
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Failed to load houses")
                ui.lblTotal.text = "Error loading data"
            }
    }

    private fun finaliseLoad() {
        adapter.notifyDataSetChanged()
        recalculateTotal()
    }
}

class QuoteAdapter(
    private val houses: MutableList<HouseQuoteData>,
    private val onChanged: () -> Unit
) : RecyclerView.Adapter<QuoteAdapter.HouseHolder>() {

    inner class HouseHolder(val ui: QuoteHouseItemBinding) : RecyclerView.ViewHolder(ui.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseHolder {
        val ui = QuoteHouseItemBinding.inflate(
            android.view.LayoutInflater.from(parent.context), parent, false
        )
        return HouseHolder(ui)
    }

    override fun getItemCount() = houses.size

    override fun onBindViewHolder(holder: HouseHolder, position: Int) {
        val houseData = houses[position]
        holder.ui.txtHouseAddress.text = houseData.house.h_address
        holder.ui.roomContainer.removeAllViews()

        // ai used for the design of the boxes
        for (roomData in houseData.rooms) {
            val context = holder.ui.root.context

            val roomRow = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 8, 0, 0)
            }

            val roomCheck = CheckBox(context).apply {
                text = "${roomData.room.r_name}  (+$$ROOM_FLAT_COST flat)"
                isChecked = roomData.included
                textSize = 15f
            }

            val spaceCheckboxes = mutableListOf<CheckBox>()

            var noCascade = false

            roomCheck.setOnCheckedChangeListener { _, isChecked ->
                if (noCascade) return@setOnCheckedChangeListener

                roomData.included = isChecked

                // cascade to all spaces
                for ((index, spaceData) in roomData.spaces.withIndex()) {
                    spaceData.included = isChecked
                    spaceCheckboxes[index].isChecked = isChecked
                }
                onChanged()

            }

            roomRow.addView(roomCheck)

            for (spaceData in roomData.spaces) {
                val currentRoom = roomData
                val spaceCheck = CheckBox(context).apply {
                    text = "  ${spaceData.space.s_name} — ${spaceData.space.s_type}  (+$${spaceData.space.s_price})\n" +
                            "       ${spaceData.space.s_product_n} | ${spaceData.space.s_variant}\n" +
                            "       ${spaceData.space.s_width}mm x ${spaceData.space.s_height}mm"
                    isChecked = spaceData.included
                    textSize = 13f
                    setPadding(64, 0, 0, 0)
                }

                spaceCheck.setOnCheckedChangeListener { _, isChecked ->
                    spaceData.included = isChecked
                    // room checked if ANY space is checked
                    val anyChecked = currentRoom.spaces.any { it.included }
                    currentRoom.included = anyChecked
                    // update room checkbox directly — no notifyItemChanged
                    noCascade = true
                    roomCheck.isChecked = anyChecked
                    noCascade = false
                    onChanged()
                }

                spaceCheckboxes.add(spaceCheck)
                roomRow.addView(spaceCheck)
            }

            holder.ui.roomContainer.addView(roomRow)
        }
    }
}