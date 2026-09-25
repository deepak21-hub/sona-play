package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TrackCatalog
import com.example.model.AudioEffectSettings
import com.example.model.NavigationTab
import com.example.model.Track
import com.example.service.AudioEngine
import com.example.spotify.SpotifyRemoteManager
import com.example.spotify.SpotifyTrackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val audioEngine = AudioEngine(application.applicationContext)
    val spotifyRemoteManager = SpotifyRemoteManager()

    private val _tracks = MutableStateFlow<List<Track>>(TrackCatalog.sampleTracks)
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track>(TrackCatalog.sampleTracks[0])
    val currentTrack: StateFlow<Track> = _currentTrack.asStateFlow()
    val audioQuality = audioEngine.audioQuality

    private val _activeTab = MutableStateFlow(NavigationTab.BROWSE)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _audioEffects = MutableStateFlow(AudioEffectSettings())
    val audioEffects: StateFlow<AudioEffectSettings> = _audioEffects.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _isRepeat = MutableStateFlow(false)
    val isRepeat: StateFlow<Boolean> = _isRepeat.asStateFlow()

    private val _masterVolume = MutableStateFlow(0.85f)
    val masterVolume: StateFlow<Float> = _masterVolume.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All (すべて)")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val spotifyState: StateFlow<SpotifyTrackState?> = spotifyRemoteManager.playerState
    val spotifyConnectionStatus = spotifyRemoteManager.connectionStatus
    val spotifyErrorMessage = spotifyRemoteManager.errorMessage

    val filteredTracks: StateFlow<List<Track>> = combine(
        _tracks,
        _searchQuery,
        _selectedCategory
    ) { trackList, query, category ->
        var list = trackList
        if (category == "Favorites (お気に入り)") {
            list = list.filter { it.isFavorite }
        } else if (category == "Hi-Res Lossless (ハイレゾ)") {
            list = list.filter { it.format.isLossless }
        } else if (category == "Anime OST (アニメOST)") {
            list = list.filter { it.album.contains("PIECE", ignoreCase = true) || it.album.contains("Bebop", ignoreCase = true) }
        } else if (category == "City Pop (シティポップ)") {
            list = list.filter { !it.album.contains("PIECE", ignoreCase = true) && !it.album.contains("Bebop", ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.titleJp.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.artistJp.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrackCatalog.sampleTracks)

    init {
        audioEngine.setVolume(_masterVolume.value)
    }

    fun selectTrack(track: Track, autoPlay: Boolean = true) {
        _currentTrack.value = track
        if (autoPlay) {
            audioEngine.playTrack(track, _audioEffects.value)
        }
    }

    fun togglePlayPause() {
        if (_activeTab.value == NavigationTab.SPOTIFY && spotifyState.value != null) {
            spotifyRemoteManager.togglePlayPause()
        } else {
            audioEngine.togglePlayPause()
        }
    }

    fun nextTrack() {
        if (_activeTab.value == NavigationTab.SPOTIFY && spotifyState.value != null) {
            spotifyRemoteManager.skipNext()
            return
        }

        val currentList = _tracks.value
        if (currentList.isEmpty()) return

        if (_isShuffle.value) {
            val randomTrack = currentList.random()
            selectTrack(randomTrack, autoPlay = true)
            return
        }

        val currentIndex = currentList.indexOfFirst { it.id == _currentTrack.value.id }
        val nextIndex = if (currentIndex != -1 && currentIndex < currentList.size - 1) {
            currentIndex + 1
        } else {
            0
        }
        selectTrack(currentList[nextIndex], autoPlay = true)
    }

    fun previousTrack() {
        if (_activeTab.value == NavigationTab.SPOTIFY && spotifyState.value != null) {
            spotifyRemoteManager.skipPrevious()
            return
        }

        val currentList = _tracks.value
        if (currentList.isEmpty()) return

        val currentIndex = currentList.indexOfFirst { it.id == _currentTrack.value.id }
        val prevIndex = if (currentIndex > 0) {
            currentIndex - 1
        } else {
            currentList.size - 1
        }
        selectTrack(currentList[prevIndex], autoPlay = true)
    }

    fun seekTo(positionMs: Long) {
        if (_activeTab.value == NavigationTab.SPOTIFY && spotifyState.value != null) {
            spotifyRemoteManager.seekTo(positionMs)
        } else {
            audioEngine.seekTo(positionMs)
        }
    }

    fun seekRelative(offsetMs: Long) {
        audioEngine.seekRelative(offsetMs)
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun toggleRepeat() {
        _isRepeat.value = !_isRepeat.value
    }

    fun setMasterVolume(vol: Float) {
        _masterVolume.value = vol.coerceIn(0f, 1f)
        audioEngine.setVolume(_masterVolume.value)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun toggleFavorite(trackId: String) {
        _tracks.value = _tracks.value.map { track ->
            if (track.id == trackId) {
                track.copy(isFavorite = !track.isFavorite)
            } else {
                track
            }
        }
        if (_currentTrack.value.id == trackId) {
            _currentTrack.value = _currentTrack.value.copy(isFavorite = !_currentTrack.value.isFavorite)
        }
    }

    fun updateAudioEffects(transform: (AudioEffectSettings) -> AudioEffectSettings) {
        val updated = transform(_audioEffects.value)
        _audioEffects.value = updated
        audioEngine.applyEffects(updated)
    }

    fun updateAudioEffectsDirect(newEffects: AudioEffectSettings) {
        _audioEffects.value = newEffects
        audioEngine.applyEffects(newEffects)
    }

    fun toggle45RpmMode() {
        updateAudioEffects { it.copy(is45RpmMode = !it.is45RpmMode) }
    }

    override fun onCleared() {
        super.onCleared()
        spotifyRemoteManager.disconnect()
    }
}
