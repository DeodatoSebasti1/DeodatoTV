package tv.own.owntv.features.setup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import tv.own.owntv.core.companion.CompanionPayload
import tv.own.owntv.core.model.SourceType
import tv.own.owntv.core.settings.PlaylistAutoRefresh
import tv.own.owntv.core.sync.SyncScopeChoice

class RemoteImportRequestTest {
    private val defaults = RemoteImportDefaults("IPTV", "Playlist", "Portal")

    @Test
    fun `xtream submission keeps remote choices and supplies default name`() {
        val request = CompanionPayload(
            type = SourceType.XTREAM,
            server = " https://provider.test ",
            user = " user ",
            pass = "secret",
            autoRefresh = "HOURS_6",
            syncLive = SyncScopeChoice.Now,
            syncMovies = SyncScopeChoice.Later,
            syncSeries = SyncScopeChoice.Off,
            isDefault = true,
        ).toRemoteImportRequest(defaults) as RemoteImportRequest.Xtream

        assertEquals("IPTV", request.name)
        assertEquals("https://provider.test", request.server)
        assertEquals("user", request.username)
        assertEquals(PlaylistAutoRefresh.HOURS_6, request.autoRefresh.mode)
        assertEquals(SyncScopeChoice.Later, request.movies)
        assertTrue(request.isDefault)
    }

    @Test
    fun `saved M3U path starts automatically`() {
        val request = CompanionPayload(
            type = SourceType.M3U,
            name = "Cinema",
            server = "/data/user/0/tv.own.owntv/files/companion-playlists/cinema.m3u",
        ).toRemoteImportRequest(defaults) as RemoteImportRequest.M3u

        assertEquals("Cinema", request.name)
        assertTrue(request.url.endsWith("cinema.m3u"))
    }

    @Test
    fun `failed M3U file persistence falls back to manual correction`() {
        assertNull(CompanionPayload(type = SourceType.M3U, server = "").toRemoteImportRequest(defaults))
    }

    @Test
    fun `stalker submission preserves staged scopes`() {
        val request = CompanionPayload(
            type = SourceType.STALKER,
            portalUrl = "https://portal.test/c/",
            mac = "00:1A:79:AA:BB:CC",
            syncMovies = SyncScopeChoice.Later,
            syncSeries = SyncScopeChoice.Off,
        ).toRemoteImportRequest(defaults) as RemoteImportRequest.Stalker

        assertEquals("Portal", request.name)
        assertEquals(SyncScopeChoice.Later, request.movies)
        assertEquals(SyncScopeChoice.Off, request.series)
    }

    @Test
    fun `incomplete credentials never start automatically`() {
        assertNull(
            CompanionPayload(type = SourceType.XTREAM, server = "https://provider.test", user = "u")
                .toRemoteImportRequest(defaults),
        )
        assertNull(
            CompanionPayload(type = SourceType.STALKER, portalUrl = "https://portal.test")
                .toRemoteImportRequest(defaults),
        )
    }
}
