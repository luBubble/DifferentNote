package icontest;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by laisixiang on 2015/12/28. 
 */
public class IconView extends TextView {

    public IconView(Context context) {
        super(context);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface iconfont = Typeface.createFromAsset(getContext().getAssets(), "assets/iconfont.ttf");
        this.setTypeface(iconfont);
    }
}  