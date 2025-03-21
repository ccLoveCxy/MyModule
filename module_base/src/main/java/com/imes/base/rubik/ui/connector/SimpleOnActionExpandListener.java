package com.imes.base.rubik.ui.connector;

import android.view.MenuItem;

import androidx.core.view.MenuItemCompat;

/**
 * Created by linjiang on 07/06/2018.
 */

public class SimpleOnActionExpandListener
        implements MenuItem.OnActionExpandListener, MenuItemCompat.OnActionExpandListener {
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        // true means menuView can expand
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        // true means menuView can collapse
        return true;
    }

    public static void bind(MenuItem menuItem, final SimpleOnActionExpandListener callback) {
        try {
            menuItem.setOnActionExpandListener(new SimpleOnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    return callback.onMenuItemActionCollapse(item);
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return callback.onMenuItemActionExpand(item);
                }
            });
        } catch (UnsupportedOperationException e) {
            MenuItemCompat.setOnActionExpandListener(menuItem, new SimpleOnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return callback.onMenuItemActionCollapse(item);
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    return callback.onMenuItemActionExpand(item);
                }
            });
        }
    }
}
