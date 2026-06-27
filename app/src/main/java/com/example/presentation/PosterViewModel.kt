package com.example.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ApiLog(
    val timestamp: String,
    val method: String,
    val endpoint: String,
    val status: String,
    val payload: String
)

data class DownloadedPoster(
    val id: String,
    val title: String,
    val category: String,
    val dateLabel: String,
    val drawableResName: String,
    val sloganText: String,
    val timestamp: Long = System.currentTimeMillis()
)

class PosterViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = PosterRepository(database.posterDao())

    // Brand Profile state
    val brandProfile: StateFlow<BrandProfile?> = repository.brandProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Favorite Posters state
    val favoritePosters: StateFlow<List<FavoritePoster>> = repository.favoritePosters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Calendar state
    private val _selectedDate = MutableStateFlow(getCurrentDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    val currentMonth: StateFlow<Int> = _currentMonth.asStateFlow()

    // Smart Poster search and category filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Combined posters flow (smart calendar + search + category filter)
    val postersList: StateFlow<List<PosterTemplate>> = combine(
        _selectedDate,
        _searchQuery,
        _selectedCategory
    ) { date, query, category ->
        var list = if (query.isNotBlank()) {
            PosterCatalog.searchPosters(query)
        } else {
            PosterCatalog.getPostersForDate(date)
        }
        
        if (category != "All") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PosterCatalog.getPostersForDate(getCurrentDateString())
    )

    // Active Selected Poster for Editing
    private val _selectedPoster = MutableStateFlow<PosterTemplate?>(null)
    val selectedPoster: StateFlow<PosterTemplate?> = _selectedPoster.asStateFlow()

    // Selected language for poster slogans ("English", "ગુજરાતી", "हिन्दी")
    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    // Customize Overlay Elements Visibility
    private val _showBrandName = MutableStateFlow(true)
    val showBrandName: StateFlow<Boolean> = _showBrandName.asStateFlow()

    private val _showMobileNumber = MutableStateFlow(true)
    val showMobileNumber: StateFlow<Boolean> = _showMobileNumber.asStateFlow()

    private val _showAddress = MutableStateFlow(true)
    val showAddress: StateFlow<Boolean> = _showAddress.asStateFlow()

    private val _showSocialHandle = MutableStateFlow(true)
    val showSocialHandle: StateFlow<Boolean> = _showSocialHandle.asStateFlow()

    private val _showLogo = MutableStateFlow(true)
    val showLogo: StateFlow<Boolean> = _showLogo.asStateFlow()

    private val _showQrCode = MutableStateFlow(true)
    val showQrCode: StateFlow<Boolean> = _showQrCode.asStateFlow()

    private val _customSloganText = MutableStateFlow<String?>(null)
    val customSloganText: StateFlow<String?> = _customSloganText.asStateFlow()

    private val _customColorHex = MutableStateFlow("#FF5722") // Custom color accent
    val customColorHex: StateFlow<String> = _customColorHex.asStateFlow()

    // --- Live API Logs & Telemetry ---
    private val _apiLogs = MutableStateFlow<List<ApiLog>>(emptyList())
    val apiLogs: StateFlow<List<ApiLog>> = _apiLogs.asStateFlow()

    // --- Downloads/Share History ---
    private val _downloadedPosters = MutableStateFlow<List<DownloadedPoster>>(emptyList())
    val downloadedPosters: StateFlow<List<DownloadedPoster>> = _downloadedPosters.asStateFlow()

    fun logApiCall(method: String, endpoint: String, status: String, payload: String) {
        val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
        val time = sdf.format(Date())
        val newLog = ApiLog(time, method, endpoint, status, payload)
        _apiLogs.value = (listOf(newLog) + _apiLogs.value).take(15) // Keep last 15 calls
    }

    fun recordDownload(poster: PosterTemplate, sloganText: String) {
        val newDownload = DownloadedPoster(
            id = "${poster.id}_${System.currentTimeMillis()}",
            title = poster.title,
            category = poster.category,
            dateLabel = poster.dateLabel,
            drawableResName = poster.drawableResName,
            sloganText = sloganText
        )
        _downloadedPosters.value = listOf(newDownload) + _downloadedPosters.value
        
        // Log telemetry event
        logApiCall(
            method = "POST",
            endpoint = "/api/posters/${poster.id}/download",
            status = "201 Created (HD Server Render)",
            payload = """
            {
              "poster_id": "${poster.id}",
              "slogan": "$sloganText",
              "resolution": "HD 1080p (300 DPI)",
              "timestamp": ${System.currentTimeMillis()},
              "cache_status": "synced"
            }
            """.trimIndent()
        )
    }

    init {
        // Automatically select the first poster of today as default selected poster
        val todayPosters = PosterCatalog.getPostersForDate(getCurrentDateString())
        if (todayPosters.isNotEmpty()) {
            _selectedPoster.value = todayPosters.first()
        }
        
        // Setup initial default brand profile if database is empty
        viewModelScope.launch {
            brandProfile.first { true } // wait for initial load
            if (brandProfile.value == null) {
                repository.saveBrandProfile(BrandProfile())
            }
        }

        // Log initial app setup API calls
        logApiCall(
            method = "GET",
            endpoint = "/api/init",
            status = "200 OK",
            payload = """
            {
              "app": "Poster365 Calendar",
              "api_version": "v1.4",
              "secure_ssl": true,
              "offline_cache_size_mb": 42.6,
              "unlimited_data_support": true,
              "user_status": "Premium"
            }
            """.trimIndent()
        )
    }

    // --- Actions ---

    fun selectDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dateString = sdf.format(calendar.time)
        _selectedDate.value = dateString
        _currentYear.value = year
        _currentMonth.value = month

        // Automatically update the selected editing poster to match the date if available
        val datePosters = PosterCatalog.getPostersForDate(dateString)
        if (datePosters.isNotEmpty()) {
            _selectedPoster.value = datePosters.first()
        }

        // Trigger dynamic simulated API Log
        val eventName = datePosters.firstOrNull { it.dateStr == dateString }?.title ?: "Special Calendar Day"
        logApiCall(
            method = "GET",
            endpoint = "/api/posters?date=$dateString&year=$year&month=${month + 1}",
            status = "200 OK (Latency: 85ms)",
            payload = """
            {
              "date": "$dateString",
              "event": "$eventName",
              "posters_count": ${datePosters.size},
              "posters": [
                ${datePosters.joinToString(",") { """{"id": "${it.id}", "title": "${it.title}", "category": "${it.category}", "image_url": "https://api.poster365.com/images/${it.id}.jpg"}""" }}
              ]
            }
            """.trimIndent()
        )
    }

    fun selectYear(year: Int) {
        _currentYear.value = year
        selectDate(year, _currentMonth.value, 1)
    }

    fun selectMonth(month: Int) {
        _currentMonth.value = month
        selectDate(_currentYear.value, month, 1)
    }

    fun nextMonth() {
        var month = _currentMonth.value + 1
        var year = _currentYear.value
        if (month > 11) {
            month = 0
            year += 1
        }
        _currentMonth.value = month
        _currentYear.value = year
        selectDate(year, month, 1)
    }

    fun prevMonth() {
        var month = _currentMonth.value - 1
        var year = _currentYear.value
        if (month < 0) {
            month = 11
            year -= 1
        }
        _currentMonth.value = month
        _currentYear.value = year
        selectDate(year, month, 1)
    }

    fun selectPoster(poster: PosterTemplate?) {
        _selectedPoster.value = poster
        _customSloganText.value = null // Reset custom slogan when template changes
        
        if (poster != null) {
            logApiCall(
                method = "GET",
                endpoint = "/api/posters/details?id=${poster.id}",
                status = "200 OK (Latency: 50ms)",
                payload = """
                {
                  "poster_id": "${poster.id}",
                  "title": "${poster.title}",
                  "category": "${poster.category}",
                  "slogans_available": ${poster.slogans.size},
                  "hd_resolution": "1080x1080"
                }
                """.trimIndent()
            )
        }
    }

    fun selectLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        
        logApiCall(
            method = "GET",
            endpoint = "/api/posters/category?name=${category.lowercase()}",
            status = "200 OK (Latency: 45ms)",
            payload = """
            {
              "category_selected": "$category",
              "status": "success",
              "cached_items_count": ${PosterCatalog.templates.filter { it.category.equals(category, ignoreCase = true) || category == "All" }.size}
            }
            """.trimIndent()
        )
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            val results = PosterCatalog.searchPosters(query)
            logApiCall(
                method = "GET",
                endpoint = "/api/posters/search?q=${query.lowercase()}",
                status = "200 OK (Latency: 110ms)",
                payload = """
                {
                  "query_parameter": "$query",
                  "response_status": "success",
                  "hits_count": ${results.size}
                }
                """.trimIndent()
            )
        }
    }

    // Toggle overlay visibility
    fun toggleBrandName() { _showBrandName.value = !_showBrandName.value }
    fun toggleMobileNumber() { _showMobileNumber.value = !_showMobileNumber.value }
    fun toggleAddress() { _showAddress.value = !_showAddress.value }
    fun toggleSocialHandle() { _showSocialHandle.value = !_showSocialHandle.value }
    fun toggleLogo() { _showLogo.value = !_showLogo.value }
    fun toggleQrCode() { _showQrCode.value = !_showQrCode.value }

    fun setCustomSlogan(text: String) {
        _customSloganText.value = text
    }

    fun setCustomColor(hex: String) {
        _customColorHex.value = hex
    }

    // Brand Profile Persistence
    fun updateBrandProfile(profile: BrandProfile) {
        viewModelScope.launch {
            repository.saveBrandProfile(profile)
        }
    }

    // Favorites
    fun toggleFavorite(poster: PosterTemplate) {
        viewModelScope.launch {
            val isFav = repository.isPosterFavorite(poster.id).first()
            if (isFav) {
                repository.removeFavorite(poster.id)
            } else {
                repository.addFavorite(
                    FavoritePoster(
                        id = poster.id,
                        title = poster.title,
                        category = poster.category,
                        dateStr = poster.dateStr,
                        drawableResName = poster.drawableResName
                    )
                )
            }
        }
    }

    fun isPosterFav(id: String): Flow<Boolean> {
        return repository.isPosterFavorite(id)
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }
}

class PosterViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PosterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PosterViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
