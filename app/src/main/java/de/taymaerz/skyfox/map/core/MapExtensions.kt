package de.taymaerz.skyfox.map.core

import de.taymaerz.skyfox.feeder.core.ReceiverId

fun ReceiverId.toMapFeedId() = split("-").take(3).joinToString("")