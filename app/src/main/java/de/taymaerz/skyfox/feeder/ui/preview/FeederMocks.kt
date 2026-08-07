package de.taymaerz.skyfox.feeder.ui.preview

import de.taymaerz.skyfox.feeder.core.Feeder
import de.taymaerz.skyfox.feeder.core.ReceiverId
import de.taymaerz.skyfox.feeder.core.config.FeederConfig

fun mockFeeder(
    label: String = "Home Feeder",
    id: ReceiverId = "abc12",
) = Feeder(config = FeederConfig(receiverId = id, label = label))
