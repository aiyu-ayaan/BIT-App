package com.atech.bit.ui.screens.about_us.screen.credit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.atech.bit.R
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_2
import com.atech.core.utils.openLinks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        topBar = {
            BackToolbar(
                title = "Credits",
                onNavigationClick = {
                    navController.popBackStack()
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(it),
            contentPadding = it
        ) {
            singleElement("Dev Details") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(grid_2)
                        .clip(MaterialTheme.shapes.large)
                        .clickable { }
                        .clearAndSetSemantics { },
                    color = MaterialTheme.colorScheme.dividerOrCardColor.copy(
                        alpha = 0.2f
                    )
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .aspectRatio(1.38f),
                        model = R.drawable.ic_credits,
                        contentDescription = null
                    )
                }
            }
            items(credits, key = { it2 -> it2.title }) { cr ->
                CreditItem(
                    title = cr.title,
                    license = cr.licenses.joinToString { it1 -> it1.title },
                    onClick = {
                        cr.url.openLinks(
                            context
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun CreditItem(
    title: String,
    license: String? = null,
    onClick: () -> Unit = {},
) {
    Surface(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                with(MaterialTheme) {
                    Text(
                        text = title,
                        maxLines = 1,
                        style = typography.titleMedium,
                        color = colorScheme.onSurface
                    )
                    license?.let {
                        Text(
                            text = it,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 2, overflow = TextOverflow.Ellipsis,
                            style = typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }

}

enum class Licenses(val title: String) {
    APACHE_2_0("Apache 2.0"), MIT(
        "MIT"
    )
}

data class Credit(
    val title: String, val url: String, val licenses: List<Licenses> = listOf()
)

val credits = listOf(
    Credit(
        "Splash-Screen",
        "https://developer.android.com/develop/ui/views/launch/splash-screen",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Android Jetpack",
        "https://github.com/androidx/androidx",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Cryptore", "https://github.com/KazaKago/Cryptore", listOf(Licenses.MIT)
    ),
    Credit(
        "Core-Ktx",
        "https://developer.android.com/jetpack/androidx/releases/core",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Exo-Player", "https://github.com/google/ExoPlayer", listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Coil", "https://github.com/coil-kt/coil", listOf(
            Licenses.APACHE_2_0
        )
    ),
    Credit(
        "Hilt", "https://developer.android.com/training/dependency-injection/hilt-android",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "App Compat", "https://developer.android.com/jetpack/androidx/releases/appcompat",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Material",
        "https://developer.android.com/jetpack/androidx/releases/material",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Navigation",
        "https://developer.android.com/jetpack/androidx/releases/navigation",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Compact-Calendar-View",
        "https://github.com/SundeepK/CompactCalendarView",
        listOf(Licenses.MIT)
    ),
    Credit(
        "Data-Store",
        "https://developer.android.com/topic/libraries/architecture/datastore",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Firebase",
        "https://firebase.google.com/",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Mp-Android-Chart",
        "https://github.com/PhilJay/MPAndroidChart",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Gson",
        "https://github.com/google/gson",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "MarkDown-View",
        "https://github.com/mukeshsolanki/MarkdownView-Android",
        listOf(Licenses.MIT)
    ),
    Credit(
        "Room",
        "https://developer.android.com/jetpack/androidx/releases/room",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Retofit",
        "https://github.com/square/retrofit",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Lottie",
        "https://github.com/airbnb/lottie-android",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Browser",
        "https://developer.android.com/jetpack/androidx/releases/browser",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Play-Services",
        "https://developers.google.com/android/guides/setup",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Paging 3",
        "https://developer.android.com/topic/libraries/architecture/paging/v3-overview",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "RichText-UI-Material3",
        "https://github.com/halilozercan/compose-richtext",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "RichText-UI-CommonMark",
        "https://github.com/halilozercan/compose-richtext",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Generative-AI-Android",
        "https://ai.google.dev/",
        listOf(Licenses.APACHE_2_0)
    )
).sortedBy {
    it.title
}


@Preview(showBackground = true)
@Composable
private fun DevDetailsScreenPreview() {
    BITAppTheme {
        CreditsScreen()
    }
}