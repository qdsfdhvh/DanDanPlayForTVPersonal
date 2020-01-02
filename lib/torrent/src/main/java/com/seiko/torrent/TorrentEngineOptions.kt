package com.seiko.torrent

import org.libtorrent4j.SettingsPack
import org.libtorrent4j.swig.settings_pack
import java.io.File


data class TorrentEngineOptions(
    /**
     * The root directory to download the torrent into.
     */
    val downloadDir: File,

    val cachedSize: Int = 256,

    val activeDownloads: Int = 4,
    val activeSeeds: Int = 4,
    val activeLimit: Int = 6,
    val maxPeerListSize: Int = 200,

    val tickInterval: Int = 1000,
    val inactivityTimeout: Int = 60,

    val connectionsLimit: Int = 200,

    val port: Int = 6881,

    val enableDht: Boolean = true,
    val enableLsd: Boolean = true,
    val enableUtp: Boolean = true,
    val enableUpnp: Boolean = true,
    val enableNatPmp: Boolean = true,
    val encryptMode: Int = settings_pack.enc_policy.pe_enabled.swigValue(),

    /**
     * 下载/上传 限速
     */
    val downloadRateLimit: Int = 0,
    val uploadRateLimit: Int = 0,


    var connectionsLimitPerTorrent: Int = 40,
    var uploadsLimitPerTorrent: Int = 4,
    var autoManaged: Boolean = false
) {
    val settingsPack: SettingsPack = createSettingPack()
        .cacheSize(cachedSize)

        .activeDownloads(activeDownloads)
        .activeSeeds(activeSeeds)
        .activeLimit(activeLimit)
        .maxPeerlistSize(maxPeerListSize)

        .tickInterval(tickInterval)
        .inactivityTimeout(inactivityTimeout)

        .connectionsLimit(connectionsLimit)

        .setString(settings_pack.string_types.listen_interfaces.swigValue(), "0.0.0.0:$port")

        .enableDht(enableDht)
        .broadcastLSD(enableLsd)
        .setBoolean(settings_pack.bool_types.enable_incoming_utp.swigValue(), enableUtp)
        .setBoolean(settings_pack.bool_types.enable_outgoing_utp.swigValue(), enableUtp)
        .setBoolean(settings_pack.bool_types.enable_upnp.swigValue(), enableUpnp)
        .setBoolean(settings_pack.bool_types.enable_natpmp.swigValue(), enableNatPmp)
        .setInteger(settings_pack.int_types.in_enc_policy.swigValue(), encryptMode)
        .setInteger(settings_pack.int_types.out_enc_policy.swigValue(), encryptMode)

        .downloadRateLimit(downloadRateLimit)
        .uploadRateLimit(uploadRateLimit)

    init {
        settingsPack.setString(settings_pack.string_types.dht_bootstrap_nodes.swigValue(),
            getDhtBootstrapNodeString())
    }

    /**
     * Default list of DHT nodes.
     */
    private fun getDhtBootstrapNodeString(): String {
        return "router.bittorrent.com:6681" +
                ",dht.transmissionbt.com:6881" +
                ",dht.libtorrent.org:25401" +
                ",dht.aelitis.com:6881" +
                ",router.bitcomet.com:6881" +
                ",router.bitcomet.com:6881" +
                ",dht.transmissionbt.com:6881" +
                ",router.silotis.us:6881" // IPv6
    }
}

private fun createSettingPack(): SettingsPack {
    val setting = SettingsPack()
    val maxQueuedDiskBytes = setting.maxQueuedDiskBytes()
    setting.maxQueuedDiskBytes(maxQueuedDiskBytes / 2)
    val sendBufferWatermark = setting.sendBufferWatermark()
    setting.sendBufferWatermark(sendBufferWatermark / 2)
    setting.seedingOutgoingConnections(false)
    return setting
}