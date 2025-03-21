package com.imes.base.rubik;

import androidx.annotation.DrawableRes;

/**
 * Created by Quintus on 2021/11/1.
 * <p>
 * Please check @{@link FuncController#addFunc(IFunc)}
 */

public interface IFunc {

    /**
     * @return the icon of function.
     */
    @DrawableRes
    int getIcon();

    /**
     * @return the name of function.
     */
    String getName();

    /**
     * Click event.
     *
     * @return "Turn on" the state of the Func once return true, turn off otherwise.
     */
    boolean onClick();
}
