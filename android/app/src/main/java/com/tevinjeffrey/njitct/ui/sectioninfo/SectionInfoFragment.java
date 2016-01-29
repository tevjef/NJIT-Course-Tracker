package com.tevinjeffrey.njitct.ui.sectioninfo;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tevinjeffrey.rmp.common.Professor;
import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.NjitCTApp;
import com.tevinjeffrey.njitct.model.MeetingTimeModel;
import com.tevinjeffrey.njitct.model.Metadata;
import com.tevinjeffrey.njitct.model.SectionModel;
import com.tevinjeffrey.njitct.ui.base.MVPFragment;
import com.tevinjeffrey.njitct.ui.courseinfo.CourseInfoView;
import com.tevinjeffrey.njitct.ui.utils.RatingLayoutInflater;
import com.tevinjeffrey.njitct.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icicle;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class SectionInfoFragment extends MVPFragment implements SectionInfoView {

    private static final String TAG = SectionInfoFragment.class.getSimpleName();

    @Bind(R.id.prof_ratings_container)
    ViewGroup ratingsContainer;

    @Bind(R.id.course_metadata)
    ViewGroup mCourseMetadata;

    @Bind(R.id.prof_ratings_root)
    ViewGroup ratingsRoot;

    @Bind(R.id.statusText)
    TextView mStatusText;

    @Bind(R.id.seats)
    ViewGroup mSeatsContainer;

    @Bind(R.id.seats_text)
    TextView mSeatsText;

    @Bind(R.id.course_title_text)
    TextView mCourseTitleText;

    @Bind(R.id.sectionNumber_text)
    TextView mSectionNumberText;

    @Bind(R.id.indexNumber_text)
    TextView mIndexNumberText;

    @Bind(R.id.subtitle)
    TextView mCreditsText;

    @Bind(R.id.instructors_text)
    TextView mInstructorsText;

    @Bind(R.id.instructors_container)
    ViewGroup mInstructorsContainer;


    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.section_times_container)
    LinearLayout mSectionTimeContainer;


    @Bind(R.id.add_courses_fab)
    FloatingActionButton mFab;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Icicle
    SectionModel selectedSection;

    @Icicle
    SectionInfoViewState mViewState = new SectionInfoViewState();

    public SectionInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            selectedSection = getArguments().getParcelable(CourseInfoView.SELECTED_SECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context contextThemeWrapper;
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        contextThemeWrapper = Utils.wrapContextTheme(getActivity(), R.style.NJITCT);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater themedInflator = inflater.cloneInContext(contextThemeWrapper);

        final View rootView = themedInflator.inflate(R.layout.fragment_section_info, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Recreate presenter if necessary.
        if (mBasePresenter == null) {
            mBasePresenter = new SectionInfoPresenterImpl(selectedSection);
            NjitCTApp.getObjectGraph(getParentActivity()).inject(mBasePresenter);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewState.apply(this, savedInstanceState != null);

        //Attach view to presenter
        mBasePresenter.attachView(this);

        //Load data depending on if the view is currently refreshing
        if (mIsInitialLoad) {
            mViewState.shouldAnimateFabIn = false;
        }

        //Requires a database access and the results should not be saved.
        getPresenter().setFabState(false);
        getPresenter().loadRMP();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_info, menu);
    }

    @OnClick(R.id.add_courses_fab)
    public void fabClick(View view) {
        getPresenter().toggleFab();
    }

    @Override
    public void showSectionTracked(boolean sectionIsAdded, boolean shouldAnimateView) {
        final int COLOR = ContextCompat.getColor(getParentActivity(), R.color.accent);
        final int COLOR_DARK = ContextCompat.getColor(getParentActivity(), R.color.accent_dark);
        final int ROTATION_NORMAL = 0;
        final int ROTATION_ADDED = 225;
        final int DURATION = 500;

            if (shouldAnimateView) {
                if (sectionIsAdded != mViewState.isSectionAdded) {
                    ViewCompat.animate(mFab).setDuration(DURATION).setInterpolator(new DecelerateInterpolator())
                            .rotation(sectionIsAdded ? ROTATION_ADDED : ROTATION_NORMAL);
                    //I would much prefer to animate from the current coolor to the next but the fab has
                    // no method to get the current color and I'm not desparate enough to manage it myself.
                    // As for now, the fab will only animate on user click. Not from a db update.
                    ValueAnimator colorAnim = ObjectAnimator.ofInt(this, "backgroundColor",
                            sectionIsAdded ? COLOR : COLOR_DARK,
                            sectionIsAdded ? COLOR_DARK : COLOR
                    );
                    colorAnim.setDuration(500);
                    colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (mFab != null)
                                mFab.setBackgroundTintList(ColorStateList.valueOf((Integer) animation.getAnimatedValue()));

                        }
                    });
                    colorAnim.setEvaluator(new ArgbEvaluator());
                    colorAnim.start();
                }
            } else {
                //Using ViewCompat to set the tint list is bugged on pre lollipop.
                mFab.setBackgroundTintList(ColorStateList.valueOf(sectionIsAdded ? COLOR_DARK : COLOR));
                ViewCompat.setRotation(mFab, sectionIsAdded ? ROTATION_ADDED : ROTATION_NORMAL);
            }
        mViewState.isSectionAdded = sectionIsAdded;
    }

    @Override
    public void showRatingsLayout() {
        ratingsContainer.setVisibility(VISIBLE);
    }

    @Override
    public void hideRatingsLayout() {
        ratingsContainer.setVisibility(GONE);
    }

    @Override
    public void hideRatingsLoading() {
        ViewGroup progress = ButterKnife.findById(ratingsRoot, R.id.rmp_progressview);
        progress.setVisibility(GONE);
    }

    @Override
    public void addErrorProfessor(String name) {
        addRMPView(new RatingLayoutInflater(getParentActivity(), null)
                .getErrorLayout(name, selectedSection));
    }

    @Override
    public void addRMPProfessor(Professor professor) {
        RatingLayoutInflater ratingLayoutInflater =
                new RatingLayoutInflater(getParentActivity(), professor);
        addRMPView(ratingLayoutInflater.getProfessorLayout());
    }

    @Override
    public void initToolbar() {
        setToolbar(mToolbar);
        getParentActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void addRMPView(View view) {
        view.setAlpha(0);
        ViewCompat.animate(view).setStartDelay(200).alpha(1).start();

        //Onclick intercepts vertical scroll
        /*viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object url =  v.getTag();
                if (url != null) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse((String) url));
                    context.startActivity(i);
                }
            }
        });*/
        ratingsContainer.addView(view);
    }

    public void initViews() {
        setSectionNumber();
        setSectionIndex();
        setSectionCredits();
        setCourseTitle();
        setStatusText();
        setSeatsText();

        for (Metadata data: selectedSection.getMetaData()) {
            ViewGroup metadata = (ViewGroup) LayoutInflater.from(getParentActivity()).inflate(R.layout.metadata, null);
            TextView title = ButterKnife.findById(metadata, R.id.metadata_title);
            TextView description = ButterKnife.findById(metadata, R.id.metadata_text);
            title.setText(data.getTitle());
            description.setText(data.getData());
            mCourseMetadata.addView(metadata);
        }

        setTimes();
        setInstructors();

    }

    private void setSeatsText() {
        if (selectedSection.getMax() == null || selectedSection.getNow() == null) {
            mSeatsContainer.setVisibility(View.GONE);
        }
        mSeatsText.setText(String.valueOf(selectedSection.getNow() + '/' + selectedSection.getMax()));
    }

    public void showFab(boolean animate) {
        if (animate) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(mFab, "scaleX", 0, 1),
                    ObjectAnimator.ofFloat(mFab, "scaleY", 0, 1),
                    ObjectAnimator.ofFloat(mFab, "alpha", 0, 1)

            );
            set.setInterpolator(new DecelerateInterpolator());
            set.setStartDelay(400);
            set.setDuration(250).start();
        } else {
            ViewCompat.setScaleX(mFab, 1);
            ViewCompat.setScaleY(mFab, 1);
            ViewCompat.setAlpha(mFab, 1);
        }
    }

    private void setSectionNumber() {
        mSectionNumberText.setText(selectedSection.getSectionNumber());
    }

    private void setSectionIndex() {
        mIndexNumberText.setText(selectedSection.getIndexNumber());
    }

    private void setSectionCredits() {
        mCreditsText.setText(String.valueOf(selectedSection.getCredits()));
    }

    private void setCourseTitle() {
        mToolbar.setTitle("");
        mCourseTitleText.setText(selectedSection.getCourse().getName());
    }

    private void setStatusText() {
        if (selectedSection.isOpen()) {
            mStatusText.setText("Open");
        } else {
            mStatusText.setText("Closed");
        }
    }

    private void setTimes() {
        LayoutInflater inflater = LayoutInflater.from(getParentActivity());

        //sort times so that Monday > Tuesday and Lecture > Recitation
        for (MeetingTimeModel time : selectedSection.getMeetingTime()) {

            View timeLayout = inflater.inflate(R.layout.section_item_time, mSectionTimeContainer, false);

            TextView dayText = ButterKnife.findById(timeLayout, R.id.section_item_time_info_day_text);
            TextView timeText = ButterKnife.findById(timeLayout, R.id.section_item_time_info_meeting_time_text);
            TextView locationText = ButterKnife.findById(timeLayout, R.id.section_item_time_info_location_text);
            TextView meetingTypeText = ButterKnife.findById(timeLayout, R.id.section_item_time_info_meeting_type);

            dayText.setText(time.getDay());
            timeText.setText(time.getFullTime());
            locationText.setText(time.getRoom());
            meetingTypeText.setText(time.getMeetingType());

            mSectionTimeContainer.addView(timeLayout);
        }
    }

    private void setInstructors() {
        if (selectedSection.getInstructor().size() > 0) {
            mInstructorsText.setText(StringUtils.join(selectedSection.getInstructor(), ", "));
        } else {
            mInstructorsContainer.setVisibility(View.GONE);
        }
    }

    private SectionInfoPresenter getPresenter() {
        return (SectionInfoPresenter) mBasePresenter;
    }

    public static SectionInfoFragment newInstance(SectionModel selectedSection) {
        final SectionInfoFragment newInstance = new SectionInfoFragment();

        final Bundle arguments = new Bundle();
        arguments.putParcelable(CourseInfoView.SELECTED_SECTION, selectedSection);

        newInstance.setArguments(arguments);
        return newInstance;
    }

    @Override
    public String toString() {
        return TAG;
    }
}