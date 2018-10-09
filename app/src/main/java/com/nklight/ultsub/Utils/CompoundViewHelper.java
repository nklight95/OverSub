package com.nklight.ultsub.Utils;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class CompoundViewHelper {
    private ViewGroup view;

    public CompoundViewHelper(ViewGroup view) {
        this.view = view;
    }

    /**
     * @param state state to be restore
     * @return state to be restored for supper class
     */
    public Parcelable onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        for (int i = 0; i < view.getChildCount(); i++) {
            view.getChildAt(i).restoreHierarchyState(ss.childrenStates);
        }
        return ss.getSuperState();
    }

    /**
     * @param superState saved state of supper class
     * @return saved state
     */
    public Parcelable onSaveInstanceState(Parcelable superState) {
        SavedState ss = new SavedState(superState);
        ss.childrenStates = new SparseArray();
        for (int i = 0; i < view.getChildCount(); i++) {
            view.getChildAt(i).saveHierarchyState(ss.childrenStates);
        }
        return ss;
    }

    static class SavedState extends View.BaseSavedState {
        SparseArray childrenStates;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in, ClassLoader classLoader) {
            super(in);
            childrenStates = in.readSparseArray(classLoader);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSparseArray(childrenStates);
        }

        public static final ClassLoaderCreator<SavedState> CREATOR
                = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel source) {
                return createFromParcel(source, null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public static class LinearLayout extends android.widget.LinearLayout {

        public LinearLayout(Context context) {
            super(context);
        }

        public LinearLayout(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public LinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public LinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        //  <editor-fold desc="copy-able code" defaultstate="collapsed">
        @Override
        protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
            dispatchFreezeSelfOnly(container);
        }

        @Override
        protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
            dispatchThawSelfOnly(container);
        }

        protected CompoundViewHelper compoundViewHelper = new CompoundViewHelper(this);

        @Nullable
        @Override
        protected Parcelable onSaveInstanceState() {
            Parcelable state = super.onSaveInstanceState();
            return compoundViewHelper.onSaveInstanceState(state);
        }

        @Override
        protected void onRestoreInstanceState(Parcelable state) {
            super.onRestoreInstanceState(compoundViewHelper.onRestoreInstanceState(state));
        }
        //</editor-fold>
    }

    public static class ConstraintLayout extends android.support.constraint.ConstraintLayout {
        public ConstraintLayout(Context context) {
            super(context);
        }

        public ConstraintLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        //  <editor-fold desc="copy-able code" defaultstate="collapsed">
        @Override
        protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
            dispatchFreezeSelfOnly(container);
        }

        @Override
        protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
            dispatchThawSelfOnly(container);
        }

        protected CompoundViewHelper compoundViewHelper = new CompoundViewHelper(this);

        @Nullable
        @Override
        protected Parcelable onSaveInstanceState() {
            Parcelable state = super.onSaveInstanceState();
            return compoundViewHelper.onSaveInstanceState(state);
        }

        @Override
        protected void onRestoreInstanceState(Parcelable state) {
            super.onRestoreInstanceState(compoundViewHelper.onRestoreInstanceState(state));
        }
        //</editor-fold>
    }

    public static class FrameLayout extends android.widget.FrameLayout{

        public FrameLayout(@NonNull Context context) {
            super(context);
        }

        public FrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public FrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public FrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
        //  <editor-fold desc="copy-able code" defaultstate="collapsed">
        @Override
        protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
            dispatchFreezeSelfOnly(container);
        }

        @Override
        protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
            dispatchThawSelfOnly(container);
        }

        protected CompoundViewHelper compoundViewHelper = new CompoundViewHelper(this);

        @Nullable
        @Override
        protected Parcelable onSaveInstanceState() {
            Parcelable state = super.onSaveInstanceState();
            return compoundViewHelper.onSaveInstanceState(state);
        }

        @Override
        protected void onRestoreInstanceState(Parcelable state) {
            super.onRestoreInstanceState(compoundViewHelper.onRestoreInstanceState(state));
        }
        //</editor-fold>
    }
}
