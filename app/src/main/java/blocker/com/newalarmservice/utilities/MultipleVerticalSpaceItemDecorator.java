package blocker.com.newalarmservice.utilities;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class MultipleVerticalSpaceItemDecorator extends RecyclerView.ItemDecoration {
    private final int mVerticalSpaceHeight;

    public MultipleVerticalSpaceItemDecorator(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = 20;
        outRect.right = 5;
        outRect.left = 5;

    }
}
