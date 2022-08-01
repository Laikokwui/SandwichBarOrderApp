package com.example.fragment_mysandwichbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FragmentOne.onFragmentInteractionListener, FragmentTwo.onFragmentInteractionListener {
    private FragmentOne fragmentOne = new FragmentOne();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_layout, fragmentOne)
                .commit();
    }

    @Override
    public void PassData(String command) {
        if (command.equals("reset")) {
            FragmentOne fragmentOne = FragmentOne.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_layout, fragmentOne)
                    .commit();
        }
        else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void PassData(int filling_count, double total_price, String order_message) {
        FragmentTwo fragmentTwo = FragmentTwo.newInstance(filling_count, total_price, order_message);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, fragmentTwo)
                .addToBackStack(null)
                .commit();
    }
}