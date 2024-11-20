package com.example.ecoscout;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProperDisposal extends AppCompatActivity {

    private TextView wasteDisposalGuide, wasteSorting, localDisposalCenters;
    private ImageView wasteDisposalIcon, wasteSortingIcon, localCentersIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proper);

        // Initializing the TextViews and ImageViews
        wasteDisposalGuide = findViewById(R.id.wasteDisposalTitle);
        wasteSorting = findViewById(R.id.wasteSortingIcon);
        localDisposalCenters = findViewById(R.id.localDisposalDetails);
        wasteDisposalIcon = findViewById(R.id.wasteDisposalIcon);
        wasteSortingIcon = findViewById(R.id.wasteSortingIcon);
        localCentersIcon = findViewById(R.id.localDisposalIcon);

        // Setting the content
        wasteDisposalGuide.setText("Waste Disposal Guide:\n" +
                "Proper waste disposal is essential for a cleaner environment. Different types of waste need to be handled accordingly:\n" +
                "• Recycling: Paper, plastics, glass, and metals can be recycled and should be placed in the designated bins.\n" +
                "• Composting: Organic waste like food scraps and yard waste can be composted to reduce landfill waste.\n" +
                "• Hazardous Waste: Items like batteries, chemicals, and electronics require special disposal methods at certified centers.");

        wasteSorting.setText("Waste Sorting:\n" +
                "Sorting waste correctly helps in reducing pollution and ensuring recyclable materials are properly processed. The key steps to waste sorting include:\n" +
                "• Separate recyclables such as plastics, paper, and glass from general waste.\n" +
                "• Keep compostable materials like food scraps in a separate bin.\n" +
                "• Make sure hazardous waste is disposed of at specialized facilities.\n" +
                "Interactive tutorials or videos can help understand the process better. Learn how to do it correctly through the app!");

        localDisposalCenters.setText("Local Disposal Centers:\n" +
                "Finding local recycling and waste management centers is crucial for responsible disposal. You can find a list of nearby centers in your region. Some useful tips for local disposal include:\n" +
                "• Look for certified waste management centers that accept various types of waste.\n" +
                "• Some centers also offer free drop-offs for items like electronics and hazardous waste.\n" +
                "• Use the app to locate the nearest centers based on your region.");

        // Setting Icons (You need to have the icons in the drawable folder)
        wasteDisposalIcon.setImageResource(R.drawable.ic_waste_disposal);
        wasteSortingIcon.setImageResource(R.drawable.ic_waste_sorting);
        localCentersIcon.setImageResource(R.drawable.ic_local_centers);
    }
}
