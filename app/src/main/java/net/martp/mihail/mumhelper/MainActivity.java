package net.martp.mihail.mumhelper;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    final static public String SAVED_STUDENT_ID = "studentID";
    final static public String SAVED_NAME_STUDENT = "nameStudent";
    final static public String SAVED_SURNAME_STUDENT = "surnameStudent";
    final static public String SAVED_MIDNAME_STUDENT = "midnameStudent";
    final static public String SAVED_FACULTY = "faculty";
    final static public String SAVED_SPECIALTY = "specialty";
    final static public String SAVED_AVARAGE_SCORE = "avscoreStudent";
    final static public String SAVED_NUMBER_GROUP = "numberGroup";
    final static public String SAVED_PHOTO = "no";
    final static public String CACHE_DATA = "no";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        NewsFragment newsFragment = new NewsFragment();
        AboutFragment aboutFragment = new AboutFragment();
        MarksFragment marksFragment = new MarksFragment();
        RatingNFragment ratingNFragment = new RatingNFragment();
        SetupFragment setupFragment = new SetupFragment();
        OrdersFragment ordersFragment = new OrdersFragment();
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        InfoFragment infoFragment = new InfoFragment();
        FirstStartFragment firstStartFragment = new FirstStartFragment();

        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        int savedStudentIDvalue;

        String idValue = sPref.getString(SAVED_STUDENT_ID, "");
      //  if (idValue != null) savedStudentIDvalue = idValue.length();
       savedStudentIDvalue = idValue.length();
       // else savedStudentIDvalue = 0;

        switch (number) {
            case 1:
                if (savedStudentIDvalue < 14) {
                    mTitle = getString(R.string.title_setup);
                    fTrans.replace(R.id.frgmCont, firstStartFragment);
                    fTrans.commit();
                } else {
                    mTitle = getString(R.string.title_info);
                    fTrans.replace(R.id.frgmCont, infoFragment);
                    fTrans.commit();
                }
                break;
            case 2:
                if (savedStudentIDvalue < 14) {
                    mTitle = getString(R.string.title_setup);
                    fTrans.replace(R.id.frgmCont, firstStartFragment);
                    fTrans.commit();
                } else {
                    mTitle = getString(R.string.title_marks);
                    fTrans.replace(R.id.frgmCont, marksFragment);
                    fTrans.commit();
                }
                break;
            case 3:
                if (savedStudentIDvalue < 14) {
                    mTitle = getString(R.string.title_setup);
                    fTrans.replace(R.id.frgmCont, firstStartFragment);
                    fTrans.commit();
                } else {
                    mTitle = getString(R.string.title_order);

                    fTrans.replace(R.id.frgmCont, ordersFragment);
                    fTrans.commit();
                }
                break;
            case 4:
                if (savedStudentIDvalue < 14) {
                    mTitle = getString(R.string.title_setup);
                    fTrans.replace(R.id.frgmCont, firstStartFragment);
                    fTrans.commit();
                } else {
                    mTitle = getString(R.string.title_rating_group);
                    fTrans.replace(R.id.frgmCont, ratingNFragment);
                    fTrans.commit();
                }
                break;
            case 5:
                mTitle = getString(R.string.title_news);
                fTrans.replace(R.id.frgmCont, newsFragment);
                fTrans.commit();
                break;
            case 6:
                mTitle = getString(R.string.title_schedule);
                fTrans.replace(R.id.frgmCont, scheduleFragment);
                fTrans.commit();
                break;
            case 7:
                mTitle = getString(R.string.title_setup);
                fTrans.replace(R.id.frgmCont, setupFragment);
                fTrans.commit();
                break;
            case 8:
                mTitle = getString(R.string.title_about_ap);
                fTrans = getFragmentManager().beginTransaction();
                fTrans.replace(R.id.frgmCont, aboutFragment);
                fTrans.commit();
                break;
            default:
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //11111

            // Toast.makeText(this, "View setup", Toast.LENGTH_SHORT).show();
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            SetupFragment setupFragment = new SetupFragment();
            fTrans.replace(R.id.frgmCont, setupFragment);
            fTrans.commit();
            return true;
        }

        if (id == R.id.action_exit) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //       View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
