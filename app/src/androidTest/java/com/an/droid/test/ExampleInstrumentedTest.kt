package com.an.droid.test

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.an.droid.test.view.MainActivity
import org.hamcrest.Description
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.an.droid.test.view.MainFragment
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val testRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        val idlingExecutor = IdlingThreadPoolExecutor(
            "httpClient",
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            0L,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue<Runnable>(),
            Executors.defaultThreadFactory()
        )
        IdlingRegistry.getInstance().register(idlingExecutor)
        testRule.launchActivity(null)
        testRule.activity.supportFragmentManager.beginTransaction()

    }

    @Test
    fun view_shoulbe_visible() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.an.droid.test", appContext.packageName)

        assertEquals(true, isKeyboardShown())
        onView(withId(R.id.search)).check(matches((isDisplayed())))
        onView(withId(R.id.search)).perform(click())
        onView(withId(R.id.search)).perform(submitText("tesla"))
        onView(withId(R.id.recycler)).check(matches((withEffectiveVisibility(Visibility.VISIBLE))))
        assertEquals(false, isKeyboardShown())

    }
}

fun isKeyboardShown(): Boolean {
    val inputMethodManager = InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(
        Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return inputMethodManager.isAcceptingText
}

fun submitText(text: String) : ViewAction = SearchViewActionExtension.submitText(text)

class SearchViewActionExtension {
    companion object {
        fun submitText(text: String): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))
                }

                override fun getDescription(): String {
                    return "Set text and submit"
                }

                override fun perform(uiController: UiController, view: View) {
                    (view as SearchView).setQuery(text, true)
                }
            }
        }
    }
}
