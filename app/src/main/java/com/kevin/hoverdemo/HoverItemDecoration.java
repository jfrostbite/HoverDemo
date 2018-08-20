package com.kevin.hoverdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * HoverItemDecoration 继承字ItemDecoration 用于实现RecyclerView的 特殊效果
 * <p>
 * 悬停思路：
 * 1、顶部固定悬停 浮于最上层 该复层绘制在onDrawOver
 * 2、分组头部根据接口获取头部标记 类似绘制分割线一般 只是需要在制定item上 进行绘制
 * 3、
 */

public class HoverItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint mDividerPaint;
    private final Paint mHoverPaint;
    private final Paint mTextPaint;
    private HoverState mHoverState;
    private int mHoverHeight = 60;
    private int mDividerHeight = 2;
    private int mHoverColor = Color.RED;
    private int mTextColor = Color.WHITE;
    private int mDividerColor = Color.parseColor("#ECECEC");
    //悬停文字绘制 索引，该索引随着悬停头的变化而变化
    private int mHoverPosition;

    private int mCurrentPosition = -1;

    /**
     * 构造函数传入 悬停数据
     */
    public HoverItemDecoration(HoverState hoverState) {
        super();
        mHoverState = hoverState;
        //分割线画笔
        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setColor(mDividerColor);
        //悬停画笔
        mHoverPaint = new Paint();
        mHoverPaint.setAntiAlias(true);
        mHoverPaint.setColor(mHoverColor);
        //文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mHoverHeight);
    }

    /**
     * 设置组数据
     */
    public HoverItemDecoration setHoverArug(int hoverHeight, int hoverColor, int textColor) {
        mHoverHeight = hoverHeight;
        mHoverColor = hoverColor;
        mTextColor = textColor;
        return this;
    }

    /**
     * 设置分割线高度和颜色
     */
    public HoverItemDecoration setDivider(int dividerHeight, int dividerColor) {
        mDividerHeight = dividerHeight;
        mDividerColor = dividerColor;
        return this;
    }

    /**
     * 该方法绘制不会覆盖 RecyclerView Item 的界面
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            //真实View 的 大小
            int left = view.getLeft() + view.getPaddingLeft();
            int right = view.getRight() - view.getPaddingRight();
            int top = view.getTop() + view.getPaddingTop();
            int bottom = view.getBottom() - view.getPaddingBottom();

            //分割线的大小
            int dividerTop = bottom;
            int dividerBottom = bottom + mDividerHeight;

            //悬停的大小
            int hoverTop = top - mHoverHeight;
            int hoverBottom = top;

            //该View 所在Position
            int position = parent.getChildAdapterPosition(view);

            //绘制分割线
            drawDivider(c, left, dividerTop, right, dividerBottom);

            //绘制活动悬停块
            drawHover(c, left, hoverTop, right, hoverBottom, position, false);

        }
    }

    /**
     * 绘制悬停块
     *
     * @param c
     * @param left
     * @param hoverTop
     * @param right
     * @param hoverBottom
     * @param position
     * @param top         该参数 来强制绘制Hover
     */
    private void drawHover(Canvas c, int left, int hoverTop, int right, int hoverBottom, int position, boolean top) {
        if (mHoverState.isHoverTag(position) || top) {
            Rect hoverRect = new Rect(left, hoverTop, right, hoverBottom);
            //绘制悬停方框的Rect
            c.drawRect(hoverRect, mHoverPaint);
            //计算文字位置,首先获取文字宽高
            String txt = mHoverState.getHoverTitle(position);
            //储存文字的宽高
            Rect txtRect = new Rect();
            mTextPaint.getTextBounds(txt, 0, txt.length(), txtRect);
            //文字左边位置
            int txtLeft = hoverRect.centerX() - Math.abs(txtRect.centerX());
            int txtBottom = hoverRect.centerY() + Math.abs(txtRect.centerY());
            c.drawText(txt, txtLeft, txtBottom, mTextPaint);
        }
    }

    /**
     * 绘制分割线
     *
     * @param c
     * @param left
     * @param dividerTop
     * @param right
     * @param dividerBottom
     */
    private void drawDivider(Canvas c, int left, int dividerTop, int right, int dividerBottom) {
        c.drawRect(left, dividerTop, right, dividerBottom, mDividerPaint);
    }

    /**
     * 方法绘制会覆盖Item的界面
     * <p>
     * 绘制固定悬停
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //首先绘制固定的顶部悬停，给出平移条件，根据平移条件进行平移，平移条件就是当满足悬停的item顶部小于顶部固定悬停高度是
        //想办法使顶部悬停头向上移动，可以利用Canvas进行移动
        int rectLeft = parent.getPaddingLeft();
        int rectTop = parent.getPaddingTop();
        int rectRight = parent.getRight() - parent.getPaddingRight();
        int rectBottom = rectTop + mHoverHeight;
        //画板平移标记
//        boolean flag = canvasTrans(c, parent, rectTop);
        boolean flag = false;

        //获取当前页面展示的第一个ItemView ，判断该ItemView 是否是悬停Hover
        int position = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
        //判断当前页面的第二个View 是否是下一个悬停，如果是，执行Canvas的平移操作
        RecyclerView.ViewHolder vh = parent.findViewHolderForAdapterPosition(position);
        if (vh != null) {
            View view = vh.itemView;
            //此处顶部悬停View 大小宽度用ItemView的大小来确定 可以 与 悬停View保持一致
            rectLeft = view.getLeft() + view.getPaddingLeft();
            rectRight = view.getRight() - view.getPaddingRight();
            //判断 该position 是否滑出界面
            if (mHoverState.isHoverTag(position + 1) && view.getBottom() < rectBottom && view.getBottom() > rectTop) {//滑出条件成立
                flag = true;
                c.save();
                c.translate(0, view.getBottom() - mHoverHeight + mDividerHeight);
            }
        }

        if (position > mCurrentPosition) {// 向下滑
            Log.e("TAG", "向下滑动 ---- " + position + "mCurr ---- " + mCurrentPosition);
            mCurrentPosition = position;
        } else if (position < mCurrentPosition) {//向上滑
            Log.e("TAG", "向上滑动 ---- " + position + "mCurr ---- " + mCurrentPosition);
            mCurrentPosition = position;
        }

        //此处放在 评议后进行 绘制操作，否则不起作用。
//        c.drawRect(rectLeft, rectTop, rectRight, rectBottom, mHoverPaint);
        drawHover(c, rectLeft, rectTop, rectRight, rectBottom, position, true);
//        Log.e("TAG", "mHoverPosition = " + mHoverPosition);

        if (flag) {
            c.restore();
        }
    }

    @Deprecated
    private boolean canvasTrans(@NonNull Canvas c, @NonNull RecyclerView parent, int rectTop) {
        boolean flag = false;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            //判断如果下一个Item 是 悬停头
            if (mHoverState.isHoverTag(position + 1)) {
                //判断当前item 底部是否 小于HoverHeight 如果小于，就执行Hover 的平移操作
                if (view.getBottom() < mHoverHeight - mDividerHeight && view.getBottom() > rectTop) {
                    mHoverPosition = position + 1;
                    c.save();
                    flag = true;
//                    int height = view.getHeight();
//                    int top = view.getTop();
//                    int bottom = view.getBottom();
//                    Log.e("TAG", "Height = " + height + "；top = " + top+ "；bottom = " + bottom + "；mHoverHeight = " + mHoverHeight);
                    int dy = view.getBottom() - mHoverHeight + mDividerHeight;
//                    Log.e("TAG", "dy = " + dy);
                    c.translate(0, dy);
                }
            }
        }
        return flag;
    }

    /**
     * 用于扩展item 距离四周的距离
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //是否是纵向滑动
        boolean isVertical = false;
        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        if (lm != null && LinearLayoutManager.class.isAssignableFrom(lm.getClass())) {
            LinearLayoutManager llm = (LinearLayoutManager) lm;
            isVertical = llm.getOrientation() == LinearLayoutManager.VERTICAL;
        }
        if (!isVertical) {
            throw new RuntimeException("This LayoutManager isn't VERTICAL");
        }
        //获取当前View 所在 position
        int position = parent.getChildAdapterPosition(view);
        //判断头部标记
        if (mHoverState.isHoverTag(position)) {//组头,给出组头高度
            outRect.top = mHoverHeight;
        }
        //分割线
        outRect.bottom = mDividerHeight;
    }

    /**
     * 根据position 获取当前头标记，和当前头标题
     */
    public interface HoverState {
        //获取头标记
        boolean isHoverTag(int position);

        //获取头标题
        String getHoverTitle(int position);
    }
}
