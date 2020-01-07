package com.seiko.torrent.model

import org.libtorrent4j.PeerInfo
import org.libtorrent4j.PieceIndexBitfield
import org.libtorrent4j.swig.peer_info

class AdvancedPeerInfo(p: peer_info): PeerInfo(p) {
    var port: Int = p.ip.port()
    var pieces: PieceIndexBitfield = PieceIndexBitfield(p.pieces)
    var isUtp: Boolean = p.flags.and_(peer_info.utp_socket).nonZero()
}