package com.example.ecoscout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.ecoscout.databinding.EventMainBinding;

import java.util.ArrayList;

public class Event extends AppCompatActivity {

    EventMainBinding binding;
    EventListAdapter listAdapter;
    ArrayList<EventList> dataArrayList = new ArrayList<>();
    EventList listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EventMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int[] imageList = {R.drawable.bwc, R.drawable.ccd, R.drawable.mmrc, R.drawable.mtc, R.drawable.upc, R.drawable.mrc, R.drawable.bcm};
        int[] locationList = {R.string.bwcLocation, R.string.ccdLocation,R.string.mmrcLocation,R.string.mtcLocation,R.string.upcLocation, R.string.mrcLocation, R.string.bcmLocation};
        int[] descList = {R.string.bwcDesc, R.string.ccdDesc, R.string.mmrcDesc,R.string.mtcDesc,R.string.upcDesc, R.string.mrcDesc, R.string.bcmDesc};
        String[] nameList = {"Barangay-Wide Cleanup: Batangas City", "Coastal Cleanup Drive: Boracay Island", "Metro Manila River Cleanup", "Mountain Trails Cleanup: Mt. Pulag", "Urban Park Cleanup: Quezon Memorial Circle", "Mangrove Reforestation and Cleanup","Beach Cleanup Marathon: Siargao"};
        String[] dateList = {"January 20, 2024", "March 10, 2024", "May 15, 2024","July 9, 2024", "September 16, 2024", "February 24, 2025", "April 21, 2025"};

        for (int i = 0; i < imageList.length; i++){
            listData = new EventList(nameList[i], dateList[i], locationList[i], descList[i], imageList[i]);
            dataArrayList.add(listData);
        }
        listAdapter = new EventListAdapter(Event.this, dataArrayList);
        binding.listview.setAdapter(listAdapter);
        binding.listview.setClickable(true);

        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Event.this, EventDetailed.class);
                intent.putExtra("name", nameList[i]);
                intent.putExtra("date", dateList[i]);
                intent.putExtra("location", locationList[i]);
                intent.putExtra("desc", descList[i]);
                intent.putExtra("image", imageList[i]);
                startActivity(intent);
            }
        });
    }
}
