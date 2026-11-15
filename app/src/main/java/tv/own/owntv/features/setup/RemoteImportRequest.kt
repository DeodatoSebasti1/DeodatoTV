package tv.own.owntv.features.setup

import tv.own.owntv.core.companion.CompanionPayload
import tv.own.owntv.core.model.SourceType
import tv.own.owntv.core.settings.PlaylistRefresh
import tv.own.owntv.core.sync.SyncScopeChoice

/**
 * Validated, app-facing form of one authenticated companion submission.
 *
 * The HTTP parser checks the wire payload first. This second boundary is intentionally kept in the
 * app because an uploaded M3U can still fail while being copied to local storage. In that case the
 * request stays on the manual form for correction instead of starting an import with an empty path.
 */
internal sealed interface RemoteImportRequest {
    val name: String
    val userAgent: String
    val autoRefresh: PlaylistRefresh

    data class Xtream(
        override val name: String,
        val server: String,
        val username: String,
        val password: String,
        override val userAgent: String,
        val epgUrl: String,
        override val autoRefresh: PlaylistRefresh,
        val live: SyncScopeChoice,
        val movies: SyncScopeChoice,
        val series: SyncScopeChoice,
        val isDefault: Boolean,
    ) : RemoteImportRequest

    data class M3u(
        override val name: String,
        val url: String,
        override val userAgent: String,
        val epgUrl: String,
        override val autoRefresh: PlaylistRefresh,
        val isDefault: Boolean,
    ) : RemoteImportRequest

    data class Stalker(
        override val name: String,
        val portalUrl: String,
        val mac: String,
        val serialNumber: String,
        val deviceId: String,
        val deviceId2: String,
        val signature: String,
        override val userAgent: String,
        override val autoRefresh: PlaylistRefresh,
        val live: SyncScopeChoice,
        val movies: SyncScopeChoice,
        val series: SyncScopeChoice,
        val isDefault: Boolean,
    ) : RemoteImportRequest
}

internal data class RemoteImportDefaults(
    val xtreamName: String,
    val m3uName: String,
    val stalkerName: String,
)

/** Returns null only when the post-receive payload is no longer safe to import automatically. */
internal fun CompanionPayload.toRemoteImportRequest(defaults: RemoteImportDefaults): RemoteImportRequest? {
    val refresh = PlaylistRefresh.parse(autoRefresh)
    return when (type) {
        SourceType.M3U -> server.trim().takeIf(String::isNotEmpty)?.let { url ->
            RemoteImportRequest.M3u(
                name = name.ifBlank { defaults.m3uName },
                url = url,
                userAgent = userAgent,
                epgUrl = epgUrl,
                autoRefresh = refresh,
                isDefault = isDefault,
            )
        }

        SourceType.STALKER -> {
            val portal = portalUrl.trim()
            val submittedMac = mac.trim()
            if (portal.isEmpty() || submittedMac.isEmpty()) null else RemoteImportRequest.Stalker(
                name = name.ifBlank { defaults.stalkerName },
                portalUrl = portal,
                mac = submittedMac,
                serialNumber = serialNumber,
                deviceId = deviceId,
                deviceId2 = deviceId2,
                signature = signature,
                userAgent = userAgent,
                autoRefresh = refresh,
                live = syncLive,
                movies = syncMovies,
                series = syncSeries,
                isDefault = isDefault,
            )
        }

        SourceType.XTREAM -> {
            val host = server.trim()
            val submittedUser = user.trim()
            if (host.isEmpty() || submittedUser.isEmpty() || pass.isEmpty()) null else RemoteImportRequest.Xtream(
                name = name.ifBlank { defaults.xtreamName },
                server = host,
                username = submittedUser,
                password = pass,
                userAgent = userAgent,
                epgUrl = epgUrl,
                autoRefresh = refresh,
                live = syncLive,
                movies = syncMovies,
                series = syncSeries,
                isDefault = isDefault,
            )
        }

        SourceType.LOCAL_BACKUP -> null
    }
}
