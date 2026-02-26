package com.example.cropcart

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SellFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SellFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton

    // Mock data — you’ll replace this with your own stored products
    private val myProducts = mutableListOf<FeaturedProduct>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sell, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addButton = view.findViewById(R.id.btnAddProduct)

        val stats = listOf(
            SellerStat("Orders Received", "23", R.drawable.ic_orders),
            SellerStat("Transactions", "₹12.4K", R.drawable.ic_transaction),
            SellerStat("Active Listings", "8", R.drawable.ic_products),
            SellerStat("Seller Rating", "4.7★", R.drawable.ic_star)
        )

        val statAdapter = SellerStatAdapter(stats)
        val statsRecyclerView = view.findViewById<RecyclerView>(R.id.sellerStatsRecyclerView)
        val btnOrders = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnOrders)
        val btnMyproducts = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnYourProducts)
        val btnAddProduct = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnAddOrder)

        btnMyproducts.setOnClickListener {
            val intent = Intent(requireContext(), MyProductsActivity::class.java)
            startActivity(intent)
        }

        btnOrders.setOnClickListener {
            val intent = Intent(requireContext(), SellerOrdersActivity::class.java)
            startActivity(intent)
        }

        btnAddProduct.setOnClickListener {
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }


        statsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        statsRecyclerView.adapter = statAdapter





        addButton.setOnClickListener {
            // Open Add Product screen
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }
    }
}