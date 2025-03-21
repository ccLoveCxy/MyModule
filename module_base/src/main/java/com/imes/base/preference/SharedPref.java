
package com.imes.base.preference;

import com.imes.base.preference.protocol.IProvider;
import com.imes.base.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class SharedPref {

    private List<IProvider> providers = new ArrayList<>();
    private SharedPrefDriver driver;

    public SharedPref() {
        driver = new SharedPrefDriver(Utils.getContext());
        providers.add(new SharedPrefProvider());
    }

    public SharedPref addProvider(IProvider provider) {
        providers.add(provider);
        return this;
    }

    public List<File> getSharedPrefDescs() {
        List<File> descriptors = new ArrayList<>();
        for (int i = 0; i < providers.size(); i++) {
            descriptors.addAll(driver.getSharedPrefDescs(providers.get(i)));
        }
        return descriptors;
    }

    public Map<String, String> getSharedPrefContent(File descriptor) {
        return driver.getSharedPrefContent(descriptor);
    }

    public String updateSharedPref(File descriptor, String key, String value) {
        try {
            driver.updateSharedPref(descriptor, key, value);
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            return t.getMessage();
        }
    }

    public String removeSharedPrefKey(File descriptor, String key) {
        try {
            driver.removeSharedPrefKey(descriptor, key);
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            return t.getMessage();
        }
    }
}
