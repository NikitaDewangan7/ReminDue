package blocker.com.newalarmservice.utilities;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class VerticalSpaceItemDecorator extends RecyclerView.ItemDecoration {
    private final int mVerticalSpaceHeight;

    public VerticalSpaceItemDecorator(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = 20;
        outRect.right = 20;
        outRect.left = 20;

    }
}
