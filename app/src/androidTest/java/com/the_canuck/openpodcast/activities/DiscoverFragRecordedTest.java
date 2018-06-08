package com.the_canuck.openpodcast.activities;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.the_canuck.openpodcast.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DiscoverFragRecordedTest {

  @Rule
  public ActivityTestRule<MainActivity> mActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test
  public void discoverFragRecordedTest() {
    ViewInteraction appCompatImageButton =
        onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(withId(R.id.toolbar), childAtPosition(withId(R.id.content_frame), 0)), 1),
                isDisplayed()));
    appCompatImageButton.perform(click());

    ViewInteraction navigationMenuItemView =
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(withId(R.id.nav_view), 0)),
                    2),
                isDisplayed()));
    navigationMenuItemView.perform(click());

    ViewInteraction constraintLayout =
        onView(
            allOf(
                withId(R.id.card_view_constraint),
                childAtPosition(
                    allOf(
                        withId(R.id.card_view),
                        childAtPosition(withId(R.id.discover_business_recycler), 0)),
                    0),
                isDisplayed()));
    constraintLayout.perform(click());

    pressBack();

    ViewInteraction appCompatButton =
        onView(
            allOf(
                withId(R.id.business_button),
                withText("More"),
                childAtPosition(childAtPosition(withId(R.id.discover_business_card), 0), 2)));
    appCompatButton.perform(scrollTo(), click());

    ViewInteraction recyclerView =
        onView(allOf(withId(R.id.recycler_view), childAtPosition(withId(R.id.list), 1)));
    recyclerView.perform(actionOnItemAtPosition(1, click()));

    pressBack();

    pressBack();

    pressBack();

    ViewInteraction appCompatImageButton2 =
        onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(withId(R.id.toolbar), childAtPosition(withId(R.id.content_frame), 0)), 1),
                isDisplayed()));
    appCompatImageButton2.perform(click());

    ViewInteraction navigationMenuItemView2 =
        onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(withId(R.id.nav_view), 0)),
                    2),
                isDisplayed()));
    navigationMenuItemView2.perform(click());

    ViewInteraction constraintLayout2 =
        onView(
            allOf(
                withId(R.id.card_view_constraint),
                childAtPosition(
                    allOf(
                        withId(R.id.card_view),
                        childAtPosition(withId(R.id.discover_arts_recycler), 1)),
                    0),
                isDisplayed()));
    constraintLayout2.perform(click());

    ViewInteraction appCompatTextView =
        onView(
            allOf(
                withId(R.id.episode),
                withText("com.the_canuck.openpodcast.Episode: 0"),
                childAtPosition(childAtPosition(withId(R.id.bottom_sheet_recyclerview), 0), 0),
                isDisplayed()));
    appCompatTextView.perform(click());
  }

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup
            && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }
}
