package com.dk.view.folder;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class ResideMenu extends FrameLayout {

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;
    private static final int PRESSED_MOVE_HORIZONTAL = 2;
    private static final int PRESSED_DOWN = 3;
    private static final int PRESSED_DONE = 4;
    private static final int PRESSED_MOVE_VERTICAL = 5;
    private static final int DURATION_DEFAULT = 500;

    private ImageView imageViewBackground;
    private LinearLayout layoutLeftMenu;
    private LinearLayout layoutRightMenu;
    private ScrollView scrollViewLeftMenu;
    private ScrollView scrollViewRightMenu;
    private ScrollView scrollViewMenu;
    /**
     * Current attaching activity.
     */
    private Activity activity;
    /**
     * The DecorView of current activity.
     */
    private ViewGroup viewDecor;
    private TouchDisableView viewActivity;
    /**
     * The flag of menu opening status.
     */
    private boolean isOpened;
    /**
     * Views which need stop to intercept touch events.
     */
    private List<View> ignoredViews;
    private List<ResideMenuItem> leftMenuItems;
    private List<ResideMenuItem> rightMenuItems;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private OnMenuListener menuListener;
    private float lastRawX;
    private boolean isInIgnoredView = false;
    private int scaleDirection = DIRECTION_LEFT;
    private int pressedState = PRESSED_DOWN;
    private List<Integer> disabledSwipeDirection = new ArrayList<Integer>();
    // Valid scale factor is between 0.0f and 1.0f.
    private float mScaleValue = 0.5f;
    private int mDuration = DURATION_DEFAULT;

    public ResideMenu(Context context) {
        super(context);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.residemenu, this);
        scrollViewLeftMenu = (ScrollView) findViewById(R.id.sv_left_menu);
        scrollViewRightMenu = (ScrollView) findViewById(R.id.sv_right_menu);
        layoutLeftMenu = (LinearLayout) findViewById(R.id.layout_left_menu);
        layoutRightMenu = (LinearLayout) findViewById(R.id.layout_right_menu);
        imageViewBackground = (ImageView) findViewById(R.id.iv_background);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        // Applies the content insets to the view's padding, consuming that content (modifying the insets to be 0),
        // and returning true. This behavior is off by default and can be enabled through setFitsSystemWindows(boolean)
        // in api14+ devices.
        this.setPadding(viewActivity.getPaddingLeft() + insets.left, viewActivity.getPaddingTop() + insets.top,
                viewActivity.getPaddingRight() + insets.right, viewActivity.getPaddingBottom() + insets.bottom);
        insets.left = insets.top = insets.right = insets.bottom = 0;
        return true;
    }

    /**
     * Set up the activity;
     *
     * @param activity
     */
    public void attachToActivity(Activity activity) {
        initValue(activity);
        viewDecor.addView(this, 0);
    }

    private void initValue(Activity activity) {
        this.activity = activity;
        leftMenuItems = new ArrayList<ResideMenuItem>();
        rightMenuItems = new ArrayList<ResideMenuItem>();
        ignoredViews = new ArrayList<View>();
        viewDecor = (ViewGroup) activity.getWindow().getDecorView();
        viewActivity = new TouchDisableView(this.activity);

        View mContent = viewDecor.getChildAt(0);
        viewDecor.removeViewAt(0);
        viewActivity.setContent(mContent);
        addView(viewActivity);

//        ViewGroup parent = (ViewGroup) scrollViewLeftMenu.getParent();
//        parent.removeView(scrollViewLeftMenu);
//        parent.removeView(scrollViewRightMenu);
    }

    /**
     * Set the background image of menu;
     *
     * @param imageResource
     */
    public void setBackground(int imageResource) {
        imageViewBackground.setImageResource(imageResource);
    }

    /**
     * Add a single item to the left menu;
     * <p/>
     * WARNING: It will be removed from v2.0.
     *
     * @param menuItem
     */
    @Deprecated
    public void addMenuItem(ResideMenuItem menuItem) {
        this.leftMenuItems.add(menuItem);
        layoutLeftMenu.addView(menuItem);
    }

    /**
     * Add a single items;
     *
     * @param menuItem
     * @param direction
     */
    public void addMenuItem(ResideMenuItem menuItem, int direction) {
        if (direction == DIRECTION_LEFT) {
            this.leftMenuItems.add(menuItem);
            layoutLeftMenu.addView(menuItem);
        } else {
            this.rightMenuItems.add(menuItem);
            layoutRightMenu.addView(menuItem);
        }
    }

    /**
     * WARNING: It will be removed from v2.0.
     *
     * @param menuItems
     */
    @Deprecated
    public void setMenuItems(List<ResideMenuItem> menuItems) {
        this.leftMenuItems = menuItems;
        rebuildMenu();
    }

    /**
     * Set menu items by a array;
     *
     * @param menuItems
     * @param direction
     */
    public void setMenuItems(List<ResideMenuItem> menuItems, int direction) {
        if (direction == DIRECTION_LEFT)
            this.leftMenuItems = menuItems;
        else
            this.rightMenuItems = menuItems;
        rebuildMenu();
    }

    private void rebuildMenu() {
        layoutLeftMenu.removeAllViews();
        layoutRightMenu.removeAllViews();
        for (ResideMenuItem leftMenuItem : leftMenuItems)
            layoutLeftMenu.addView(leftMenuItem);
        for (ResideMenuItem rightMenuItem : rightMenuItems)
            layoutRightMenu.addView(rightMenuItem);
    }

    /**
     * WARNING: It will be removed from v2.0.
     *
     * @return
     */
    @Deprecated
    public List<ResideMenuItem> getMenuItems() {
        return leftMenuItems;
    }

    /**
     * Return instances of menu items;
     *
     * @return
     */
    public List<ResideMenuItem> getMenuItems(int direction) {
        if (direction == DIRECTION_LEFT)
            return leftMenuItems;
        else
            return rightMenuItems;
    }

    /**
     * If you need to do something on closing or opening menu,
     * set a listener here.
     *
     * @return
     */
    public void setMenuListener(OnMenuListener menuListener) {
        this.menuListener = menuListener;
    }


    public OnMenuListener getMenuListener() {
        return menuListener;
    }

    /**
     * Show the menu;
     */
    public void openMenu(int direction) {
        setScaleDirection(direction);
        isOpened = true;

        ObjectAnimator animator = ObjectAnimator.ofFloat(viewActivity, "FolderX", viewActivity.getFolderX(), 0.5f);
        animator.addListener(openAnimatorListener);
        animator.setDuration(mDuration);
        animator.start();

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(scrollViewMenu, "alpha", scrollViewMenu.getAlpha(), 1f);
        alphaAnimator.setDuration(500);
        alphaAnimator.start();
    }

    /**
     * Close the menu;
     */
    public void closeMenu() {
        isOpened = false;

        ObjectAnimator animator = ObjectAnimator.ofFloat(viewActivity, "FolderX", viewActivity.getFolderX(), 1f);
        animator.setDuration(500);
        animator.addListener(closeAnimatorListener);
        animator.start();

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(scrollViewMenu, "alpha", scrollViewMenu.getAlpha(), 0f);
        alphaAnimator.setDuration(500);
        alphaAnimator.start();
    }

    private Animator.AnimatorListener closeAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            moveMenuLayer(1);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mDirectionFlag = true;
            viewActivity.revertView();
            if (isOpened()) {
                viewActivity.setTouchDisable(true);
                viewActivity.setOnClickListener(viewActivityOnClickListener);
            } else {
                viewActivity.setTouchDisable(false);
                viewActivity.setOnClickListener(null);
                hideScrollViewMenu(scrollViewLeftMenu);
                hideScrollViewMenu(scrollViewRightMenu);
                if (menuListener != null)
                    menuListener.closeMenu();
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            mDirectionFlag = true;
            viewActivity.revertView();
        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private Animator.AnimatorListener openAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            viewActivity.setTouchDisable(false);
            if (isOpened()) {
                showScrollViewMenu(scrollViewMenu);
                if (menuListener != null)
                    menuListener.openMenu();
            }
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (isOpened()) {
                viewActivity.setTouchDisable(true);
                viewActivity.setOnClickListener(viewActivityOnClickListener);

                moveMenuLayer();
            } else {
                viewActivity.setTouchDisable(false);
                viewActivity.setOnClickListener(null);
                hideScrollViewMenu(scrollViewLeftMenu);
                hideScrollViewMenu(scrollViewRightMenu);
                if (menuListener != null)
                    menuListener.closeMenu();
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };


    @Deprecated
    public void setDirectionDisable(int direction) {
        disabledSwipeDirection.add(direction);
    }

    public void setSwipeDirectionDisable(int direction) {
        disabledSwipeDirection.add(direction);
    }

    private boolean isInDisableDirection(int direction) {
        return disabledSwipeDirection.contains(direction);
    }

    private void setScaleDirection(int direction) {

        int screenWidth = getScreenWidth();
        float pivotX;
        float pivotY = getScreenHeight() * 0.5f;

        if (direction == DIRECTION_LEFT) {
            scrollViewMenu = scrollViewLeftMenu;
            pivotX = screenWidth * 1.5f;
        } else {
            scrollViewMenu = scrollViewRightMenu;
            pivotX = screenWidth * -0.5f;
        }

//        ViewHelper.setPivotX(viewActivity, pivotX);
//        ViewHelper.setPivotY(viewActivity, pivotY);
//        ViewHelper.setPivotX(imageViewShadow, pivotX);
//        ViewHelper.setPivotY(imageViewShadow, pivotY);
        /**
         * add by Dean Ding
         *
         * init folder direction
         */
        viewActivity.setDirection(direction);
        scaleDirection = direction;
    }

    /**
     * return the flag of menu status;
     *
     * @return
     */
    public boolean isOpened() {
        return isOpened;
    }

    private OnClickListener viewActivityOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isOpened()) closeMenu();
        }
    };

