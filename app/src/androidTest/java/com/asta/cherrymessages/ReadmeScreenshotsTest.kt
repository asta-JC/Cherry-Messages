package com.asta.cherrymessages

import android.graphics.Bitmap
import android.view.WindowInsets
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.asta.cherrymessages.data.local.entity.ConversationEntity
import com.asta.cherrymessages.ui.conversations.ConversationListScreen
import com.asta.cherrymessages.ui.newmessage.NewMessageScreen
import com.asta.cherrymessages.ui.newmessage.NewMessageViewModel
import com.asta.cherrymessages.ui.settings.SettingsScreen
import com.asta.cherrymessages.ui.theme.CherryMessagesTheme
import kotlinx.coroutines.runBlocking
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/** Runs only in -PreadmeScreenshots, never against the user's messaging database. */
@RunWith(AndroidJUnit4::class)
class ReadmeScreenshotsTest {
    @Test fun captureProductionScreensWithDemoData() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        assumeTrue(context.packageName.endsWith(".screenshots"))
        val database = androidx.room.Room.databaseBuilder(context, com.asta.cherrymessages.data.local.AppDatabase::class.java, com.asta.cherrymessages.data.local.AppDatabase.DATABASE_NAME).build()
        val dao = database.conversationDao()
        val now = System.currentTimeMillis()
        runBlocking {
            listOf(
                "Alex Morgan" to "See you at the café at 6!",
                "Jamie Chen" to "Thanks! The photos look great.",
                "Casey Parker" to "Saturday works for me. See you then!",
                "Sam Rivera" to "I’m on my way. Be there in ten minutes.",
                "Taylor Reed" to "That sounds good. Talk soon!"
            ).forEachIndexed { index, (name, body) ->
                dao.upsertConversation(ConversationEntity(
                    threadId = 9000L + index, recipientAddress = "+120255501${23 + index}", recipientName = name,
                    snippet = body, date = now - index * 3_600_000L, unreadCount = if (index == 0) 2 else 0,
                    isPinned = index == 0
                ))
            }
        }
        database.close()
        val output = File(context.getExternalFilesDir(null), "readme").apply { mkdirs() }
        val prefs = context.getSharedPreferences("appearance", android.content.Context.MODE_PRIVATE)
        val hadAppearance = prefs.contains("theme")
        val originalAppearance = prefs.getString("theme", "system")
        prefs.edit().putString("theme", "light").commit()
        try {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            val screen = mutableIntStateOf(0)
            scenario.onActivity { activity ->
                activity.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                activity.setContent {
                    CherryMessagesTheme(darkTheme = false) {
                        when (screen.intValue) {
                            0 -> ConversationListScreen(onChatClick = { _, _, _ -> }, onNewMessageClick = {}, onSettingsClick = {})
                            1 -> {
                                val vm: NewMessageViewModel = hiltViewModel()
                                LaunchedEffect(Unit) {
                                    vm.selectContact("Alex Morgan", "+1 202 555 0123")
                                    vm.updateMessageBody("Hi Alex! I’ll meet you at the café at 6. See you soon!")
                                }
                                NewMessageScreen(viewModel = vm, onBackClick = {}, onChatOpened = { _, _, _ -> })
                            }
                            else -> SettingsScreen(onBackClick = {})
                        }
                    }
                }
            }
            listOf("inbox-light.png", "compose-light.png", "settings-light.png").forEachIndexed { index, name ->
                scenario.onActivity { screen.intValue = index }
                instrumentation.waitForIdleSync()
                Thread.sleep(1200) // Let real database/Compose state settle before capture.
                var top = 0
                var bottom = 0
                scenario.onActivity { activity ->
                    val insets = activity.window.decorView.rootWindowInsets
                    if (android.os.Build.VERSION.SDK_INT >= 30) {
                        top = insets.getInsets(WindowInsets.Type.statusBars()).top
                        bottom = insets.getInsets(WindowInsets.Type.navigationBars()).bottom
                    } else {
                        @Suppress("DEPRECATION")
                        top = insets.systemWindowInsetTop
                        @Suppress("DEPRECATION")
                        bottom = insets.systemWindowInsetBottom
                    }
                }
                val screenshot = instrumentation.uiAutomation.takeScreenshot()
                org.junit.Assert.assertTrue("Display must be awake; refuse blank screenshots", (screenshot.getPixel(screenshot.width / 2, screenshot.height / 2) and 0x00ffffff) != 0)
                val content = Bitmap.createBitmap(screenshot, 0, top, screenshot.width, screenshot.height - top - bottom)
                File(output, name).outputStream().use { content.compress(Bitmap.CompressFormat.PNG, 100, it) }
                content.recycle()
                screenshot.recycle()
            }
        }
        } finally {
            val editor = prefs.edit()
            if (hadAppearance) editor.putString("theme", originalAppearance) else editor.remove("theme")
            editor.commit()
        }
    }
}
