package com.tevinjeffrey.njitct.ui.courseinfo;

import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.model.CourseModel;
import com.tevinjeffrey.njitct.model.Metadata;
import com.tevinjeffrey.njitct.model.SectionModel;
import com.tevinjeffrey.njitct.model.SubjectModel;
import com.tevinjeffrey.njitct.ui.base.MVPFragment;
import com.tevinjeffrey.njitct.ui.course.CourseView;
import com.tevinjeffrey.njitct.ui.sectioninfo.SectionInfoFragment;
import com.tevinjeffrey.njitct.ui.utils.CircleSharedElementCallback;
import com.tevinjeffrey.njitct.ui.utils.CircleView;
import com.tevinjeffrey.njitct.ui.utils.ItemClickListener;
import com.tevinjeffrey.njitct.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icicle;
import timber.log.Timber;

@SuppressWarnings("ClassWithTooManyMethods")
public class CourseInfoFragment extends MVPFragment implements CourseInfoView, ItemClickListener<SectionModel, View> {

    private static final String TAG = CourseInfoFragment.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.course_info_list)
    RecyclerView mRecyclerView;

    @Bind(R.id.course_title_text)
    TextView mCourseTitleText;

    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.shortenedCourseInfo)
    TextView mShortenedCourseInfo;

    @Bind(R.id.openSections_text)
    TextView mOpenSectionsText;

    @Bind(R.id.totalSections_text)
    TextView mTotalSectionsText;

    @Icicle
    CourseModel mSelectedCourse;

    @Icicle
    CourseInfoViewState mViewState = new CourseInfoViewState();

    private List<View> mHeaderViews = new ArrayList<>();

    public CourseInfoFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mSelectedCourse = getArguments().getParcelable(CourseView.SELECTED_COURSE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater themedInflator = inflater.cloneInContext(Utils.wrapContextTheme(getActivity(),
                R.style.NJITCT));
        final View rootView = themedInflator.inflate(R.layout.fragment_course_info, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewState.apply(this, savedInstanceState != null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_info, menu);
    }


    @Override
    public void onItemClicked(SectionModel section, View view) {
        Timber.i("Selected section: %s", section);
        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTED_SECTION, section);
        startSectionInfoFragment(bundle, view);
    }

    public void initViews() {
        setCourseTitle();
        setShortenedCourseInfo();
        setOpenSections();
        setTotalSections();
    }

    public void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getParentActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(new CourseInfoFragmentAdapter(mHeaderViews,
                    mSelectedCourse.getSections(), this));
        }
    }

    public void initHeaderView() {
        View courseMetadata = createCourseMetaDataView();
        mHeaderViews.add(courseMetadata);
    }

    private void setCourseTitle() {
        mCourseTitleText.setText(mSelectedCourse.getName());
    }

    private void setShortenedCourseInfo() {
        //String offeringUnitCode = mSelectedCourse.getOfferingUnitCode();

        SubjectModel subject = mSelectedCourse.getSubject();
        String courseNumber = mSelectedCourse.getNumber();
        if (subject != null) {
            String shortenedCourseInfo = subject.getName() + " › " + courseNumber;
            mShortenedCourseInfo.setText(shortenedCourseInfo);
        }
    }


    private void setOpenSections() {
        mOpenSectionsText.setText(String.valueOf(mSelectedCourse.getOpenSections()));
    }

    private void setTotalSections() {
        mTotalSectionsText.setText(String.valueOf(mSelectedCourse.getTotalSections()));
    }

    private View createCourseMetaDataView() {
        ViewGroup root = (ViewGroup) getParentActivity().getLayoutInflater().inflate(R.layout.course_info_metadata, null);
        for (Metadata data: mSelectedCourse.getCourseMetadata()) {
            ViewGroup metadata = (ViewGroup) LayoutInflater.from(getParentActivity()).inflate(R.layout.metadata, null);
            TextView title = ButterKnife.findById(metadata, R.id.metadata_title);
            TextView description = ButterKnife.findById(metadata, R.id.metadata_text);
            description.setMovementMethod(new LinkMovementMethod());
            title.setText(data.getTitle());
            description.setText(data.getData());
            root.addView(metadata);
        }
        return root;
    }

    private void startSectionInfoFragment(Bundle b, View clickedView) {
        SectionInfoFragment sectionInfoFragment = new SectionInfoFragment();

        FragmentTransaction ft =
                this.getFragmentManager().beginTransaction();

        CircleView circleView = ButterKnife.findById(clickedView, R.id.section_number_background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            circleView.setTransitionName(getString(R.string.transition_name_circle_view));
            ft.addSharedElement(circleView, getString(R.string.transition_name_circle_view));

            mAppBarLayout.setTransitionName(null);

            Transition cifSectionEnter = TransitionInflater
                    .from(getParentActivity())
                    .inflateTransition(R.transition.cif_section_enter);

            Transition cifSectionReturn = TransitionInflater
                    .from(getParentActivity())
                    .inflateTransition(R.transition.cif_section_return);

            sectionInfoFragment.setEnterTransition(cifSectionEnter);
            sectionInfoFragment.setReturnTransition(cifSectionReturn);


            setReenterTransition(new Fade(Fade.IN).setDuration(200));

            Transition cifExit = TransitionInflater
                    .from(getParentActivity())
                    .inflateTransition(R.transition.cif_exit);

            setExitTransition(cifExit);

            sectionInfoFragment.setAllowReturnTransitionOverlap(false);
            sectionInfoFragment.setAllowEnterTransitionOverlap(false);


            Transition sharedElementsEnter = TransitionInflater
                    .from(getParentActivity()).inflateTransition(R.transition.cif_shared_element_enter);

            Transition sharedElementsReturn = TransitionInflater
                    .from(getParentActivity()).inflateTransition(R.transition.cif_shared_element_return);

            sectionInfoFragment.setSharedElementEnterTransition(sharedElementsEnter);
            sectionInfoFragment.setSharedElementReturnTransition(sharedElementsReturn);

            CircleSharedElementCallback sharedelementCallback = new CircleSharedElementCallback();
            sectionInfoFragment.setEnterSharedElementCallback(sharedelementCallback);
            sharedElementsEnter.addListener(sharedelementCallback.getTransitionCallback());


        } else {
            ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        }

        sectionInfoFragment.setArguments(b);
        startFragment(this, sectionInfoFragment, ft);
    }

    @Override
    public String toString() {
        return TAG;
    }

    @Override
    public void initToolbar() {
        setToolbar(mToolbar);
        getParentActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}