//    private Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
//        @Override
//        public void onAnimationStart(Animator animation) {
//            if (isOpened()) {
//                showScrollViewMenu(scrollViewMenu);
//                if (menuListener != null)
//                    menuListener.openMenu();
//            }
//        }
//
//        @Override
//        public void onAnimationEnd(Animator animation) {
//            // reset the view;
//            if (isOpened()) {
//                viewActivity.setTouchDisable(true);
//                viewActivity.setOnClickListener(viewActivityOnClickListener);
//            } else {
//                viewActivity.setTouchDisable(false);
//                viewActivity.setOnClickListener(null);
//                hideScrollViewMenu(scrollViewLeftMenu);
//                hideScrollViewMenu(scrollViewRightMenu);
//                if (menuListener != null)
//                    menuListener.closeMenu();
//            }
//        }
//
//        @Override
//        public void onAnimationCancel(Animator animation) {
//
//        }
//
//        @Override
//        public void onAnimationRepeat(Animator animation) {
//
//        }
//    };


    /**
     * If there were some view you don't want reside menu
     * to intercept their touch event, you could add it to
     * ignored views.
     *
     * @param v
     */
    public void addIgnoredView(View v) {
        ignoredViews.add(v);
    }

    /**
     * Remove a view from ignored views;
     *
     * @param v
     */
    public void removeIgnoredView(View v) {
        ignoredViews.remove(v);
    }

    /**
     * Clear the ignored view list;
     */
    public void clearIgnoredViewList() {
        ignoredViews.clear();
    }

    /**
     * If the motion event was relative to the view
     * which in ignored view list,return true;
     *
     * @param ev
     * @return
     */
    private boolean isInIgnoredView(MotionEvent ev) {
        Rect rect = new Rect();
        for (View v : ignoredViews) {
            v.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getX(), (int) ev.getY()))
                return true;
        }
        return false;
    }

    private void setScaleDirectionByRawX(float currentRawX) {
        if (currentRawX < lastActionDownX)
            setScaleDirection(DIRECTION_RIGHT);
        else
            setScaleDirection(DIRECTION_LEFT);
    }

    private float getTargetScale(float currentRawX) {
        float scaleFloatX = ((currentRawX - lastActionDownX) / getScreenWidth());
        float result;
        if (scaleDirection == DIRECTION_RIGHT) {
            if (isOpened())
                result = 0.5f + scaleFloatX;
            else
                result = 1 + scaleFloatX;
        } else {
            if (isOpened())
                result = 0.5f - scaleFloatX;
            else
                result = 1 - scaleFloatX;
        }
        return result;
    }

    private float lastActionDownX, lastActionDownY;
    private boolean mDirectionFlag = true;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        float currentActivityScaleX = viewActivity.getFolderX();

        if (currentActivityScaleX == 1.0f && mDirectionFlag &&
                ev.getAction() != MotionEvent.ACTION_DOWN) {
            setScaleDirectionByRawX(ev.getRawX());
            //when scroll above 30 px, the direction will be locked.
            if (Math.abs(ev.getRawX() - lastActionDownX) > 30)
                mDirectionFlag = false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastActionDownX = ev.getX();
                lastActionDownY = ev.getY();
                isInIgnoredView = isInIgnoredView(ev) && !isOpened();
                pressedState = PRESSED_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                if (isInIgnoredView || isInDisableDirection(scaleDirection))
                    break;

                if (pressedState != PRESSED_DOWN &&
                        pressedState != PRESSED_MOVE_HORIZONTAL)
                    break;

                int xOffset = (int) (ev.getX() - lastActionDownX);
                int yOffset = (int) (ev.getY() - lastActionDownY);

                if (pressedState == PRESSED_DOWN) {
                    if (yOffset > 25 || yOffset < -25) {
                        pressedState = PRESSED_MOVE_VERTICAL;
                        break;
                    }
                    if (xOffset < -50 || xOffset > 50) {
                        pressedState = PRESSED_MOVE_HORIZONTAL;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else if (pressedState == PRESSED_MOVE_HORIZONTAL) {
                    showScrollViewMenu(scrollViewMenu);
                    moveMenuLayer(1);

                    float targetScale = getTargetScale(ev.getRawX());
                    ViewHelper.setAlpha(scrollViewMenu, (1 - targetScale) * 2.0f);
                    viewActivity.setFolderX(targetScale);
                    lastRawX = ev.getRawX();
                    return true;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isInIgnoredView) break;
                if (pressedState != PRESSED_MOVE_HORIZONTAL) {
                    mDirectionFlag = true;
                    break;
                }

                pressedState = PRESSED_DONE;
                if (isOpened()) {
                    if (currentActivityScaleX > 0.75f) {
                        closeMenu();
                    } else
                        openMenu(scaleDirection);
                } else {
                    if (currentActivityScaleX < 0.75f) {
                        openMenu(scaleDirection);
                    } else {
                        closeMenu();
                    }
                }
                break;
        }
        lastRawX = ev.getRawX();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public int getScreenHeight() {
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void setScaleValue(float scaleValue) {
        this.mScaleValue = scaleValue;
    }

    public interface OnMenuListener {

        /**
         * This method will be called at the finished time of opening menu animations.
         */
        public void openMenu();

        /**
         * This method will be called at the finished time of closing menu animations.
         */
        public void closeMenu();
    }

    private void showScrollViewMenu(ScrollView scrollViewMenu) {
        scrollViewMenu.setVisibility(View.VISIBLE);
    }

    private void hideScrollViewMenu(ScrollView scrollViewMenu) {
        scrollViewMenu.setVisibility(View.INVISIBLE);
    }

    /**
     * 0 is background
     * 1 is above background
     * 2 is viewActivity(Content)
     *
     * @param index
     */
    private void moveMenuLayer(int index) {
        if (scrollViewMenu.getParent() != null) {
            removeView(scrollViewMenu);
            addView(scrollViewMenu, index);
        }
    }

    private void moveMenuLayer() {
        if (scrollViewMenu.getParent() != null) {
            removeView(scrollViewMenu);
            addView(scrollViewMenu);
        }
    }

}
