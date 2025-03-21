package com.example.mymodule;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

public class MyModule extends ViewModel {
    public MediatorLiveData<String> name;
    public MediatorLiveData<String>  age;
    public MediatorLiveData<String>  id;

    public MediatorLiveData<String> getName() {
        if (name == null){
            name = new MediatorLiveData<>();
            name.setValue(""); //默认值
        }
        return name;
    }

    public MediatorLiveData<String> getAge() {
        if (age == null){
            age = new MediatorLiveData<>();
            age.setValue("");
        }
        return age;
    }

    public MediatorLiveData<String> getId() {
        if (id == null){
            id = new MediatorLiveData<>();
            id.setValue("");
        }
        return id;
    }
    public void add(String number){
         id.setValue(id.getValue() + number);
    }
}
