package com.example.kopidannyemilsyam

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // DATA PENGGUNA
    private var fullName = "Coffee Lover"
    private var email = "user@kopianyemil.com"
    private var username = ""
    private var password = ""
    private var phone = "081234567890"

    // DATA PESANAN (productId -> quantity)
    private val selectedProducts = mutableMapOf<Int, Int>()

    // DATA ALAMAT PENGIRIMAN
    private var deliveryName = ""
    private var deliveryPhone = ""
    private var deliveryAddress = ""
    private var deliveryDetail = ""

    // STATE FILTER & SEARCH
    private var currentCategory = "All"
    private var searchQuery = ""

    // DUMMY PRODUCTS (Minimal 5 Produk dengan foto lokal & rating)
    private val productList = listOf(
        Product(
            id = 1,
            name = "Kopi Susu Gula Aren",
            description = "Espresso khas Kopi & Nyemil dengan susu segar dan gula aren asli",
            price = 18000,
            category = "Coffee",
            rating = 4.9,
            imageRes = R.drawable.img_kopi_gula_aren,
            emoji = "☕"
        ),
        Product(
            id = 2,
            name = "Cappuccino",
            description = "Espresso kaya rasa dipadu dengan steamed milk dan foam lembut",
            price = 22000,
            category = "Coffee",
            rating = 4.8,
            imageRes = R.drawable.img_cappuccino,
            emoji = "🥤"
        ),
        Product(
            id = 3,
            name = "Matcha Latte",
            description = "Matcha Jepang berkualitas tinggi dengan susu segar hangat",
            price = 24000,
            category = "Non Coffee",
            rating = 4.7,
            imageRes = R.drawable.img_matcha_latte,
            emoji = "🍵"
        ),
        Product(
            id = 4,
            name = "Donat Cokelat",
            description = "Donat kentang empuk dengan lapisan glaze cokelat lezat",
            price = 12000,
            category = "Snacks",
            rating = 4.9,
            imageRes = R.drawable.img_donat_cokelat,
            emoji = "🍩"
        ),
        Product(
            id = 5,
            name = "Sourdough",
            description = "Roti fermentasi alami renyah di luar, empuk gurih di dalam",
            price = 25000,
            category = "Snacks",
            rating = 4.8,
            imageRes = R.drawable.img_sourdough,
            emoji = "🍞"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSplash()
    }

    // FORMAT RUPIAH
    private fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(amount)
    }

    // TOAST
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // TOTAL ITEMS IN CART
    private fun getCartItemCount(): Int {
        return selectedProducts.values.sum()
    }

    // 1. SPLASH SCREEN
    private fun showSplash() {
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                showWelcome()
            }
        }, 1500)
    }

    // 2. WELCOME SCREEN
    private fun showWelcome() {
        setContentView(R.layout.activity_welcome)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            showSignUp()
        }

        findViewById<Button>(R.id.btnWelcomeLogin).setOnClickListener {
            showLogin()
        }
    }

    // 3. SIGN UP OPTIONS
    private fun showSignUp() {
        setContentView(R.layout.activity_signup)

        findViewById<Button>(R.id.btnGoRegister).setOnClickListener {
            showRegister()
        }

        findViewById<Button>(R.id.btnGoLogin).setOnClickListener {
            showLogin()
        }
    }

    // 4. LOGIN
    private fun showLogin() {
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etLoginUsername)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnTogglePassword = findViewById<ImageButton>(R.id.btnToggleLoginPassword)

        var isPasswordVisible = false
        btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            etPassword.setSelection(etPassword.text.length)
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val inputUsername = etUsername.text.toString().trim()
            val inputPassword = etPassword.text.toString()

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                showMessage("Username/Email dan password wajib diisi")
                return@setOnClickListener
            }

            if (username.isNotEmpty()) {
                if (inputUsername == username && inputPassword == password) {
                    showMessage("Login berhasil! Selamat datang kembali.")
                    showHome()
                } else {
                    showMessage("Username atau password salah")
                }
            } else {
                // Quick login if no explicit user registered yet
                fullName = if (inputUsername.contains("@")) inputUsername.substringBefore("@") else inputUsername
                username = inputUsername
                password = inputPassword
                showMessage("Login berhasil!")
                showHome()
            }
        }

        findViewById<TextView>(R.id.tvLoginToRegister).setOnClickListener {
            showRegister()
        }
    }

    // 5. REGISTER
    private fun showRegister() {
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etFullName)
        val etEmailInput = findViewById<EditText>(R.id.etRegisterEmail)
        val etUsername = findViewById<EditText>(R.id.etRegisterUsername)
        val etPassword = findViewById<EditText>(R.id.etRegisterPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            val inputName = etName.text.toString().trim()
            val inputEmail = etEmailInput.text.toString().trim()
            val inputUsername = etUsername.text.toString().trim()
            val inputPassword = etPassword.text.toString()
            val inputConfirmPassword = etConfirmPassword.text.toString()

            if (inputName.isEmpty() || inputUsername.isEmpty() || inputPassword.isEmpty()) {
                showMessage("Semua kolom wajib diisi")
                return@setOnClickListener
            }

            if (inputPassword.length < 6) {
                showMessage("Password minimal 6 karakter")
                return@setOnClickListener
            }

            if (inputConfirmPassword.isNotEmpty() && inputPassword != inputConfirmPassword) {
                showMessage("Konfirmasi password tidak cocok")
                return@setOnClickListener
            }

            fullName = inputName
            if (inputEmail.isNotEmpty()) email = inputEmail
            username = inputUsername
            password = inputPassword

            showMessage("Registrasi berhasil! Silakan login")
            showLogin()
        }

        findViewById<TextView>(R.id.tvRegisterToLogin).setOnClickListener {
            showLogin()
        }
    }

    // 6. HOME SCREEN (SEARCH & CATEGORY FILTER WORKING)
    private fun showHome() {
        setContentView(R.layout.activity_home)

        val greeting = findViewById<TextView>(R.id.tvHomeGreeting)
        greeting.text = "Halo, $fullName!"

        // Cart Badge
        val tvCartBadge = findViewById<TextView>(R.id.tvCartBadge)
        val cartCount = getCartItemCount()
        if (cartCount > 0) {
            tvCartBadge.visibility = View.VISIBLE
            tvCartBadge.text = cartCount.toString()
        } else {
            tvCartBadge.visibility = View.GONE
        }

        val btnHomeCart = findViewById<RelativeLayout>(R.id.btnHomeCart)
        btnHomeCart.setOnClickListener {
            showCart()
        }

        val tvProductCount = findViewById<TextView>(R.id.tvProductCount)
        val recyclerView = findViewById<RecyclerView>(R.id.rvProducts)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.isNestedScrollingEnabled = false

        var filteredList = filterProducts(currentCategory, searchQuery)
        tvProductCount.text = "${filteredList.size} Products"

        val adapter = ProductAdapter(filteredList) { product ->
            val currentQty = selectedProducts[product.id] ?: 0
            selectedProducts[product.id] = currentQty + 1
            showMessage("${product.name} ditambahkan ke keranjang")
            showHome() // Refresh badge and view
        }
        recyclerView.adapter = adapter

        // SEARCH BAR FILTER
        val etSearch = findViewById<EditText>(R.id.etSearchProduct)
        etSearch.setText(searchQuery)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim() ?: ""
                filteredList = filterProducts(currentCategory, searchQuery)
                tvProductCount.text = "${filteredList.size} Products"
                adapter.updateList(filteredList)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // CATEGORY CHIPS FILTER
        val btnCatAll = findViewById<TextView>(R.id.btnCatAll)
        val btnCatCoffee = findViewById<TextView>(R.id.btnCatCoffee)
        val btnCatNonCoffee = findViewById<TextView>(R.id.btnCatNonCoffee)
        val btnCatSnacks = findViewById<TextView>(R.id.btnCatSnacks)

        fun updateCategoryUI(selected: String) {
            currentCategory = selected
            val isAllSelected = selected.equals("All", ignoreCase = true) ||
                    selected.equals("All Menu", ignoreCase = true) ||
                    selected.equals("Semua", ignoreCase = true)

            btnCatAll.setBackgroundResource(if (isAllSelected) R.drawable.bg_category_selected else R.drawable.bg_category_unselected)
            btnCatAll.setTextColor(getColor(if (isAllSelected) R.color.coffee_white else R.color.coffee_dark))

            btnCatCoffee.setBackgroundResource(if (selected == "Coffee") R.drawable.bg_category_selected else R.drawable.bg_category_unselected)
            btnCatCoffee.setTextColor(getColor(if (selected == "Coffee") R.color.coffee_white else R.color.coffee_dark))

            btnCatNonCoffee.setBackgroundResource(if (selected == "Non Coffee") R.drawable.bg_category_selected else R.drawable.bg_category_unselected)
            btnCatNonCoffee.setTextColor(getColor(if (selected == "Non Coffee") R.color.coffee_white else R.color.coffee_dark))

            btnCatSnacks.setBackgroundResource(if (selected == "Snacks") R.drawable.bg_category_selected else R.drawable.bg_category_unselected)
            btnCatSnacks.setTextColor(getColor(if (selected == "Snacks") R.color.coffee_white else R.color.coffee_dark))

            filteredList = filterProducts(currentCategory, searchQuery)
            tvProductCount.text = "${filteredList.size} Products"
            adapter.updateList(filteredList)
        }

        updateCategoryUI(currentCategory)

        btnCatAll.setOnClickListener { updateCategoryUI("All") }
        btnCatCoffee.setOnClickListener { updateCategoryUI("Coffee") }
        btnCatNonCoffee.setOnClickListener { updateCategoryUI("Non Coffee") }
        btnCatSnacks.setOnClickListener { updateCategoryUI("Snacks") }

        // BOTTOM NAVIGATION & ACTIONS
        findViewById<Button>(R.id.btnHome).setOnClickListener {
            showHome()
        }

        findViewById<Button>(R.id.btnHomeCartNav).setOnClickListener {
            showCart()
        }

        findViewById<Button>(R.id.btnHomeProfile).setOnClickListener {
            showProfile()
        }

        findViewById<Button>(R.id.btnGoOrder).setOnClickListener {
            if (selectedProducts.isEmpty()) {
                showMessage("Pilih minimal satu produk dahulu")
            } else {
                showCart()
            }
        }
    }

    private fun filterProducts(category: String, query: String): List<Product> {
        return productList.filter { product ->
            val matchesCategory = category.equals("All", ignoreCase = true) ||
                    category.equals("All Menu", ignoreCase = true) ||
                    category.equals("Semua", ignoreCase = true) ||
                    product.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isEmpty() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    // 7. KERANJANG / CHECKOUT
    private fun showCart() {
        setContentView(R.layout.activity_cart)

        val layoutEmptyCart = findViewById<LinearLayout>(R.id.layoutEmptyCart)
        val layoutCartSummary = findViewById<LinearLayout>(R.id.layoutCartSummary)
        val rvCartItems = findViewById<RecyclerView>(R.id.rvCartItems)
        val tvCartSubtotal = findViewById<TextView>(R.id.tvCartSubtotal)
        val tvCartDeliveryFee = findViewById<TextView>(R.id.tvCartDeliveryFee)
        val tvCartTotal = findViewById<TextView>(R.id.tvCartTotal)

        val cartItemList = mutableListOf<CartItem>()
        for ((prodId, qty) in selectedProducts) {
            val prod = productList.find { it.id == prodId }
            if (prod != null && qty > 0) {
                cartItemList.add(CartItem(prod, qty))
            }
        }

        fun updateCartUI() {
            val subtotal = cartItemList.sumOf { it.product.price * it.quantity }
            val deliveryFee = if (cartItemList.isNotEmpty()) 10000 else 0
            val total = subtotal + deliveryFee

            tvCartSubtotal.text = formatRupiah(subtotal)
            tvCartDeliveryFee.text = formatRupiah(deliveryFee)
            tvCartTotal.text = formatRupiah(total)

            if (cartItemList.isEmpty()) {
                layoutEmptyCart.visibility = View.VISIBLE
                layoutCartSummary.visibility = View.GONE
                rvCartItems.visibility = View.GONE
            } else {
                layoutEmptyCart.visibility = View.GONE
                layoutCartSummary.visibility = View.VISIBLE
                rvCartItems.visibility = View.VISIBLE
            }
        }

        rvCartItems.layoutManager = LinearLayoutManager(this)
        val cartAdapter = CartAdapter(cartItemList) { cartItem, delta ->
            val newQty = cartItem.quantity + delta
            if (newQty <= 0) {
                selectedProducts.remove(cartItem.product.id)
                cartItemList.remove(cartItem)
            } else {
                cartItem.quantity = newQty
                selectedProducts[cartItem.product.id] = newQty
            }
            showCart() // Refresh cart view
        }
        rvCartItems.adapter = cartAdapter
        updateCartUI()

        findViewById<Button>(R.id.btnProceedToAddress).setOnClickListener {
            if (selectedProducts.isEmpty()) {
                showMessage("Keranjangmu masih kosong")
            } else {
                showAddress()
            }
        }

        findViewById<Button>(R.id.btnCartBackHome).setOnClickListener {
            showHome()
        }
    }

    // 8. PROFILE
    private fun showProfile() {
        setContentView(R.layout.activity_profile)

        val tvGreeting = findViewById<TextView>(R.id.tvProfileGreeting)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val etName = findViewById<EditText>(R.id.etProfileName)
        val etEmailInput = findViewById<EditText>(R.id.etProfileEmailInput)

        tvGreeting.text = "Halo, $fullName!"
        tvEmail.text = email
        etName.setText(fullName)
        etEmailInput.setText(email)

        findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            val newName = etName.text.toString().trim()
            val newEmail = etEmailInput.text.toString().trim()

            if (newName.isEmpty()) {
                showMessage("Nama tidak boleh kosong")
                return@setOnClickListener
            }

            fullName = newName
            if (newEmail.isNotEmpty()) email = newEmail

            showMessage("Profil berhasil diperbarui")
            showProfile()
        }

        findViewById<Button>(R.id.btnProfileCart).setOnClickListener {
            showCart()
        }

        findViewById<Button>(R.id.btnProfileHome).setOnClickListener {
            showHome()
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            username = ""
            password = ""
            selectedProducts.clear()
            showMessage("Berhasil logout")
            showWelcome()
        }
    }

    // 9. ALAMAT PENGIRIMAN
    private fun showAddress() {
        setContentView(R.layout.activity_address)

        val etName = findViewById<EditText>(R.id.etAddressName)
        val etPhone = findViewById<EditText>(R.id.etAddressPhone)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etDetail = findViewById<EditText>(R.id.etAddressDetail)

        etName.setText(if (deliveryName.isNotEmpty()) deliveryName else fullName)
        etPhone.setText(if (deliveryPhone.isNotEmpty()) deliveryPhone else phone)
        etAddress.setText(deliveryAddress)
        etDetail.setText(deliveryDetail)

        findViewById<Button>(R.id.btnSubmitOrder).setOnClickListener {
            val inputName = etName.text.toString().trim()
            val inputPhone = etPhone.text.toString().trim()
            val inputAddress = etAddress.text.toString().trim()
            val inputDetail = etDetail.text.toString().trim()

            if (inputName.isEmpty() || inputPhone.isEmpty() || inputAddress.isEmpty()) {
                showMessage("Lengkapi nama, nomor telepon, dan alamat pengiriman")
                return@setOnClickListener
            }

            deliveryName = inputName
            deliveryPhone = inputPhone
            deliveryAddress = inputAddress
            deliveryDetail = inputDetail

            showConfirmation()
        }

        findViewById<Button>(R.id.btnBackCart).setOnClickListener {
            showCart()
        }

        findViewById<Button>(R.id.btnBackHome).setOnClickListener {
            showHome()
        }
    }

    // 10. KONFIRMASI PESANAN
    private fun showConfirmation() {
        setContentView(R.layout.activity_confirmation)

        val subtotal = productList.sumOf { product ->
            product.price * (selectedProducts[product.id] ?: 0)
        }
        val deliveryFee = 10000
        val total = subtotal + deliveryFee

        val orderSummary = productList
            .filter { (selectedProducts[it.id] ?: 0) > 0 }
            .joinToString("\n") { product ->
                val quantity = selectedProducts[product.id] ?: 0
                val linePrice = product.price * quantity
                "• ${product.name} x$quantity — ${formatRupiah(linePrice)}"
            }

        findViewById<TextView>(R.id.tvConfirmationName).text = "Penerima: $deliveryName ($deliveryPhone)"

        findViewById<TextView>(R.id.tvConfirmationOrder).text =
            "Daftar Pesanan:\n$orderSummary\n\nSubtotal: ${formatRupiah(subtotal)}\nOngkos Kirim: ${formatRupiah(deliveryFee)}\nTotal Bayar: ${formatRupiah(total)}"

        findViewById<TextView>(R.id.tvConfirmationAddress).text =
            "Alamat: $deliveryAddress\nPatokan: ${if (deliveryDetail.isNotEmpty()) deliveryDetail else "-"}"

        findViewById<Button>(R.id.btnFinish).setOnClickListener {
            selectedProducts.clear()
            showMessage("Pesanan kamu berhasil dibuat! Terima kasih sudah berbelanja.")
            showHome()
        }
    }
}
