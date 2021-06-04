package com.get.current.location.in.java.ui.base;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    /**
     ***********************************************************************************************
     ******************************************** Properties ***************************************
     ***********************************************************************************************
     */
    private SparseArray<View> viewSparseArray;
    /**
     ***********************************************************************************************
     ********************************* BaseActivity abstract methods *******************************
     ***********************************************************************************************
     */
    @LayoutRes
    protected abstract int getLayoutID();
    protected abstract void initializeView();
    protected abstract void initializeObject();
    protected abstract void initializeToolBar();
    protected abstract void initializeCallbackListener();
    protected abstract void addTextChangedListener();
    protected abstract void setOnClickListener();
    protected abstract void handleClickEvent(View view);
    /**
     ***********************************************************************************************
     ************************************ Activity lifecycle methods *******************************
     ***********************************************************************************************
     */
    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate(Bundle savedInstanceState)");

        viewSparseArray = new SparseArray<>();

        setContentView(getLayoutID());

        initializeView();
        initializeObject();
        initializeToolBar();
        initializeCallbackListener();
        addTextChangedListener();
        setOnClickListener();
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    @CallSuper
    protected void onRestart() { /* Only called after onStop() */
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    @CallSuper
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed()");
    }

    /**
     * Find the view control and put it in the collection
     *
     * @param viewID the id of the view to be found
     * @param <E>    the returned view
     * @return
     */
    public <E extends View> E findView(int viewID) {
        E view = (E) viewSparseArray.get(viewID);
        if (view == null) {
            /* If the searched view is not in the collection, add it to the collection */
            view = (E) findViewById(viewID);
            if (view != null) {
                viewSparseArray.append(viewID, view);
            }
        }
        return view;
    }

    /**
     * View's click event
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        handleClickEvent(v);
    }

    /**
     * Set the click event to the view
     *
     * @param view The view bound to the event
     * @param <E>
     */
    public <E extends View> void setOnClick(E view) {
        view.setOnClickListener(this);
    }
}
