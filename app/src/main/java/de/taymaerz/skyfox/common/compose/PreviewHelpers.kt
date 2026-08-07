package de.taymaerz.skyfox.common.compose

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.taymaerz.skyfox.common.theming.AplTheme
import de.taymaerz.skyfox.main.core.ThemeState

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Preview(showBackground = true, name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class Preview2

@Composable
fun PreviewWrapper(content: @Composable () -> Unit) {
    AplTheme(state = ThemeState()) {
        Surface {
            content()
        }
    }
}
