package com.grosner.androiddatabaselibrarycomparison2.greendao;

import com.grosner.androiddatabaselibrarycomparison2.tests.IPlayer;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.jetbrains.annotations.NotNull;

@Entity
public class Player extends BaseModel implements IPlayer {

    @Id
    private String id = "";

    private String firstName = "";

    private String lastName = "";

    private int age = 0;

    private String position = "";

    @Generated(hash = 1169459112)
    public Player(String id, String firstName, String lastName, int age,
                  String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.position = position;
    }

    @Generated(hash = 30709322)
    public Player() {
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(@NotNull String s) {
        this.id = s;
    }

    @NotNull
    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(@NotNull String s) {
        this.firstName = s;
    }

    @NotNull
    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(@NotNull String s) {
        this.lastName = s;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int i) {
        this.age = i;
    }

    @NotNull
    @Override
    public String getPosition() {
        return position;
    }

    @Override
    public void setPosition(@NotNull String s) {
        this.position = s;
    }

}