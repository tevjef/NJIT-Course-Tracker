package com.tevinjeffrey.njitct.ui.subject;

import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.ActionSwipeListener;
import com.nispok.snackbar.listeners.EventListener;
import com.tevinjeffrey.njitct.NjitCTApp;
import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.model.SubjectModel;
import com.tevinjeffrey.njitct.model.TrackingApiModel;
import com.tevinjeffrey.njitct.ui.base.MVPFragment;
import com.tevinjeffrey.njitct.ui.course.CourseFragment;
import com.tevinjeffrey.njitct.ui.trackedsections.TrackedSectionsView;
import com.tevinjeffrey.njitct.ui.utils.ItemClickListener;
import com.tevinjeffrey.njitct.utils.SemesterUtils;
import com.tevinjeffrey.njitct.utils.Utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icicle;
import timber.log.Timber;

public class SubjectFragment extends MVPFragment implements SubjectView, SwipeRefreshLayout.OnRefreshListener, ItemClickListener<SubjectModel, View> {

    private static final String TAG = SubjectFragment.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.list_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.error_view)
    ViewGroup mErrorView;

    @Icicle
    SemesterUtils.Semester mSemester;

    @Icicle
    ArrayList<SubjectModel> mListDataset;

    @Icicle
    SubjectViewState mViewState = new SubjectViewState();

    @Inject
    TrackingApiModel mApi;

    public SubjectFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mSemester = getArguments().getParcelable(TrackedSectionsView.SEMESTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater themedInflator = inflater.cloneInContext(Utils.wrapContextTheme(getActivity(),
                R.style.NJITCT));
        final View rootView = themedInflator.inflate(R.layout.fragment_subjects, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Recreate presenter if necessary.
        if (mBasePresenter == null) {
            mBasePresenter = new SubjectPresenterImpl(mSemester);
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
            getPresenter().loadSubjects(true);
        } else {
            //Silently load tracked sections on a config change
            if (!getPresenter().isLoading()) {
                getPresenter().loadSubjects(false);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissSnackbar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                this.onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void initToolbar() {
        setToolbarTitle();
        setToolbar(mToolbar);
    }

    public void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getParentActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        if (mListDataset == null) {
            mListDataset = new ArrayList<>(10);
        }

        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(new SubjectFragmentAdapter(mListDataset,
                    this));
        }
    }

    @Override
    public void onItemClicked(SubjectModel subject, View view) {
        Timber.i("Selected subject: %s", subject);
        subject.setSemester(mSemester);
        startCourseFragement(createArgs(subject));
    }

    public void initSwipeLayout() {
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent, R.color.primary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void showLoading(final boolean pullToRefresh) {
        mViewState.isRefreshing = pullToRefresh;
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(pullToRefresh);
                }
            }
        });
    }

    @Override
    public void setData(List<? extends SubjectModel> data) {
        mViewState.data = data;
        mListDataset.clear();
        mListDataset.addAll(data);
        if (mRecyclerView.getAdapter() != null) {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void showError(Throwable t) {
        String message;
        Resources resources = getContext().getResources();
        if (t instanceof UnknownHostException) {
            message = resources.getString(R.string.no_internet);
        } else if (t instanceof JsonParseException) {
            message = resources.getString(R.string.server_down);
        } else if (t instanceof IOException) {
            notifySectionNotOpen();
            getParentActivity().onBackPressed();
            return;
        } else if (t instanceof SocketTimeoutException) {
            message = resources.getString(R.string.timed_out);
        } else {
            message = t.getMessage();
        }

        //error message finalized, now save it.
        mViewState.errorMessage = message;

        //Show the error layout if there's nothing in the adpater to show.
        // Redirects the message that would usually be in the snackbar, to error layout.
        if (!adapterHasItems()) {
            showLayout(LayoutType.ERROR);
            TextView textViewMessage = ButterKnife.findById(mErrorView, R.id.text);
            textViewMessage.setText(message);
        } else {
            showSnackBar(message);
        }
    }

    @OnClick(R.id.try_again)
    public void onTryAgainClick(View view) {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        getPresenter().loadSubjects(true);
    }

    public void showLayout(LayoutType type) {
        mViewState.layoutType = type;
        switch (type) {
            case ERROR:
                showRecyclerView(View.GONE);
                showErrorLayout(View.VISIBLE);
                //enableSwipeRefreshLayout(true);
                break;
            case LIST:
                showErrorLayout(View.GONE);
                showRecyclerView(View.VISIBLE);
                //enableSwipeRefreshLayout(true);
                break;
            default:
                throw new RuntimeException("Unknown type: " + type);
        }
    }

    private boolean adapterHasItems() {
        return mRecyclerView.getAdapter().getItemCount() > 0;
    }

    private void notifySectionNotOpen() {
        Toast.makeText(getParentActivity().getApplicationContext(),
                mSemester.toString() + getParentActivity()
                        .getString(R.string.section_has_not_opened),
                Toast.LENGTH_LONG).show();
    }

    private void showRecyclerView(int visibility) {
        if (mRecyclerView.getVisibility() != visibility)
            mRecyclerView.setVisibility(visibility);
    }

    private void showErrorLayout(int visibility) {
        if (mErrorView.getVisibility() != visibility)
            mErrorView.setVisibility(visibility);
    }

    private void showSnackBar(CharSequence message) {
        SnackbarManager.show(
                Snackbar.with(getParentActivity())
                        .type(SnackbarType.MULTI_LINE)
                        .text(message)
                        .actionLabel(R.string.snackbar_retry)// text to display
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                onRefresh();
                                mViewState.snackBarShowing = false;
                            }
                        })
                        .swipeListener(new ActionSwipeListener() {
                            @Override
                            public void onSwipeToDismiss() {
                                mViewState.snackBarShowing = false;
                            }
                        })
                        .actionColor(ContextCompat.getColor(getParentActivity(), android.R.color.white))
                        .color(ContextCompat.getColor(getParentActivity(), R.color.accent))// action button label color
                        .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                        .eventListener(new EventListener() {
                            @Override
                            public void onShow(Snackbar snackbar) {
                                if (snackbar != null) {
                                    mViewState.snackBarShowing = true;
                                    if (snackbar.getText() != null) {
                                        mViewState.errorMessage = snackbar.getText().toString();
                                    }
                                }
                            }

                            @Override
                            public void onShowByReplace(Snackbar snackbar) {

                            }

                            @Override
                            public void onShown(Snackbar snackbar) {

                            }

                            @Override
                            public void onDismiss(Snackbar snackbar) {

                            }

                            @Override
                            public void onDismissByReplace(Snackbar snackbar) {

                            }

                            @Override
                            public void onDismissed(Snackbar snackbar) {

                            }
                        })

                , getParentActivity()); // activity where it is displayed
    }

    private void dismissSnackbar() {
        //It's only being dismissed to not leak the fragment
        if (SnackbarManager.getCurrentSnackbar() != null) {
            SnackbarManager.dismiss();
        }
    }

    private void setToolbarTitle() {
        super.setToolbarTitle(mToolbar, mSemester.toString());
    }

    private void startCourseFragement(Bundle b) {
        CourseFragment courseFragment = new CourseFragment();
        courseFragment.setArguments(b);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition sfTransition = TransitionInflater.from(getParentActivity()).inflateTransition(R.transition.sf_exit);
            setExitTransition(sfTransition.excludeTarget(Toolbar.class, true));
            courseFragment.setAllowEnterTransitionOverlap(false);
        } else {
            ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        }

        startFragment(this, courseFragment, ft);
    }

    private Bundle createArgs(Parcelable selectedSubject) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTED_SUBJECT, selectedSubject);
        return bundle;
    }

    private SubjectPresenter getPresenter() {
        return (SubjectPresenter) mBasePresenter;
    }
}