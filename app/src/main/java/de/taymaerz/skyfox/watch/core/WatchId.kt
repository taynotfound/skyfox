package de.taymaerz.skyfox.watch.core

import java.util.UUID

typealias WatchId = String


fun makeWatchId() = UUID.randomUUID().toString()